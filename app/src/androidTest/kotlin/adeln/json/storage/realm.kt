package adeln.json.storage

import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.RealmQuery
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

fun LocalTweet.marshal(t: Tweet) {
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

inline fun <reified T : RealmModel> Realm.where(): RealmQuery<T> =
    where(T::class.java)
