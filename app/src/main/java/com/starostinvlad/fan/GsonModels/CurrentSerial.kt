package com.starostinvlad.fan.GsonModels

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlin.Throws

@Entity
class CurrentSerial {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
    var pageId: String? = null
    var currentSeasonIndex = 0
    var currentEpisodeIndex = 0
    var currentTranslationIndex = 0

    constructor() {}

    @Ignore
    constructor(pageId: String?, currentSeasonIndex: Int, currentEpisodeIndex: Int, currentTranslationIndex: Int) {
        this.pageId = pageId
        this.currentSeasonIndex = currentSeasonIndex
        this.currentEpisodeIndex = currentEpisodeIndex
        this.currentTranslationIndex = currentTranslationIndex
    }
}