package com.rbiggin.a2do2gether.model

/**
 * Describes the user's details
 */
data class UserDetails (var firstName: String,
                        var secondName: String,
                        var nickname: String,
                        var uid: String,
                        var discoverable: Boolean)