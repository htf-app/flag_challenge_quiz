package com.ginger.democountryquizapp.utils

import android.content.Context
import java.io.IOException
import java.nio.charset.StandardCharsets

object FileUtils {

fun readJsonFromAssets(context: Context, fileName: String): String? {
        return try {
val inputStream = context.assets.open(fileName)
val size = inputStream.available()
val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
String(buffer, StandardCharsets.UTF_8)
        } catch (e: IOException) {
        e.printStackTrace()
            null
            }
            }
            }
