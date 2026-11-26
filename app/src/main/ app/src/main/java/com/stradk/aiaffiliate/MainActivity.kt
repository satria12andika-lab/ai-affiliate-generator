package com.stradk.aiaffiliate

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.ValueCallback
import android.webkit.WebChromeClient.FileChooserParams

class MainActivity : Activity() {

    private lateinit var webView: WebView
    private var fileCallback: ValueCallback<Array<Uri>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true

        webView.webViewClient = WebViewClient()

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                view: WebView,
                callback: ValueCallback<Array<Uri>>,
                params: FileChooserParams
            ): Boolean {

                fileCallback?.onReceiveValue(null)
                fileCallback = callback

                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                intent.addCategory(Intent.CATEGORY_OPENABLE)

                startActivityForResult(
                    Intent.createChooser(intent, "Pilih Foto Produk"),
                    100
                )

                return true
            }
        }

        setContentView(webView)

        webView.loadUrl("file:///android_asset/index.html")
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            val result = if (resultCode == RESULT_OK) {
                data?.data?.let { arrayOf(it) }
            } else null

            fileCallback?.onReceiveValue(result)
            fileCallback = null
        }
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
