import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.os.ResultReceiver
import java.io.*
import java.net.URL
import java.net.URLConnection


class DownloadService : IntentService("DownloadService") {
    override fun onHandleIntent(intent: Intent?) {
        val urlToDownload = intent!!.getStringExtra("url")
        val receiver: ResultReceiver =
            intent.getParcelableExtra<Parcelable>("receiver") as ResultReceiver
        try {

            //create url and connect
            val url = URL(urlToDownload)
            val connection: URLConnection = url.openConnection()
            connection.connect()

            // this will be useful so that you can show a typical 0-100% progress bar
            val fileLength: Int = connection.getContentLength()

            // download the file
            val input: InputStream = BufferedInputStream(connection.getInputStream())
            val path = "/sdcard/BarcodeScanner-debug.apk"
            val output: OutputStream = FileOutputStream(path)
            val data = ByteArray(1024)
            var total: Long = 0
            var count: Int
            while (input.read(data).also { count = it } != -1) {
                total += count.toLong()

                // publishing the progress....
                val resultData = Bundle()
                resultData.putInt("progress", (total * 100 / fileLength).toInt())
                receiver.send(UPDATE_PROGRESS, resultData)
                output.write(data, 0, count)
            }

            // close streams
            output.flush()
            output.close()
            input.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val resultData = Bundle()
        resultData.putInt("progress", 100)
        receiver.send(UPDATE_PROGRESS, resultData)
    }

    companion object {
        const val UPDATE_PROGRESS = 8344
    }
}