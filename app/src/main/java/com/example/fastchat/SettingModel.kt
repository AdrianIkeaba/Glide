package com.example.fastchat

class SettingModel {
    var settingImage: Int = 0
    var settingName: String = ""

    constructor(settingImage: Int, settingName: String) {
        this.settingName = settingName
        this.settingImage = settingImage
    }
}