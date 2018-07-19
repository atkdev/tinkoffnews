package com.tinkoff.news.data.cache

import android.util.Log
import com.google.gson.Gson
import java.lang.reflect.Type

class CacheObjectMapperImpl<T> constructor(val type: Type): CacheObjectMapper<T> {

    override fun mapObject(obj: T): ByteArray =
        Gson().toJson(obj).toByteArray()


    override fun mapBytes(bytes: ByteArray): T {
        Log.d("!!!!!! out", Gson().toJson(String(bytes)))
        Log.d("!!!!!! type ", type.toString())
        return Gson().fromJson(String(bytes), type)
    }

}