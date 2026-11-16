package org.wit.placemark.helpers

import android.content.Context
import timber.log.Timber
import java.io.*

fun write(context: Context, fileName: String, data: String) {
    try {
        val outputStreamWriter = OutputStreamWriter(
            context.openFileOutput(
                fileName,
                Context.MODE_PRIVATE
            )
        )

        outputStreamWriter.write(data)
        outputStreamWriter.close()

    } catch (e: Exception) {
        Timber.e(e, "Cannot write file: %s", fileName)
    }
}

fun read(context: Context, fileName: String): String {
    var str = ""

    try {
        val inputStream = context.openFileInput(fileName)

        val inputStreamReader = InputStreamReader(inputStream)
        val bufferedReader = BufferedReader(inputStreamReader)

        val partialStr = StringBuilder()
        var done = false

        while (!done) {
            val line = bufferedReader.readLine()
            done = (line == null)

            if (line != null) {
                partialStr.append(line)
            }
        }

        inputStream.close()
        str = partialStr.toString()

    } catch (e: FileNotFoundException) {
        Timber.e(e, "File not found: %s", fileName)

    } catch (e: IOException) {
        Timber.e(e, "Cannot read file: %s", fileName)
    }

    return str
}

fun exists(context: Context, filename: String): Boolean {
    val file = context.getFileStreamPath(filename)
    return file.exists()
}
