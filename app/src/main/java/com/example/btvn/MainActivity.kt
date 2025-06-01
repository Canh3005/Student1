package com.example.btvn

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    lateinit var studentAdapter: StudentAdapter
    lateinit var dbHelper: StudentDatabaseHelper
    private  var studentList= mutableListOf<StudentModel>()

    private val formLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            if (data != null) {
                val name = data.getStringExtra("name") ?: ""
                val id = data.getStringExtra("id") ?: ""
                val email = data.getStringExtra("email") ?: ""
                val phone = data.getStringExtra("phone") ?: ""
                val isEditMode = data.getBooleanExtra("edit_mode", false)
                val position = data.getIntExtra("position", -1)
                val student = StudentModel(name, id, email, phone)

                if (isEditMode && position != -1) {

                    dbHelper.updateStudent(student)
                    studentList[position] = student
                    Toast.makeText(this, "Đã cập nhật sinh viên", Toast.LENGTH_SHORT).show()
                } else {
                    if (dbHelper.addStudent(student)) {
                        studentList.add(student)
                        Toast.makeText(this, "Đã thêm sinh viên mới", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show()
                    }

                }

                studentAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        dbHelper = StudentDatabaseHelper(this)
        dbHelper.readableDatabase // Mở DB để đảm bảo hiển thị trong Inspector
        studentList = dbHelper.getAllStudents() // lấy danh sách từ SQLite
        studentAdapter = StudentAdapter(this, studentList)
        val listView = findViewById<ListView>(R.id.lvSV)
        listView.adapter = studentAdapter
        registerForContextMenu(listView)


        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }


    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu, menu)
    }
    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val position = info.position
        val selectedStudent = studentList[position]

        when (item.itemId) {
            (R.id.menu_edit) -> {
                val intent = Intent(this, StudentActivity::class.java)
                intent.putExtra("edit_mode", true)
                intent.putExtra("name", selectedStudent.name)
                intent.putExtra("id", selectedStudent.id)
                intent.putExtra("email", selectedStudent.email)
                intent.putExtra("phone", selectedStudent.SĐT)
                intent.putExtra("position", position)
                formLauncher.launch(intent)
                return true
            }
            (R.id.menu_delete) -> {
                val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                builder.setTitle("Xác nhận xóa")
                builder.setMessage("Bạn có chắc chắn muốn xóa sinh viên ${selectedStudent.name}?")

                builder.setPositiveButton("Xóa") { dialog, _ ->

                    dbHelper.deleteStudent(selectedStudent.id)
                    studentAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "Đã xóa ${selectedStudent.name}", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }


                builder.setNegativeButton("Hủy") { dialog, _ ->
                    dialog.dismiss()
                }

                builder.create().show()
                return true
            }
            (R.id.menu_mail) -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "message/rfc822" // định dạng email
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(selectedStudent.email)) // người nhận
                intent.putExtra(Intent.EXTRA_SUBJECT, "Thư gửi ${selectedStudent.name}") // tiêu đề

                try {
                    startActivity(Intent.createChooser(intent, "Gửi email qua..."))
                } catch (e: Exception) {
                    Toast.makeText(this, "Không tìm thấy ứng dụng email", Toast.LENGTH_SHORT).show()
                }
                return true
            }
            (R.id.menu_call) -> {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${selectedStudent.SĐT}")

                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Không thể thực hiện cuộc gọi", Toast.LENGTH_SHORT).show()
                }
                return true
            }
        }

        return super.onContextItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnuAddSV -> {   val intent = Intent(this, StudentActivity::class.java)
                intent.putExtra("edit_mode", false)
                formLauncher.launch(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)

    }

}