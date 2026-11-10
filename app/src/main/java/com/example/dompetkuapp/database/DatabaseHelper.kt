package com.example.dompetkuapp.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "DompetKu.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_TRANSACTIONS = "transactions"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = ("CREATE TABLE $TABLE_TRANSACTIONS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_TITLE TEXT, "
                + "$COLUMN_AMOUNT REAL, "
                + "$COLUMN_TYPE TEXT, "
                + "$COLUMN_CATEGORY TEXT, "
                + "$COLUMN_DATE TEXT)")
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        onCreate(db)
    }

    fun insertTransaction(transaction: TransactionModel): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, transaction.title)
            put(COLUMN_AMOUNT, transaction.amount)
            put(COLUMN_TYPE, transaction.type)
            put(COLUMN_CATEGORY, transaction.category)
            put(COLUMN_DATE, transaction.date)
        }
        val id = db.insert(TABLE_TRANSACTIONS, null, values)
        db.close()
        return id
    }

    fun getAllTransactions(): List<TransactionModel> {
        val transactionList = mutableListOf<TransactionModel>()
        val selectQuery = "SELECT * FROM $TABLE_TRANSACTIONS ORDER BY $COLUMN_ID DESC"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
                val type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))

                transactionList.add(TransactionModel(id, title, amount, type, category, date))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return transactionList
    }

    fun updateTransaction(transaction: TransactionModel): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, transaction.title)
            put(COLUMN_AMOUNT, transaction.amount)
            put(COLUMN_TYPE, transaction.type)
            put(COLUMN_CATEGORY, transaction.category)
            put(COLUMN_DATE, transaction.date)
        }
        val rows = db.update(TABLE_TRANSACTIONS, values, "$COLUMN_ID = ?", arrayOf(transaction.id.toString()))
        db.close()
        return rows
    }

    fun deleteTransaction(id: Int): Int {
        val db = this.writableDatabase
        val rows = db.delete(TABLE_TRANSACTIONS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return rows
    }

    fun getTotalIncome(): Double {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_TRANSACTIONS WHERE $COLUMN_TYPE = 'PEMASUKAN'", null)
        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        db.close()
        return total
    }

    fun getTotalExpense(): Double {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_TRANSACTIONS WHERE $COLUMN_TYPE = 'PENGELUARAN'", null)
        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        db.close()
        return total
    }
}
