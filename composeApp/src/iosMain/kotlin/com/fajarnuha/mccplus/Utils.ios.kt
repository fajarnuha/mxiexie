package com.fajarnuha.mccplus

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readBytes
import org.jetbrains.skia.Image
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes

actual fun imageBitmapFromBytes(bytes: ByteArray): ImageBitmap {
    val data = bytes.toNSData()
    return Image.makeFromEncoded(data.toByteArray()).toComposeImageBitmap()
}
@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData = NSData.dataWithBytes(this.toUByteArray().toByteArray(), size.toULong())
@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray = this.bytes?.readBytes(this.length.toInt()) ?: byteArrayOf()