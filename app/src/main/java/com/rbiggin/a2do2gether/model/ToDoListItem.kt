package com.rbiggin.a2do2gether.model

data class ToDoListItem(val id: String, val description: String, val creator: String,
                        val status: Boolean, val completedBy: String? = null,
                        val dateCreated: String, val priority: Priority) {

    enum class DataBaseKeys(val key: String) {
        ID("id"),
        CREATED("dateCreated"),
        DESCRIPTION("description"),
        CREATOR("creator"),
        STATUS("status"),
        COMPLETED_BY("completed_by"),
        PRIORITY("priority")
    }

    enum class Priority(val value: String) {
        CRITICAL("CRITICAL"),
        IMPORTANT("IMPORTANT"),
        TODO("TODO")
    }

    companion object {
        fun parseToDoListItem(item: HashMap<*, *>): ToDoListItem? {
            val id = item[DataBaseKeys.ID.key] as? String
            val created = item[DataBaseKeys.CREATED.key] as? String
            val creator = item[DataBaseKeys.CREATOR.key] as? String
            val description = item[DataBaseKeys.DESCRIPTION.key] as? String
            val status = item[DataBaseKeys.STATUS.key].toString().toBoolean()
            val completedBy: String? = item[DataBaseKeys.COMPLETED_BY.key] as String?
            val priority = parsePriority(item[DataBaseKeys.PRIORITY.key] as String?)

            return when {
                id is String
                        && creator is String
                        && description is String
                        && priority is ToDoListItem.Priority
                        && created is String ->
                    ToDoListItem(id, description, creator, status, completedBy, created, priority)
                else -> null
            }
        }

        private fun parsePriority(value: String?): Priority? {
            return when (value) {
                Priority.CRITICAL.value -> Priority.CRITICAL
                Priority.IMPORTANT.value -> Priority.IMPORTANT
                Priority.TODO.value -> Priority.TODO
                else -> null
            }
        }
    }
}