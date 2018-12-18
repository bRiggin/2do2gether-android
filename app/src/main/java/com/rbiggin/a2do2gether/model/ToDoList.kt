package com.rbiggin.a2do2gether.model

import com.google.firebase.database.DataSnapshot

data class ToDoList(val id: String, val title: String, val creator: String,
                    val items: HashMap<String, ToDoListItem>?) {
    enum class DataBaseKeys(val key: String) {
        ID("id"),
        TITLE("list_title"),
        CREATOR("creator"),
        ITEMS("items")
    }

    companion object {
        fun parseToDoList(snapshot: DataSnapshot): ToDoList? {
            val creator = snapshot.child(DataBaseKeys.CREATOR.key).value as? String
            val title = snapshot.child(DataBaseKeys.TITLE.key).value as? String
            val items: HashMap<String, ToDoListItem> = HashMap()

            val itemsMap = snapshot.child(DataBaseKeys.ITEMS.key).value

            if (itemsMap is HashMap<*, *>) {
                itemsMap.forEach { item ->
                    (item.value as? HashMap<String, *>)?.let { map ->
                        (item.key as? String)?.let { key ->
                            ToDoListItem.parseToDoListItem(map)?.let { items[key] = it }
                        }
                    }
                }
            }
            return when {
                creator is String && title is String ->
                    ToDoList(snapshot.key.toString(), title, creator, items)
                else -> null
            }
        }
    }
}