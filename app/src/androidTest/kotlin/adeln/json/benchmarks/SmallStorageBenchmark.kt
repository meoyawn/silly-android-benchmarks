package adeln.json.benchmarks

import adeln.json.storage.LocalTweet
import adeln.json.storage.forEach
import adeln.json.storage.inTransaction
import adeln.json.storage.insertOrReplace
import adeln.json.storage.marshal
import adeln.json.storage.mkRealm
import adeln.json.storage.mkSqlite
import adeln.json.storage.nextTweet
import android.content.Context
import android.database.DatabaseUtils.longForQuery
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import io.kotlintest.matchers.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Random

@RunWith(AndroidJUnit4::class)
class SmallStorageBenchmark {

    val total = 100_000L

    val ctx: Context = InstrumentationRegistry.getTargetContext()
    val rand = Random()
    val tweets = Array(total.toInt()) { rand.nextTweet() }

    @Before
    fun setup() {
        ctx.cacheDir.parentFile.deleteRecursively()
    }

    @Test
    fun sqlite() =
        mkSqlite(ctx).writableDatabase.use { db ->
            db.inTransaction {
                tweets.forEach {
                    insertOrReplace("tweets") { marshal(it) }
                }
            }

            longForQuery(db, "SELECT COUNT(*) FROM tweets", emptyArray()) shouldEqual total

            db.rawQuery("SELECT * FROM tweets", emptyArray()).use {
                it.forEach {
                    it.getString(1) + it.getString(2)
                }
            }
        }

    @Test
    fun realm() =
        mkRealm(ctx).use { db ->
            db.inTransaction {
                copyToRealmOrUpdate(tweets.map { LocalTweet().apply { marshal(it) } })
            }

            db.where(LocalTweet::class.java).count() shouldEqual total

            db.where(LocalTweet::class.java).findAll().forEach {
                it.createdAt + it.profileImageUrl
            }
        }
}
