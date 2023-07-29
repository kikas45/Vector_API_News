package com.example.vectonews.ui.detailfragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.vectonews.R
import com.example.vectonews.databinding.FragmentDetailBinding


class DetailFragment : Fragment(R.layout.fragment_detail) {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!


    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailBinding.bind(view)


        binding.apply {

            btnBackPressed.setOnClickListener {
                findNavController().popBackStack(R.id.main_Home_Fragment, false)
            }
            val titles = arguments?.getString("titles").toString()


            if (titles.isNotEmpty()) {
                textView3.text = titles
            }


            webView.webViewClient = WebViewClient()

            webView.settings.javaScriptEnabled = true
            webView.settings.setSupportZoom(true)
            webView.settings.builtInZoomControls = true

            webView.settings.displayZoomControls = false
            webView.settings.useWideViewPort = true
            webView.settings.loadWithOverviewMode = true



            if (savedInstanceState != null) {
                webView.restoreState(savedInstanceState)
            } else {
                val urls_image = arguments?.getString("urls_webView").toString()

                webView.loadUrl(urls_image)
            }

            swipeMotionLayout.setOnRefreshListener {
                webView.reload()
            }



            webView.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    progressBar.visibility =
                        View.VISIBLE // Show ProgressBar when the page starts loading
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    progressBar.visibility = View.GONE
                    swipeMotionLayout.isRefreshing = false
                }

            }

        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webView.saveState(outState)
    }


    override fun onResume() {
        super.onResume()
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                binding.apply {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        findNavController().popBackStack(R.id.main_Home_Fragment, false)
                    }
                }

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null

        handler.removeCallbacksAndMessages { null }


    }

}