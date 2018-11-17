package com.rbiggin.a2do2gether.model

import java.lang.IllegalArgumentException

data class ToDoListItem (val description: String, val creator: String,
                         val status: Boolean, val completedBy: String? = null,
                         val priority: Priority){

    enum class DataBaseKeys(val key: String){
        DESCRIPTION("item_description"),
        CREATOR("creator"),
        STATUS("status"),
        COMPLETED_BY("completed_by"),
        PRIORITY("priority")
    }

    enum class Priority(val value: String){
        CRITICAL("critical"),
        IMPORTANT("important"),
        TODO("to_do")
    }

    companion object {
        fun parsePriority(value: String): Priority{
            return when(value) {
                "critical" -> {
                    Priority.CRITICAL
                }
                "important" -> {
                    Priority.IMPORTANT
                }
                "to_do" -> {
                    Priority.TODO
                }
                else -> {
                    throw IllegalArgumentException()
                }
            }
        }
    }
}