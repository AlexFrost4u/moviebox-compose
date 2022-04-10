package com.majorkik.tmdb.api.model

import com.majorkik.tmdb.api.APIConstants

data class BackdropPath(
    val value: String
) {
    @Suppress("unused")
    enum class Size(val path: String) {
        Width300("w300"),
        Width780("w780"),
        Width1280("w1280"),
        Original("original"),
    }

    fun build(size: Size = Size.Original): String {
        return APIConstants.imageUrl + size.path + value
    }
}

fun String.toBackdropPath() = BackdropPath(value = this)