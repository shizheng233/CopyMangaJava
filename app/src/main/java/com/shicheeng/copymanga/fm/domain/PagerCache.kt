package com.shicheeng.copymanga.fm.domain

import android.content.Context
import com.shicheeng.copymanga.ui.screen.setting.SettingPref
import com.tomclaw.cache.DiskLruCache
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PagerCache @Inject constructor(
    @ApplicationContext context: Context,
    settingPref: SettingPref,
) {

    private val cache = (context.externalCacheDirs + context.cacheDir).firstNotNullOfOrNull {
        it.makeDirIfNoExist()
    }.let { file ->
        checkNotNull(file) {
            val dirs = (context.externalCacheDirs + context.cacheDir).joinToString(";") {
                it.absolutePath
            }
            "Cannot find directory for PagesCache: [$dirs]"
        }
    }

    private val lruCache = createDiskLruCacheSafe(
        dir = cache,
        size = FileSize.MEGABYTES.convert(settingPref.cacheSize.toLong(), FileSize.BYTES),
    )

    operator fun get(url: String): File? {
        return lruCache.get(url)?.takeIfReadable()
    }

    suspend fun put(url: String, inputStream: InputStream): File = withContext(Dispatchers.IO) {
        val file = File(cache.parentFile, url.longHashCode().toString())
        try {
            file.outputStream().use { out ->
                inputStream.copyToSuspending(out)
            }
            lruCache.put(url, file)
        } finally {
            file.delete()
        }
    }

    private fun createDiskLruCacheSafe(dir: File, size: Long): DiskLruCache {
        return try {
            DiskLruCache.create(dir, size)
        } catch (e: Exception) {
            dir.deleteRecursively()
            dir.mkdir()
            DiskLruCache.create(dir, size)
        }
    }

    /**
     * Copy from kotatsu
     */
    private suspend fun InputStream.copyToSuspending(
        out: OutputStream,
        bufferSize: Int = DEFAULT_BUFFER_SIZE,
    ): Long = withContext(Dispatchers.IO) {
        val job = currentCoroutineContext()[Job]
        var bytesCopied: Long = 0
        val buffer = ByteArray(bufferSize)
        var bytes = read(buffer)
        while (bytes >= 0) {
            out.write(buffer, 0, bytes)
            bytesCopied += bytes
            job?.ensureActive()
            bytes = read(buffer)
            job?.ensureActive()
        }
        bytesCopied
    }

    /**
     * Come from Kotatsu
     */
    private fun String.longHashCode(): Long {
        var h = 1125899906842597L
        val len: Int = this.length
        for (i in 0 until len) {
            h = 31 * h + this[i].code
        }
        return h
    }

    /**
     * Come from Kotatsu
     */
    private fun File.takeIfReadable() = takeIf { it.exists() && it.canRead() }

}

fun File.makeDirIfNoExist(): File {
    if (!this.exists()) {
        if (this.parentFile?.exists() != true) this.parentFile?.mkdir()
        if (this.parentFile?.canWrite() != true) this.parentFile?.canWrite()
        this.mkdir()
    }
    return this
}

/**
 *
 * Copy from Kotatsu
 */
enum class FileSize(private val multiplier: Int) {

    BYTES(1), KILOBYTES(1024), MEGABYTES(1024 * 1024);

    fun convert(amount: Long, target: FileSize): Long = amount * multiplier / target.multiplier

}