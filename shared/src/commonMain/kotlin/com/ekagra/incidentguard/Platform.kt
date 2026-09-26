package com.ekagra.incidentguard

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform