package com.rbiggin.a2do2gether.model

data class ToDoListMap (val id: String, val title: String, val creator: String,
                         val items: HashMap<String, ToDoListItem>?)