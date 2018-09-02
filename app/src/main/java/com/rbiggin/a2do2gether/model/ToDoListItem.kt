package com.rbiggin.a2do2gether.model

data class ToDoListItem (val description: String, val creator: String,
                         val status: Boolean, val completedBy: String?)