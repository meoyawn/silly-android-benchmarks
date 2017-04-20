package adeln.json.storage

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

val SCHEMA = """
CREATE TABLE tweets (
  id                   INTEGER PRIMARY KEY,
  created_at           TEXT    NOT NULL,
  profile_image_url    TEXT    NOT NULL,
  user_name            TEXT    NOT NULL,
  screen_name          TEXT    NOT NULL,
  text                 TEXT    NOT NULL,
  retweet_count        INTEGER NOT NULL,
  favourite_count      INTEGER NOT NULL,
  current_user_retweet INT     NOT NULL,
  who_retweeted        TEXT
)
"""

fun ContentValues.marshal(t: Tweet) {
    put("id", t.id)
    putNull("who_retweeted")

    val t = t
    put("created_at", t.createdAt)

    val u = t.user
    put("profile_image_url", u.profileImageUrl)
    put("user_name", u.name)
    put("screen_name", u.screenName)

    put("text", t.text)
    put("retweet_count", t.retweetCount)
    put("favourite_count", t.favoriteCount)
    put("current_user_retweet", t.currentUserRetweet != null)
}

class Database(ctx: Context, fileName: String) : SQLiteOpenHelper(ctx, fileName, null, 1) {
    override fun onCreate(db: SQLiteDatabase): Unit =
        db.execSQL(SCHEMA)

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int): Unit =
        Unit

    override fun onOpen(db: SQLiteDatabase): Unit =
        db.setForeignKeyConstraintsEnabled(true)
}

class Transaction(
    val db: SQLiteDatabase,
    val values: ContentValues
)

inline fun <T> SQLiteDatabase.inTransaction(f: Transaction.() -> T): T {
    beginTransaction()

    return try {
        Transaction(this, ContentValues()).f().also {
            setTransactionSuccessful()
        }
    } finally {
        endTransaction()
    }
}

inline fun <T> Transaction.clean(marshal: ContentValues.() -> T): ContentValues =
    values.also {
        it.clear()
        it.marshal()
    }

inline fun <T> Transaction.insertOrReplace(table: String, marshal: ContentValues.() -> T): Long =
    db.insertWithOnConflict(table, null, clean(marshal), SQLiteDatabase.CONFLICT_REPLACE)

inline fun <T> Transaction.insertOrIgnore(table: String, marshal: ContentValues.() -> T): Long =
    db.insertWithOnConflict(table, null, clean(marshal), SQLiteDatabase.CONFLICT_IGNORE)

fun mkSqlite(ctx: Context, fileName: String = "database.sqlite"): Database =
    Database(ctx, fileName)

fun Cursor.forEach(f: (Cursor) -> Unit): Unit =
    if (moveToFirst()) {
        do {
            f(this)
        } while (moveToNext())
    } else Unit
