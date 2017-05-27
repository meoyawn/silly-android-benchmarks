package adeln.json.benchmarks

import adeln.json.logGc
import adeln.json.storage.LocalTweet
import adeln.json.storage.RoomTweet
import adeln.json.storage.forEach
import adeln.json.storage.inTransaction
import adeln.json.storage.insertOrReplace
import adeln.json.storage.marshal
import adeln.json.storage.mkRequery
import adeln.json.storage.mkRoom
import adeln.json.storage.mkSqlite
import adeln.json.storage.nextTweet
import adeln.json.storage.where
import android.database.DatabaseUtils.longForQuery
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import io.kotlintest.matchers.shouldEqual
import io.realm.Realm
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Random

@RunWith(AndroidJUnit4::class)
class SmallStorageBenchmark {

    val ctx = InstrumentationRegistry.getTargetContext()!!
    val rand = Random()
    val tweets = Array(40_000) { rand.nextTweet() }

    @Before
    fun setup() {
        ctx.databaseList().forEach { ctx.deleteDatabase(it) }

        Realm.init(ctx)
        Realm.getDefaultInstance().use { it.inTransaction { deleteAll() } }
    }

    @Test
    fun sqlite() {
        mkSqlite(ctx).writableDatabase.use { db ->
            logGc("sqlite") {
                db.inTransaction {
                    tweets.forEach {
                        insertOrReplace("tweets") { marshal(it) }
                    }
                }

                longForQuery(db, "SELECT COUNT(*) FROM tweets", emptyArray()).toInt() shouldEqual tweets.size

                db.rawQuery("SELECT * FROM tweets", emptyArray()).use {
                    it.forEach {
                        it.getString(1) + it.getString(2)
                    }
                }
            }
        }
    }

    @Test
    fun realm() {
        Realm.getDefaultInstance().use { db ->
            logGc("realm") {
                db.inTransaction {
                    copyToRealmOrUpdate(tweets.map { LocalTweet().apply { marshal(it) } })
                }

                db.where<LocalTweet>().count().toInt() shouldEqual tweets.size

                db.where<LocalTweet>().findAll().forEach {
                    it.createdAt + it.profileImageUrl
                }
            }
        }
    }

    @Test
    fun requery() {
        mkRequery(ctx).writableDatabase.use { db ->
            logGc("requery") {
                db.inTransaction {
                    tweets.forEach {
                        insertOrReplace("tweets") { marshal(it) }
                    }
                }

                db.longForQuery("SELECT COUNT(*) FROM tweets", emptyArray()).toInt() shouldEqual tweets.size

                db.rawQuery("SELECT * FROM tweets", emptyArray()).use {
                    it.forEach {
                        it.getString(1) + it.getString(2)
                    }
                }
            }
        }
    }

    @Test
    fun room() {
        ctx.mkRoom().let { db ->
            logGc("room") {
                val dao = db.dao()

                dao.insert(tweets.map { RoomTweet().apply { marshal(it) } })

                dao.count().toInt() shouldEqual tweets.size

                dao.all().forEach {
                    it.createdAt + it.profileImageUrl
                }
            }

            db.close()
        }
    }
}
