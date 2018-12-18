package com.rbiggin.a2do2gether.model

data class ToDoListItem(val description: String, val creator: String,
                        val status: Boolean, val completedBy: String? = null,
                        val priority: Priority) {

    enum class DataBaseKeys(val key: String) {
        DESCRIPTION("description"),
        CREATOR("creator"),
        STATUS("status"),
        COMPLETED_BY("completed_by"),
        PRIORITY("priority")
    }

    enum class Priority(val value: String) {
        CRITICAL("critical"),
        IMPORTANT("important"),
        TODO("to_do")
    }

    companion object {

        fun parseToDoListItem(item: HashMap<*, *>): ToDoListItem? {
            val creator = item[DataBaseKeys.CREATOR.key] as? String
            val description = item[DataBaseKeys.DESCRIPTION.key] as? String
            val status = item[DataBaseKeys.STATUS.key].toString().toBoolean()
            val completedBy: String? = item[DataBaseKeys.COMPLETED_BY.key] as String?
            val priority = parsePriority(item[DataBaseKeys.PRIORITY.key] as String?)

            return when {
                creator is String && description is String && priority is ToDoListItem.Priority ->
                    ToDoListItem(description, creator, status, completedBy, priority)
                else -> null
            }
        }

        private fun parsePriority(value: String?): Priority? {
            return when (value) {
                "critical" -> Priority.CRITICAL
                "important" -> Priority.IMPORTANT
                "to_do" -> Priority.TODO
                else -> null
            }
        }
    }
}