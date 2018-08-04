package com.rbiggin.a2do2gether.model

import com.rbiggin.a2do2gether.utils.Constants

data class UserConnectionSearch(var firstName: String,
                                var secondName: String,
                                var nickname: String,
                                var uid: String,
                                var type: Constants.ConnectionsSearchResult)