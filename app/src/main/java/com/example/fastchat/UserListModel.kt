package com.example.fastchat

class UserListModel {
    var uid: String? = null
    var userName: String? = null
    var message: String? = null
    var profileImage: String? = null

    constructor(){}

    constructor(uid: String?, userName: String?, profileImage: String?, message: String?) {
        this.uid = uid
        this.userName = userName
        this.message = message
        this.profileImage = profileImage
    }
}