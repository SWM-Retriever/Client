package org.retriever.dailypet.ui.mypage

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.navigation.navArgs
import org.retriever.dailypet.databinding.ActivityWebViewBinding
import org.retriever.dailypet.ui.base.BaseActivity

class WebViewActivity : BaseActivity<ActivityWebViewBinding>({ ActivityWebViewBinding.inflate(it) }) {

    private val args: WebViewActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initWebView()
    }

    private fun initWebView() {
        val webView = binding.webView
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        webView.loadUrl(args.url)
    }

}