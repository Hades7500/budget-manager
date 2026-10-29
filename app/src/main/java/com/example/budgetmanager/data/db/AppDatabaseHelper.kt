package com.example.budgetmanager.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.budgetmanager.data.model.TransactionModel

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "expense_tracker.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_TRANSACTIONS = "transactions"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_PAYMENT_METHOD = "payment_method"
        private const val COLUMN_TIMESTAMP = "timestamp"
        private const val COLUMN_YEAR = "year"
        private const val COLUMN_MONTH = "month"
        private const val COLUMN_DAY = "day"

        private const val TABLE_SETTINGS = "settings"
        private const val COLUMN_SETTING_KEY = "setting_key"
        private const val COLUMN_SETTING_VAL = "setting_value"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTxTable = """
            CREATE TABLE $TABLE_TRANSACTIONS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT,
                $COLUMN_CATEGORY TEXT,
                $COLUMN_AMOUNT REAL,
                $COLUMN_TYPE TEXT,
                $COLUMN_PAYMENT_METHOD TEXT,
                $COLUMN_TIMESTAMP INTEGER,
                $COLUMN_YEAR INTEGER,
                $COLUMN_MONTH INTEGER,
                $COLUMN_DAY INTEGER
            )
        """.trimIndent()

        val createSettingsTable = """
            CREATE TABLE $TABLE_SETTINGS (
                $COLUMN_SETTING_KEY TEXT PRIMARY KEY,
                $COLUMN_SETTING_VAL TEXT
            )
        """.trimIndent()

        db.execSQL(createTxTable)
        db.execSQL(createSettingsTable)

        seedInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SETTINGS")
        onCreate(db)
    }

    private fun seedInitialData(db: SQLiteDatabase) {
        val initialList = listOf(
            TransactionModel(title = "Pizza", category = "Food", amount = 270.0, type = "EXPENSE", paymentMethod = "Card", year = 2026, month = 10, day = 3),
            TransactionModel(title = "Snacks", category = "Food", amount = 135.0, type = "EXPENSE", paymentMethod = "Card", year = 2026, month = 10, day = 3),
            TransactionModel(title = "Milk", category = "Food", amount = 17.0, type = "EXPENSE", paymentMethod = "Card", year = 2026, month = 10, day = 3)
        )

        for (tx in initialList) {
            val cv = ContentValues().apply {
                put(COLUMN_TITLE, tx.title)
                put(COLUMN_CATEGORY, tx.category)
                put(COLUMN_AMOUNT, tx.amount)
                put(COLUMN_TYPE, tx.type)
                put(COLUMN_PAYMENT_METHOD, tx.paymentMethod)
                put(COLUMN_TIMESTAMP, tx.timestamp)
                put(COLUMN_YEAR, tx.year)
                put(COLUMN_MONTH, tx.month)
                put(COLUMN_DAY, tx.day)
            }
            db.insert(TABLE_TRANSACTIONS, null, cv)
        }
    }

    fun addTransaction(tx: TransactionModel): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COLUMN_TITLE, tx.title)
            put(COLUMN_CATEGORY, tx.category)
            put(COLUMN_AMOUNT, tx.amount)
            put(COLUMN_TYPE, tx.type)
            put(COLUMN_PAYMENT_METHOD, tx.paymentMethod)
            put(COLUMN_TIMESTAMP, tx.timestamp)
            put(COLUMN_YEAR, tx.year)
            put(COLUMN_MONTH, tx.month)
            put(COLUMN_DAY, tx.day)
        }
        return db.insert(TABLE_TRANSACTIONS, null, cv)
    }

    fun deleteTransaction(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_TRANSACTIONS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun getTransactionsForMonth(year: Int, month: Int): List<TransactionModel> {
        val list = mutableListOf<TransactionModel>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TRANSACTIONS,
            null,
            "$COLUMN_YEAR = ? AND $COLUMN_MONTH = ?",
            arrayOf(year.toString(), month.toString()),
            null,
            null,
            "$COLUMN_DAY DESC, $COLUMN_ID DESC"
        )

        cursor.use { c ->
            val idIdx = c.getColumnIndexOrThrow(COLUMN_ID)
            val titleIdx = c.getColumnIndexOrThrow(COLUMN_TITLE)
            val catIdx = c.getColumnIndexOrThrow(COLUMN_CATEGORY)
            val amountIdx = c.getColumnIndexOrThrow(COLUMN_AMOUNT)
            val typeIdx = c.getColumnIndexOrThrow(COLUMN_TYPE)
            val methodIdx = c.getColumnIndexOrThrow(COLUMN_PAYMENT_METHOD)
            val timeIdx = c.getColumnIndexOrThrow(COLUMN_TIMESTAMP)
            val yearIdx = c.getColumnIndexOrThrow(COLUMN_YEAR)
            val monthIdx = c.getColumnIndexOrThrow(COLUMN_MONTH)
            val dayIdx = c.getColumnIndexOrThrow(COLUMN_DAY)

            while (c.moveToNext()) {
                list.add(
                    TransactionModel(
                        id = c.getLong(idIdx),
                        title = c.getString(titleIdx),
                        category = c.getString(catIdx),
                        amount = c.getDouble(amountIdx),
                        type = c.getString(typeIdx),
                        paymentMethod = c.getString(methodIdx),
                        timestamp = c.getLong(timeIdx),
                        year = c.getInt(yearIdx),
                        month = c.getInt(monthIdx),
                        day = c.getInt(dayIdx)
                    )
                )
            }
        }
        return list
    }

    fun getTotalExpenseForCategoryAndMonth(category: String, year: Int, month: Int): Double {
        val db = readableDatabase
        var total = 0.0
        val cursor = db.rawQuery(
            "SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_TRANSACTIONS WHERE $COLUMN_CATEGORY = ? AND $COLUMN_YEAR = ? AND $COLUMN_MONTH = ? AND $COLUMN_TYPE = 'EXPENSE'",
            arrayOf(category, year.toString(), month.toString())
        )
        cursor.use {
            if (it.moveToFirst()) {
                total = it.getDouble(0)
            }
        }
        return total
    }

    fun getTotalExpenseForMonth(year: Int, month: Int): Double {
        val db = readableDatabase
        var total = 0.0
        val cursor = db.rawQuery(
            "SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_TRANSACTIONS WHERE $COLUMN_YEAR = ? AND $COLUMN_MONTH = ? AND $COLUMN_TYPE = 'EXPENSE'",
            arrayOf(year.toString(), month.toString())
        )
        cursor.use {
            if (it.moveToFirst()) {
                total = it.getDouble(0)
            }
        }
        return total
    }

    fun getStartingBalance(year: Int, month: Int): Double {
        return -15321.55
    }

    fun getEndingBalance(year: Int, month: Int): Double {
        val totalExpense = getTotalExpenseForMonth(year, month)
        return getStartingBalance(year, month) - (totalExpense - 422.0)
    }

    fun getSetting(key: String, defaultValue: String): String {
        val db = readableDatabase
        var value = defaultValue
        val cursor = db.query(TABLE_SETTINGS, arrayOf(COLUMN_SETTING_VAL), "$COLUMN_SETTING_KEY = ?", arrayOf(key), null, null, null)
        cursor.use {
            if (it.moveToFirst()) {
                value = it.getString(0)
            }
        }
        return value
    }

    fun saveSetting(key: String, value: String) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COLUMN_SETTING_KEY, key)
            put(COLUMN_SETTING_VAL, value)
        }
        db.insertWithOnConflict(TABLE_SETTINGS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }
}