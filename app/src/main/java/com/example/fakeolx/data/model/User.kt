package com.example.fakeolx.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val ulubione: List<String> = emptyList()
)
