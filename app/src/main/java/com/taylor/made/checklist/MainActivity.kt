package com.taylor.made.checklist

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_header.view.*
import kotlinx.android.synthetic.main.item_main.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {
    var list: MutableList<TodoItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectDB()

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            val dialog = TodoItemAddDialog(this)
            dialog.setOnDismissListener {
                selectDB()
            }
            dialog.show()
        }
    }

    private fun selectDB() {
        list = mutableListOf()
        val helper = DBHelper(this)
        val db = helper.readableDatabase
        val cursor = db.rawQuery("select * from tb_todo order by date desc", null)

        var preDate: Calendar? = null
        while (cursor.moveToNext()) {
            val dbdate = cursor.getString(3)
            val currentDate = GregorianCalendar()
            val date = if (dbdate == getString(R.string.immediately))  {
                SimpleDateFormat("yyyy-MM-dd").parse("${currentDate.get(Calendar.YEAR)}-${currentDate.get(Calendar.MONTH)+1}-${currentDate.get(Calendar.DAY_OF_MONTH)}")
            } else {
                SimpleDateFormat("yyyy-MM-dd").parse(dbdate)
            }
            currentDate.time = date

            if (!currentDate.equals(preDate)) {
                val headerItem = HeaderItem(dbdate)
                list.add(headerItem)
                preDate = currentDate
            }

            val completed = cursor.getInt(4) != 0
            val dataItem = DataItem(cursor.getInt(0), cursor.getString(1), cursor.getString(2), completed)
            list.add(dataItem)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MyAdapter(list)
        recyclerView.addItemDecoration(MyDecoration())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
            selectDB()
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headerView = view.itemHeaderView
    }

    class DataViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemTitleView = view.itemTitleView
        val itemContentView = view.itemContentView
    }

    inner class MyAdapter(val list: MutableList<TodoItem>) :
            RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun getItemViewType(position: Int): Int {
            return list[position].type
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            if (viewType == TodoItem.TYPE_HEADER) {
                val layoutInflater = LayoutInflater.from(parent?.context)
                return HeaderViewHolder(layoutInflater.inflate(R.layout.item_header, parent, false))
            } else {
                val layoutInflater = LayoutInflater.from(parent?.context)
                return DataViewHolder(layoutInflater.inflate(R.layout.item_main, parent, false))
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val checkItem = list[position]

            if (checkItem.type == TodoItem.TYPE_HEADER) {
                val viewHolder = holder as HeaderViewHolder
                val headerItem = checkItem as HeaderItem
                viewHolder.headerView.setText(headerItem.date)
            } else {
                val viewHolder = holder as DataViewHolder
                val dataItem = checkItem as DataItem
                viewHolder.itemTitleView.setText(dataItem.title)
                viewHolder.itemContentView.setText(dataItem.content)
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }


    }

    inner class MyDecoration() : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            val index = parent!!.getChildAdapterPosition(view)
            val todoItem = list.get(index)
            if (todoItem.type == TodoItem.TYPE_DATA) {
                view!!.setBackgroundColor(0xFFFFFFFF.toInt())
                ViewCompat.setElevation(view, 10.0f)
            }
            outRect!!.set(20, 10, 20, 10)
        }
    }
}