package com.tinkoff.news.data.cache

interface CacheObjectMapper<T> {

    fun mapObject(obj: T): ByteArray

    fun mapBytes(bytes: ByteArray): T

}