package adeln.json.storage

import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey

inline fun <T> Realm.inTransaction(f: Realm.() -> T): T {
    beginTransaction()

    return try {
        f().also {
            commitTransaction()
        }
    } catch(e: Exception) {
        if (isInTransaction) {
            cancelTransaction()
        }

        throw e
    }
}

open class LocalTweet : RealmObject() {
    @PrimaryKey @Index var id: Long = -1
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

fun LocalTweet.marshal(x: Tweet) {
    id = x.id
    whoRetweeted = x.retweetedStatus?.let { x.user.name }
    val t = x.retweetedStatus ?: x

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

fun mkRealm(ctx: Context, fileName: String = "realm.realm"): Realm {
    Realm.init(ctx)

    val config = RealmConfiguration.Builder()
        .name(fileName)
        .build()

    return Realm.getInstance(config)
}
