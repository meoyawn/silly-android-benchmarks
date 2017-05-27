package adeln.json.storage

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Database
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Insert
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.Query
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.database.sqlite.SQLiteDatabase

@Entity(tableName = "tweets")
class RoomTweet {
    @PrimaryKey var id: Long = -1
    var createdAt: String = ""
    var profileImageUrl: String = ""
    var userName: String = ""
    var screenName: String = ""
    var text: String = ""
    var retweetCount: Int = -1
    var favoriteCount: Int = -1
    var currentUserRetweet: Boolean = false
    var whoRetweeted: String? = null
}

@Dao
interface RoomDao {

    @Insert(onConflict = SQLiteDatabase.CONFLICT_REPLACE)
    fun insert(ts: List<RoomTweet>)

    @Query("SELECT count(*) FROM tweets")
    fun count(): Long

    @Query("SELECT * from tweets")
    fun all(): List<RoomTweet>
}

@Database(
    entities = arrayOf(
        RoomTweet::class
    ),
    version = 1
)
abstract class RoomDb : RoomDatabase() {
    abstract fun dao(): RoomDao
}

fun Context.mkRoom(): RoomDb =
    Room.databaseBuilder(this, RoomDb::class.java, "room")
        .build()

fun RoomTweet.marshal(t: Tweet) {
    id = t.id
    whoRetweeted = null

    val u = t.user
    createdAt = t.createdAt
    profileImageUrl = u.profileImageUrl
    userName = u.name
    screenName = u.screenName

    text = t.text
    retweetCount = t.retweetCount
    favoriteCount = t.favoriteCount
    currentUserRetweet = t.currentUserRetweet != null
}
