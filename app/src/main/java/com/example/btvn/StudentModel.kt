package com.example.btvn

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentModel(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val SĐT: String
)