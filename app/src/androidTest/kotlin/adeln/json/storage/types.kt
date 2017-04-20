package adeln.json.storage

import java.util.Random
import java.util.UUID

fun nextString(): String =
    UUID.randomUUID().toString()

data class User(
    val name: String,
    val profileImageUrl: String,
    val screenName: String
)

fun nextUser(): User =
    User(
        name = nextString(),
        profileImageUrl = nextString(),
        screenName = nextString()
    )

data class Tweet(
    val id: Long,
    val user: User,
    val createdAt: String,
    val text: String,
    val retweetCount: Int,
    val favoriteCount: Int,
    val currentUserRetweet: Unit?
)

fun Random.nextTweet(): Tweet =
    Tweet(
        id = nextLong(),
        user = nextUser(),
        createdAt = nextString(),
        text = nextString(),
        retweetCount = nextInt(),
        favoriteCount = nextInt(),
        currentUserRetweet = if (nextBoolean()) Unit else null
    )
