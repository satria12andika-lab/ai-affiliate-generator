
package com.stradk.aiaffiliate

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebResourceRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient.FileChooserParams
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var webView: WebView
    private var fileCallback: ValueCallback<Array<Uri>>? = null

    companion object {
        private const val FILE_REQUEST = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            webView = WebView(this)

            webView.visibility = View.VISIBLE

            webView.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                javaScriptCanOpenWindowsAutomatically = false
                setSupportMultipleWindows(false)
            }

            webView.webViewClient = object : WebViewClient() {

                override fun onPageFinished(
                    view: WebView?,
                    url: String?
                ) {
                    super.onPageFinished(view, url)
                }
            }

            webView.webChromeClient = object : WebChromeClient() {

                override fun onShowFileChooser(
                    view: WebView?,
                    callback: ValueCallback<Array<Uri>>?,
                    params: FileChooserParams?
                ): Boolean {

                    fileCallback?.onReceiveValue(null)
                    fileCallback = callback

                    return try {
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.type = "image/*"
                        intent.addCategory(Intent.CATEGORY_OPENABLE)

                        startActivityForResult(
                            Intent.createChooser(
                                intent,
                                "Pilih Foto Produk"
                            ),
                            FILE_REQUEST
                        )
                        true
                    } catch (e: Exception) {
                        fileCallback?.onReceiveValue(null)
                        fileCallback = null
                        Toast.makeText(
                            this@MainActivity,
                            "Tidak bisa membuka galeri",
                            Toast.LENGTH_SHORT
                        ).show()
                        false
                    }
                }
            }

            setContentView(webView)

            webView.loadUrl(
                "file:///android_asset/index.html"
            )

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Gagal membuka aplikasi: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    @Deprecated("Deprecated in Android")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_REQUEST) {

            val result = if (resultCode == RESULT_OK) {
                data?.data?.let { arrayOf(it) }
            } else {
                null
            }

            fileCallback?.onReceiveValue(result)
            fileCallback = null
        }
    }

    @Deprecated("Deprecated in Android")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            webView.stopLoading()
            webView.loadUrl("about:blank")
            webView.destroy()
        }

        super.onDestroy()
    }
}
