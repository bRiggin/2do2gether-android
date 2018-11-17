package com.rbiggin.a2do2gether.model

data class ToDoListMap (val id: String, val title: String, val creator: String,
                         val items: HashMap<String, ToDoListItem>?){
    enum class DataBaseKeys(val key: String){
        ID("id"),
        TITLE("list_title"),
        CREATOR("creator"),
        ITEMS("items")
    }
}