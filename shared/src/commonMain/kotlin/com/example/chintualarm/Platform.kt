package com.example.chintualarm

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform