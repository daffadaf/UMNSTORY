package com.umn.story.models

data class User(
    val nim: String = "",
    val nama: String = "",
    val password: String = "",
    val likes: ArrayList<String> = arrayListOf()
)
