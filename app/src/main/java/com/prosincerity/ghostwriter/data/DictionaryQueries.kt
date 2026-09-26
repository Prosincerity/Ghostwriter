package com.prosincerity.ghostwriter.data

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

/** Reads on the caller's IO dispatcher and always closes the cursor, including on cancellation. */
internal suspend fun SQLiteDatabase.readDictionaryRows(
    sql: String,
    args: Array<String>,
    onRow: (Cursor) -> Unit,
): Int {
    coroutineContext.ensureActive()
    var rows = 0
    rawQuery(sql, args).use { cursor ->
        while (true) {
            coroutineContext.ensureActive()
            if (!cursor.moveToNext()) break
            onRow(cursor)
            rows++
        }
    }
    return rows
}
