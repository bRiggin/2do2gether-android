package com.rbiggin.a2do2gether.utils

/**
 * Constants for application
 */
object Constants {
    /**
     *
     * Generic constant values
     *
     */

    /** Time is ms that timed Utility dialog time out after */
    const val DIALOG_DISMISS_TIME: Int = 1500

    /** 3 Fragments email address, password and create password */
    const val NUMBER_OF_LOGIN_FRAGMENTS: Int = 3

    /** ... */
    const val NUMBER_OF_CHARACTERS_IN_NICKNAME: Int = 8

    /**
     *
     * Shared Preferences Strings
     *
     */

    /** ... */
    const val SP_UID: String = "current_uid"

    /** ... */
    const val SP_FCM_TOKEN: String = "current_fcm_token"

    /**
     *
     * Firebase Strings
     *
     */

    /** ... */
    const val FB_USER_PROFILE: String = "user_profile"

    /** ... */
    const val FB_CONNECTION_REQUEST: String = "connection_requests"

    /** ... */
    const val FB_FCM_TOKENS: String = "fcm_tokens"

    /** ... */
    const val FB_FIRST_NAME: String = "first_name"

    /** ... */
    const val FB_SECOND_NAME: String = "second_name"

    /** ... */
    const val FB_NICKNAME: String = "nickname"

    /** ... */
    const val FB_DISCOVERABLE: String = "discoverable"

    /** ... */
    const val FB_UID: String = "uid"

    /**
     *
     * Error codes
     *
     */

    /** Error code for email field being left blank by user */
    const val ERROR_BLANK_EMAIL_STRING: Int = 100001

    /** Error code for password field being left blank by user */
    const val ERROR_BLANK_PASSWORD_STRING: Int = 100002

    /** Error code for user not entering matching passwords when creating account */
    const val ERROR_PASSWORDS_DO_NOT_MATCH: Int = 100003

    /** Error code for user not entering both passwords when creating account */
    const val ERROR_MISSING_PASSWORD: Int = 100004

    /** Error code for an exception that has been thrown by the image crop activity */
    const val ERROR_IMAGE_CROPPING_ACTIVITY_EXCEPTION: Int = 100005

    /** Error code for the user trying upload incomplete profile details.*/
    const val ERROR_PROFILE_DETAILS_BLANK: Int = 100006

    /** Error code for the user trying upload new profile picture without network connection*/
    const val ERROR_PROFILE_PICTURE_NO_NETWORK_CONNECTION: Int = 100007

    /** ... */
    const val ERROR_NICKNAME_STRUCTURE_ERROR: Int = 100008

    /** ... */
    const val ERROR_USER_NOT_PUBLIC: Int = 100009

    /**
     *
     * Functional dialog codes
     *
     */

    /** Dialog code for user forgetting account password */
    const val DIALOG_FORGOT_PASSWORD: Int = 200001

    /**
     *
     * Auth codes
     *
     */

    /** Auth code for user successfully logging in */
    const val AUTH_LOGIN_SUCCESSFUL: Int = 300001

    /** Auth code failed login */
    const val AUTH_LOGIN_FAILED: Int = 300002

    /** Auth code for user successfully logging out */
    const val AUTH_LOGOUT_SUCCESSFUL: Int = 300003

    /** Auth code for user successfully creating account */
    const val AUTH_CREATE_ACCOUNT_SUCCESSFUL: Int = 300004

    /** Auth code for failing to create an account */
    const val AUTH_CREATE_ACCOUNT_UNSUCCESSFUL: Int = 300005

    /** Auth code for successfully sending a password reset email */
    const val AUTH_PASSWORD_RESET_SUCCESSFUL: Int = 300006

    /** Auth code for unsuccessfully sending a password reset email */
    const val AUTH_PASSWORD_RESET_UNSUCCESSFUL: Int = 300007

    /**
     *
     * Auth states
     *
     */

    /** Auth code for unsuccessfully sending a password reset email */
    const val AUTH_STATE_LOGGED_IN: Int = 400001

    /** Auth code for unsuccessfully sending a password reset email */
    const val AUTH_STATE_LOGGED_OUT: Int = 400002

    /**
     *
     * Storage codes
     *
     */

    /** ... */
    const val STORAGE_PROFILE_UPLOAD_SUCCESSFUL: Int = 500001

    /** ... */
    const val STORAGE_PROFILE_UPLOAD_UNSUCCESSFUL: Int = 500002

    /**
     *
     * Database codes
     *
     */

    /** ... */
    const val DB_WRITE_USER_DETAILS_SUCCESSFUL: Int = 600001

    /** ... */
    const val DB_WRITE_USER_DETAILS_UNSUCCESSFUL: Int = 600002

    /** ... */
    const val DB_CONNECTION_REQUEST_SUBMITTED: Int = 600003

    /**
     *
     * Fragment IDs
     *
     */

    /** Id for Address Fragment */
    const val ADDRESS_FRAGMENT_ID: Int = 700001

    /** Id for Password Fragment */
    const val PASSWORD_FRAGMENT_ID: Int = 700002

    /** Id for Register Fragment */
    const val REGISTER_FRAGMENT_ID: Int = 700003

    /** Id for ToDoList Fragment */
    const val TODOLIST_FRAGMENT_ID: Int = 700004

    /** Id for Checklist Fragment */
    const val CHECKLIST_FRAGMENT_ID: Int = 700005

    /** Id for My Connections Fragment */
    const val MY_CONNECTIONS_FRAGMENT_ID: Int = 700006

