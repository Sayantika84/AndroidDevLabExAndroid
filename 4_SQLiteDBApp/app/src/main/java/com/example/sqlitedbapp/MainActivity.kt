package com.example.sqlitedbapp

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import android.widget.*

class MainActivity : Activity() {

    private lateinit var myDb: DatabaseHelper
    private lateinit var editRollNo: EditText
    private lateinit var editName: EditText
    private lateinit var editMarks: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Database Helper
        myDb = DatabaseHelper(this)

        // Setup Programmatic UI
        setupUI()
    }

    private fun setupUI() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val scrollView = ScrollView(this)
        scrollView.addView(layout)

        // Create Input Fields
        editRollNo = EditText(this).apply { hint = "Roll Number (For Search/Update/Delete)" ; inputType = InputType.TYPE_CLASS_NUMBER }
        editName = EditText(this).apply { hint = "Student Name" }
        editMarks = EditText(this).apply { hint = "Marks" ; inputType = InputType.TYPE_CLASS_NUMBER }

        layout.addView(editRollNo)
        layout.addView(editName)
        layout.addView(editMarks)

        // Create Buttons
        val btnAdd = Button(this).apply { text = "Add Record" }
        val btnViewAll = Button(this).apply { text = "View All Records" }
        val btnSearch = Button(this).apply { text = "Search by Roll No" }
        val btnUpdate = Button(this).apply { text = "Update Record" }
        val btnDelete = Button(this).apply { text = "Delete Record" }

        layout.addView(btnAdd)
        layout.addView(btnViewAll)
        layout.addView(btnSearch)
        layout.addView(btnUpdate)
        layout.addView(btnDelete)

        setContentView(scrollView)

        // --- Button Click Listeners ---

        // 1. INSERT
        btnAdd.setOnClickListener {
            val isInserted = myDb.insertData(editRollNo.text.toString(), editName.text.toString(), editMarks.text.toString())
            if (isInserted) showMessage("Success", "Record Added")
            else showMessage("Error", "Record Not Added. Roll No might already exist.")
            clearInputs()
        }

        // 2. VIEW ALL
        btnViewAll.setOnClickListener {
            val res = myDb.getAllData()
            if (res.count == 0) {
                showMessage("Error", "No records found")
                return@setOnClickListener
            }
            val buffer = StringBuffer()
            while (res.moveToNext()) {
                buffer.append("Roll No: ${res.getString(0)}\n")
                buffer.append("Name: ${res.getString(1)}\n")
                buffer.append("Marks: ${res.getString(2)}\n\n")
            }
            showMessage("Student Records", buffer.toString())
        }

        // 3. SEARCH
        btnSearch.setOnClickListener {
            val rollNo = editRollNo.text.toString()
            if (rollNo.isEmpty()) {
                showMessage("Error", "Please enter Roll Number to search")
                return@setOnClickListener
            }
            val res = myDb.getData(rollNo)
            if (res.count == 0) {
                showMessage("Error", "No record found for Roll No: $rollNo")
                return@setOnClickListener
            }
            res.moveToFirst()
            editName.setText(res.getString(1))
            editMarks.setText(res.getString(2))
            showMessage("Success", "Record Found")
        }

        // 4. UPDATE
        btnUpdate.setOnClickListener {
            val isUpdated = myDb.updateData(editRollNo.text.toString(), editName.text.toString(), editMarks.text.toString())
            if (isUpdated) showMessage("Success", "Record Updated")
            else showMessage("Error", "Record Not Updated. Check Roll No.")
            clearInputs()
        }

        // 5. DELETE
        btnDelete.setOnClickListener {
            val deletedRows = myDb.deleteData(editRollNo.text.toString())
            if (deletedRows > 0) showMessage("Success", "Record Deleted")
            else showMessage("Error", "Record Not Deleted. Check Roll No.")
            clearInputs()
        }
    }

    private fun clearInputs() {
        editRollNo.text.clear()
        editName.text.clear()
        editMarks.text.clear()
    }

    private fun showMessage(title: String, message: String) {
        AlertDialog.Builder(this)
            .setCancelable(true)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}

/**
 * SQLiteOpenHelper class to manage database creation and version management.
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    companion object {
        const val DATABASE_NAME = "Student.db"
        const val TABLE_NAME = "student_table"
        const val COL_1 = "ROLL_NO"
        const val COL_2 = "NAME"
        const val COL_3 = "MARKS"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $TABLE_NAME (ROLL_NO TEXT PRIMARY KEY, NAME TEXT, MARKS TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertData(rollNo: String, name: String, marks: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COL_1, rollNo)
            put(COL_2, name)
            put(COL_3, marks)
        }
        val result = db.insert(TABLE_NAME, null, contentValues)
        return result != -1L // returns -1 if error
    }

    fun getAllData(): Cursor {
        val db = this.writableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    fun getData(rollNo: String): Cursor {
        val db = this.writableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE ROLL_NO = ?", arrayOf(rollNo))
    }

    fun updateData(rollNo: String, name: String, marks: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COL_1, rollNo)
            put(COL_2, name)
            put(COL_3, marks)
        }
        db.update(TABLE_NAME, contentValues, "ROLL_NO = ?", arrayOf(rollNo))
        return true
    }

    fun deleteData(rollNo: String): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "ROLL_NO = ?", arrayOf(rollNo))
    }
}