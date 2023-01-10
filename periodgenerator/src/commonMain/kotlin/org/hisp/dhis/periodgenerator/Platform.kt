package org.hisp.dhis.periodgenerator

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform