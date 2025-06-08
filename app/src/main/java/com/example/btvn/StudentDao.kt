package com.example.btvn

import androidx.room.*

@Dao
@RewriteQueriesToDropUnusedColumns
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStudent(student: StudentModel): Long

    @Update
    suspend fun updateStudent(student: StudentModel): Int

    @Delete
    suspend fun deleteStudent(student: StudentModel): Int

    @Query("SELECT * FROM students")
    suspend fun getAllStudents(): List<StudentModel>
}