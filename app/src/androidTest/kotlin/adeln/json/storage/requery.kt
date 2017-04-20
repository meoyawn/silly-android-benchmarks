package adeln.json.storage

import android.content.ContentValues
import android.content.Context
import io.requery.android.database.sqlite.SQLiteDatabase
import io.requery.android.database.sqlite.SQLiteOpenHelper

class Requery(ctx: Context, fileName: String) : SQLiteOpenHelper(ctx, fileName, null, 1) {
    override fun onCreate(db: SQLiteDatabase): Unit =
        db.execSQL(SCHEMA)

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int): Unit =
        Unit

    override fun onOpen(db: SQLiteDatabase): Unit =
        db.setForeignKeyConstraintsEnabled(true)
}

class RequeryTx(
    val db: SQLiteDatabase,
    val values: ContentValues
)

inline fun <T> SQLiteDatabase.inTransaction(f: RequeryTx.() -> T): T {
    beginTransaction()

    return try {
        RequeryTx(this, ContentValues()).f().also {
            setTransactionSuccessful()
        }
    } finally {
        endTransaction()
    }
}

inline fun <T> RequeryTx.clean(marshal: ContentValues.() -> T): ContentValues =
    values.also {
        it.clear()
        it.marshal()
    }

inline fun <T> RequeryTx.insertOrReplace(table: String, marshal: ContentValues.() -> T): Long =
    db.insertWithOnConflict(table, null, clean(marshal), SQLiteDatabase.CONFLICT_REPLACE)

inline fun <T> RequeryTx.insertOrIgnore(table: String, marshal: ContentValues.() -> T): Long =
    db.insertWithOnConflict(table, null, clean(marshal), SQLiteDatabase.CONFLICT_IGNORE)

fun mkRequery(ctx: Context, fileName: String = "database.sqlite"): Requery =
    Requery(ctx, fileName)
