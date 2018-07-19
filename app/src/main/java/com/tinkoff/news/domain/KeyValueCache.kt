package com.tinkoff.news.domain

interface KeyValueCache<T> {
    fun set(key: String, value: T)
    fun get(key: String): T?
}