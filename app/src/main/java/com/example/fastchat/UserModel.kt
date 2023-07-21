package com.example.fastchat

class UserModel {
    var uid: String? = null
    var userName: String? = null
    var phoneNumber: String? = null
    var profileImage: String? = null

    constructor(){}

    constructor(uid: String?, userName: String?, phoneNumber: String?, profileImage: String?, ) {
        this.uid = uid
        this.userName = userName
        this.phoneNumber = phoneNumber
        this.profileImage = profileImage
    }
}