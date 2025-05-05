package com.umn.story.models

data class StoryUser(
    val story: Story,
    var like: Boolean = false
)
