package com.tinkoff.news.data.cache

import com.tinkoff.news.domain.KeyValueCache
import java.io.*

class KeyValueCacheImpl<T>(
        private val cachePath: File,
        private var mapper: CacheObjectMapper<T>
) : KeyValueCache<T> {

    private val lock = Object()


    ///////////////////////////////////////////////////////////////////////////
    // Cache implementation
    ///////////////////////////////////////////////////////////////////////////

    override fun set(key: String, value: T) {
        writeToCache(
                key,
                mapper.mapObject(value)
        )
    }

    override fun get(key: String): T? {
        val bytes = getFromCache(key) ?: return null

        return mapper.mapBytes(bytes)
    }



    ///////////////////////////////////////////////////////////////////////////
    // Simple file cache
    ///////////////////////////////////////////////////////////////////////////

    private fun writeToCache(key: String, bytes: ByteArray) {

        val cacheFile = File(cachePath, key)

        synchronized(lock) {

            if (!cacheFile.exists()) {
                cacheFile.createNewFile()
            } else {

            }

            var stream: OutputStream? = null

            try {
                stream = FileOutputStream(cacheFile)
                stream.write(bytes)
            } catch (_: Exception) {
            } finally {
                try {

                } catch (_: Exception) {}
                stream?.close()
            }

        }
    }

    private fun getFromCache(key: String): ByteArray? {
        val cacheFile = File(cachePath, key)

        val size = cacheFile.length().toInt()
        val bytes = ByteArray(size)

        synchronized(lock) {
            if (!cacheFile.exists()) {
                return null
            }

            var stream: BufferedInputStream? = null
            try {
                stream = BufferedInputStream(FileInputStream(cacheFile))
                stream.read(bytes, 0, bytes.size)
            } catch (_: Exception) {
            } finally {
                try {
                    stream?.close()
                } catch (_: Exception) {
                }
            }
        }

        return bytes
    }

}