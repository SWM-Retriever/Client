package org.retriever.dailypet.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import org.retriever.dailypet.databinding.FragmentWebViewBinding
import org.retriever.dailypet.ui.base.BaseFragment

class WebViewFragment : BaseFragment<FragmentWebViewBinding>() {

    private val args: WebViewFragmentArgs by navArgs()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentWebViewBinding {
        return FragmentWebViewBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initWebView()
    }

    private fun initWebView(){
        val webView = binding.webView
        webView.webViewClient = WebViewClient()
        webView.loadUrl(args.url)
    }

}