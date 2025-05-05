package com.umn.story.models

import java.util.Date

data class Story(
    val id: String = "",
    val cerita: String = "",
    val foto: String? = null,
    val createdAt: Date? = null
)
