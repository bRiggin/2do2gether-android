package com.rbiggin.a2do2gether.utils

class Constants {

    companion object {
        /**
         * Generic constant values
         */
        const val DIALOG_DISMISS_TIME: Int = 1500
        const val NUMBER_OF_LOGIN_FRAGMENTS: Int = 3
        const val NUMBER_OF_CHARACTERS_IN_NICKNAME: Int = 8

        /**
         * General keys
         */
        const val FRAGMENT_ID: String = "fragment_id"
        const val LOAD_FRAGMENT: String = "load_fragment"
        const val SP_FCM_TOKEN: String = "current_fcm_token"
        const val BUNDLE: String = "bundle"

        /**
         * Firebase Strings
         */
        const val FB_USER_PROFILE: String = "user_profile"
        const val FB_CHECKLISTS: String = "checklists"
        const val FB_CHECKLIST_TITLE: String = "title"
        const val FB_CHECKLIST_ITEMS: String = "items"
        const val FB_CONNECTION_REQUEST: String = "connection_requests"
        const val FB_SETTINGS: String = "user_settings"
        const val FB_CONNECTIONS: String = "connections"
        const val FB_FCM_TOKENS: String = "fcm_tokens"
        const val FB_FIRST_NAME: String = "first_name"
        const val FB_SECOND_NAME: String = "second_name"
        const val FB_NICKNAME: String = "nickname"
        const val FB_DISCOVERABLE: String = "discoverable"
        const val FB_UID: String = "uid"

        /**
         * Error codes
         */
        const val ERROR_BLANK_EMAIL_STRING: Int = 100001
        const val ERROR_BLANK_PASSWORD_STRING: Int = 100002
        const val ERROR_PASSWORDS_DO_NOT_MATCH: Int = 100003
        const val ERROR_MISSING_PASSWORD: Int = 100004
        const val ERROR_IMAGE_CROPPING_ACTIVITY_EXCEPTION: Int = 100005
        const val ERROR_PROFILE_DETAILS_BLANK: Int = 100006
        const val ERROR_PROFILE_PICTURE_NO_NETWORK_CONNECTION: Int = 100007
        const val ERROR_NICKNAME_STRUCTURE_ERROR: Int = 100008
        const val ERROR_USER_NOT_PUBLIC: Int = 100009
        const val ERROR_NO_NETWORK: Int = 100010

        /**
         * Functional dialog codes
         */
        const val DIALOG_FORGOT_PASSWORD: Int = 200001

        /**
         * Auth codes
         */
        const val AUTH_LOGIN_SUCCESSFUL: Int = 300001
        const val AUTH_LOGIN_FAILED: Int = 300002
        const val AUTH_LOGOUT_SUCCESSFUL: Int = 300003
        const val AUTH_CREATE_ACCOUNT_SUCCESSFUL: Int = 300004
        const val AUTH_CREATE_ACCOUNT_UNSUCCESSFUL: Int = 300005
        const val AUTH_PASSWORD_RESET_SUCCESSFUL: Int = 300006
        const val AUTH_PASSWORD_RESET_UNSUCCESSFUL: Int = 300007

        /**
         * Auth states
         */
        const val AUTH_STATE_LOGGED_IN: Int = 400001
        const val AUTH_STATE_LOGGED_OUT: Int = 400002

        /**
         * Storage codes
         */
        const val STORAGE_PROFILE_UPLOAD_SUCCESSFUL: Int = 500001
        const val STORAGE_PROFILE_UPLOAD_UNSUCCESSFUL: Int = 500002

        /**
         * Database codes
         */
        const val DB_WRITE_USER_DETAILS_SUCCESSFUL: Int = 600001
        const val DB_WRITE_USER_DETAILS_UNSUCCESSFUL: Int = 600002
        const val DB_CONNECTION_REQUEST_SUBMITTED: Int = 600003

        /**
         * Fragment IDs
         */
        const val ADDRESS_FRAGMENT_ID: Int = 700001
        const val PASSWORD_FRAGMENT_ID: Int = 700002
        const val REGISTER_FRAGMENT_ID: Int = 700003
        const val TODOLIST_FRAGMENT_ID: Int = 700004
        const val CHECKLIST_FRAGMENT_ID: Int = 700005
        const val MY_CONNECTIONS_FRAGMENT_ID: Int = 700006
        const val MY_PROFILE_FRAGMENT_ID: Int = 700007
        const val SETTINGS_FRAGMENT_ID: Int = 700008

    }

    enum class Auth {
        CREATE_ACCOUNT,
        LOGIN,
        RESET_PASSWORD,
        LOGOUT;
    }

    enum class Fragment {
        TODO,
        CHECKLIST,
        MY_CONNECTIONS,
        MY_PROFILE,
        SETTINGS;
    }

    enum class DatabaseApi {
        READ_USER_DETAILS,
        READ_CHECKLISTS,
        READ_CONNECTION_REQUEST_DETAILS,
        WRITE_USER_DETAILS,
        FIND_USERS,
        FIND_PENDING_CONNECTIONS,
        FIND_CONNECTIONS,
        READ_SETTINGS;
    }

    enum class MenuBarItem {
        PLUS,
        DELETE,
        SHARE_PUBLISH;
    }

    enum class ConnectionsSearchResult {
        SELF,
        EXISTING_CONNECTION,
        NEW_CONNECTION;
    }


    enum class Setting(val value: String) {
        LIST_REORDER("reorder_to_do_lists"),
        PROFILE_PRIVACY("profile_public"),
        CONNECTION_REQUEST("notification_connection_requests"),
        NEW_CONNECTIONS("notification_new_connections"),
        NEW_LIST("notification_new_lists"),
        ANALYTICS("analytics_enabled")
    }

    enum class NotificationType(val value: String) {
        REQUEST("connection_request"),
        NEW_CONNECTION("new_connection"),
        NEW_LIST("notification_new_connections")
    }
}