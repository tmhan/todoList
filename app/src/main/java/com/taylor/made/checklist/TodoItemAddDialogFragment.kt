package com.taylor.made.checklist

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import java.util.*

class TodoItemAddDialog(context: Context) : Dialog(context) {

    private lateinit var title: EditText
    private lateinit var content: EditText
    private lateinit var tvCancel: TextView
    private lateinit var tvConfirm: TextView
    private lateinit var datePicker: TextView
    var cal = Calendar.getInstance()

    override fun show() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_todo_item_add, null)
        setContentView(view)
        title = view.findViewById(R.id.todo_title)
        content = view.findViewById(R.id.todo_desc)
        tvCancel = view.findViewById(R.id.tvCancel)
        tvConfirm = view.findViewById(R.id.tvConfirm)
        datePicker = view.findViewById(R.id.addDateView)

        window?.apply {
            setBackgroundDrawableResource(R.drawable.transparent_background)

            val lp = WindowManager.LayoutParams()
            lp.copyFrom(attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            attributes = lp

            setGravity(Gravity.BOTTOM)
            attributes.windowAnimations = R.style.DialogAnimation
        }

        initView()
        super.show()
    }

    val dateSetListener = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(
            view: DatePicker, year: Int, monthOfYear: Int,
            dayOfMonth: Int
        ) {
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            datePicker.text = ("$year-${monthOfYear+1}-$dayOfMonth")
        }
    }

    fun initView() {

        val cancelListener = {
            dismiss()
        }

        setOnCancelListener {
            cancelListener.invoke()
        }

        tvCancel.setOnClickListener {
            cancelListener.invoke()
        }

        tvConfirm.setOnClickListener {
            val helper =  DBHelper(context)
            val db = helper.writableDatabase
            val contentValues = ContentValues()
            contentValues.put("title", title.text.toString())
            contentValues.put("content", content.text.toString())
            contentValues.put("date", datePicker.text.toString())
            contentValues.put("completed", 0)

            db.insert("tb_todo", null, contentValues)
            db.close()

            dismiss()
        }

        datePicker.setOnClickListener {
            DatePickerDialog(context,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).apply {
                datePicker.minDate = System.currentTimeMillis()
            }.show()
        }
    }
}