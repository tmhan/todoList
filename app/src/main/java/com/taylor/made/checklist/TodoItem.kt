package com.taylor.made.checklist

abstract class TodoItem {
    abstract val type: Int
    companion object {
        val TYPE_HEADER = 0
        val TYPE_DATA = 1
    }
}

class HeaderItem(var date: String) : TodoItem() {
    override val type: Int
        get() = TodoItem.TYPE_HEADER
}

internal class DataItem(var id: Int, var title: String, var content: String, var completed: Boolean = false) : TodoItem() {
    override val type: Int
        get() = TodoItem.TYPE_DATA
}