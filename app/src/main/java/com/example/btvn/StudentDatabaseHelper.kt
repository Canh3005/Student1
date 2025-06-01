package com.example.btvn

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class StudentDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "students.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "students"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PHONE = "phone"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_NAME TEXT,
                $COLUMN_EMAIL TEXT,
                $COLUMN_PHONE TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addStudent(student: StudentModel): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, student.id)
            put(COLUMN_NAME, student.name)
            put(COLUMN_EMAIL, student.email)
            put(COLUMN_PHONE, student.SĐT)
        }
        val result = db.insert(TABLE_NAME, null, values)
        db.close()
        return result != -1L
    }

    fun updateStudent(student: StudentModel): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, student.name)
            put(COLUMN_EMAIL, student.email)
            put(COLUMN_PHONE, student.SĐT)
        }
        val result = db.update(
            TABLE_NAME,
            values,
            "$COLUMN_ID = ?",
            arrayOf(student.id)
        )
        db.close()
        return result > 0
    }

    fun deleteStudent(id: String): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id))
        db.close()
        return result > 0
    }

    fun getAllStudents(): MutableList<StudentModel> {
        val studentList = mutableListOf<StudentModel>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE))
                studentList.add(StudentModel(name, id, email, phone))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return studentList
    }
}