    /** Id for My Profile Fragment */
    const val MY_PROFILE_FRAGMENT_ID: Int = 700007

    /** Id for Settings Fragment */
    const val SETTINGS_FRAGMENT_ID: Int = 700008

    /**
     *
     * Logging tag strings
     *
     */

    const val LOGIN_ACTIVITY_TAG: String = "Login Activity:"
    const val LOGIN_PRESENTER_TAG: String = "Login Presenter:"

    const val MAIN_ACTIVITY_TAG: String = "Main Activity:"
    const val MAIN_PRESENTER_TAG: String = "Main Presenter:"

    const val TODO_ACTIVITY_TAG: String = "ToDoLists Activity:"
    const val TODO_PRESENTER_TAG: String = "ToDoLists Presenter:"

    const val CHECKLISTS_ACTIVITY_TAG: String = "Checklists Activity:"
    const val CHECKLISTS_PRESENTER_TAG: String = "Checklists Presenter:"

    const val CONNECTIONS_ACTIVITY_TAG: String = "My Connections Activity:"
    const val CONNECTIONS_PRESENTER_TAG: String = "My Connections Presenter:"

    const val PROFILE_ACTIVITY_TAG: String = "My Profile Activity:"
    const val PROFILE_PRESENTER_TAG: String = "My Profile Presenter:"

    const val SETTINGS_ACTIVITY_TAG: String = "Setting Activity:"
    const val SETTINGS_PRESENTER_TAG: String = "Setting Presenter:"

    const val USER_REPOSITORY_TAG = "User Repository:"

    /**
     * Enumeration class that describes fragment type and get methods.
     */
    enum class AuthApiType {
        CREATE_ACCOUNT,
        LOGIN,
        RESET_PASSWORD,
        LOGOUT
    }
    fun authApiCreateAccount(): AuthApiType {return AuthApiType.CREATE_ACCOUNT}
    fun authApiLogin(): AuthApiType {return AuthApiType.LOGIN}
    fun authApiResetPassword(): AuthApiType {return AuthApiType.RESET_PASSWORD}
    fun authApiLogout(): AuthApiType {return AuthApiType.LOGOUT}

    /**
     * Enumeration class that describes fragment type and get methods.
     */
    enum class FragmentType {
        TODO,
        CHECKLIST,
        MY_CONNECTIONS,
        MY_PROFILE,
        SETTINGS
    }
    fun fragmentTypeToDo(): FragmentType {return FragmentType.TODO}
    fun fragmentTypeChecklists(): FragmentType {return FragmentType.CHECKLIST}
    fun fragmentTypeConnections(): FragmentType {return FragmentType.MY_CONNECTIONS}
    fun fragmentTypeProfile(): FragmentType {return FragmentType.MY_PROFILE}
    fun fragmentTypeSettings(): FragmentType {return FragmentType.SETTINGS}

    /**
     * Enumeration class that describes database API request types and get methods.
     */
    enum class DatabaseApiType {
        READ_USER_DETAILS,
        WRITE_USER_DETAILS,
        FIND_USERS,
        FIND_PENDING_CONNECTIONS
    }
    fun dbApiReadUserDetails(): DatabaseApiType {return DatabaseApiType.READ_USER_DETAILS}
    fun dbApiWriteUserDetails(): DatabaseApiType {return DatabaseApiType.WRITE_USER_DETAILS}
    fun dbApiFindUsers(): DatabaseApiType {return DatabaseApiType.FIND_USERS}
    fun dbApiFindPendingConnections(): DatabaseApiType {return DatabaseApiType.FIND_PENDING_CONNECTIONS}

    /**
     * Enumeration class that describes my connections view types and get methods.
     */
    enum class MyConnectionView {
        MAIN_VIEW,
        SEARCH_VIEW
    }
    fun connectionsMainView(): MyConnectionView {return MyConnectionView.MAIN_VIEW}
    fun connectionsSearchView(): MyConnectionView {return MyConnectionView.SEARCH_VIEW}

    /**
     * Enumeration class that describes action bar menu items and get methods.
     */
    enum class MenuBarItem {
        PLUS,
        DELETE,
        SHARE_PUBLISH
    }
    fun menuBarItemPlus(): MenuBarItem {return MenuBarItem.PLUS}
    fun menuBarItemDelete(): MenuBarItem {return MenuBarItem.DELETE}
    fun menuBarItemSharePublish(): MenuBarItem {return MenuBarItem.SHARE_PUBLISH }

    /**
     *
     */
    enum class ConnectionsSearchResultType{
        SELF,
        EXISTING_CONNECTION,
        NEW_CONNECTION
    }
    fun searchResultSelf(): ConnectionsSearchResultType {return ConnectionsSearchResultType.SELF}
    fun searchResultExistingConneciton(): ConnectionsSearchResultType {return ConnectionsSearchResultType.EXISTING_CONNECTION}
    fun searchResultNewConnection(): ConnectionsSearchResultType {return ConnectionsSearchResultType.NEW_CONNECTION}

    /**
     *
     */
    enum class ConnectionsActionType{
        CONNECTION_REQUEST,
        ACCEPT_CONNECTION_REQUEST,
        REJECT_CONNECTION_REQUEST
    }
    fun connectionsActionRequest(): ConnectionsActionType {return ConnectionsActionType.CONNECTION_REQUEST}
    fun connectionsActionAccept(): ConnectionsActionType {return ConnectionsActionType.ACCEPT_CONNECTION_REQUEST}
    fun connectionsActionReject(): ConnectionsActionType {return ConnectionsActionType.REJECT_CONNECTION_REQUEST}
}