package com.aquirozc.updater.main

import android.app.DownloadManager
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.aquirozc.updater.R
import com.aquirozc.updater.data.URLMetadata
import com.aquirozc.updater.helper.DownloadHelper
import com.aquirozc.updater.helper.PackageHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlin.reflect.KFunction0

class MainActivity: AppCompatActivity() {

    private val downloadSection: LinearLayout by lazy { findViewById(R.id.download_section) }
    private val placeholderSection: LinearLayout by lazy { findViewById(R.id.placeholder_section) }

    private val nameFD: TextView by lazy { findViewById(R.id.local_name_fd) }
    private val sizeFD: TextView by lazy { findViewById(R.id.local_size_fd) }
    private val versionFD: TextView by lazy { findViewById(R.id.local_version_fd) }
    private val dateFD: TextView by lazy { findViewById(R.id.local_date_fd) }

    private val downloadButtons : Array<Button> by lazy { arrayOf(findViewById(R.id.remote1_btn),findViewById(R.id.remote2_btn)) }
    private val sources: Array<KFunction0<String>> = arrayOf(DownloadHelper::getLatestURLFromAndroforever,DownloadHelper::getLatestURLFromApkmody)
    private val metadata = arrayOf(URLMetadata.ANDROFOREVER_METADA, URLMetadata.APKMODY_METADATA)

    private var isFetchJobActive = false

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fetchPackageInformation(null)
        this.findViewById<FloatingActionButton>(R.id.refresh_btn).setOnClickListener(this::fetchPackageInformation)
    }

    private fun downloadFile(url : String, referer : String){
        val request = DownloadManager.Request(url.toUri())
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/com.aquirozc/" + getFileName(url))
            .addRequestHeader("Referer",referer)

        (this.getSystemService(DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
    }

    private fun fetchPackageInformation(view : View?) {
        if (isFetchJobActive) {
            Snackbar.make(findViewById(R.id.MainLayout), "Please Wait", Snackbar.LENGTH_SHORT).show()
            return
        }

        showProgressBar(true)

        val info = PackageHelper.getLocalPackageInfo(applicationContext, "com.spotify.music")
        nameFD.text = info.name
        sizeFD.text = info.size
        versionFD.text = info.version
        dateFD.text = info.date

        lifecycleScope.launch {
            supervisorScope {
                repeat(2) { i -> launch { setupDownloadButton(i) } }
            }
           showProgressBar(false)
        }
    }

    private fun getFileName(url : String): String {
        return url.substringAfterLast("/").replace("%20"," ")
    }

    private suspend fun setupDownloadButton(index: Int) {
        runCatching {
            withContext(Dispatchers.IO) {
                val url = sources[index].invoke()
                val meta = metadata[index]
                val version = url.split(meta.separator).getOrNull(meta.index) ?: "Unknown"
                val size = "${DownloadHelper.getFileSize(url) / (1024 * 1024)}MB"

                downloadButtons[index].text = "Spotify $version ($size)"
                downloadButtons[index].setOnClickListener { downloadFile(url, metadata[index].domain) }
            }
        }
    }

    private fun showProgressBar(isFetching : Boolean){
        downloadSection.visibility = if (isFetching) View.GONE else View.VISIBLE
        placeholderSection.visibility = if (isFetching) View.VISIBLE else View.GONE
        isFetchJobActive = isFetching
    }

}