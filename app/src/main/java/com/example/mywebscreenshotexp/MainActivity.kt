package com.example.mywebscreenshotexp

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.example.mywebscreenshotexp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    /*
    * using WebSettings, you have fine-grained control over the behavior and settings of the WebView in your app,
    *   allowing you to customize it to match your desired functionality and user experience.
    * */
    private var chatWebSettings : WebSettings? = null
    /*
    * CookieManager in a WebView gives you control over handling cookies within the context of your application.
    *   You can manage cookies for specific URLs, set and remove cookies
    * */
    private var chatCookieManager: CookieManager? = null
    private var context: Context = this
    private var _binding: ActivityMainBinding? = null

    companion object{
        val TAG = "PETOFY_LOAD_URL"
//        val urlToLoad = "https://www.perfomatix.com/best-practices-in-android-coding/"
        val urlToLoad = "https://www.petofy.com/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        chatCookieManager = CookieManager.getInstance()
        chatCookieManager?.setAcceptCookie(true)            //  enables the WebView to accept cookies.
        chatCookieManager?.setAcceptThirdPartyCookies(_binding?.chatWebView, false)   // Setting it to false means that the WebView will only accept cookies from the same domain as the one currently loaded, and not from third-party domains.

        /*
        * By doing this, you are essentially saying that you want to use this WebChromeClient instance to handle any relevant events
        *   and behaviors related to the Chrome browser functionality in the chatWebView
        *   It provides callbacks for handling actions such as progress updates, JavaScript alerts, requesting permission for geolocation, handling file uploads, and more.
        * */
        _binding?.chatWebView?.webChromeClient = WebChromeClient()

        var initialProgressBar: ProgressDialog? = ProgressDialog(context)
        initialProgressBar?.setTitle("Loading...")
        initialProgressBar?.setMessage("Please Wait...")
        initialProgressBar?.setCancelable(false)
        initialProgressBar?.show()

        // swipe refresh to reLoad the website
        _binding?.swipeRefreshLayout?.setOnRefreshListener {
            _binding?.chatWebView?.reload()
        }


      /*
      * The WebViewClient is responsible for handling various events in the WebView, such as page loading.
      *     By setting it as the webViewClient, you can intercept requests and handle them accordingly
      * */
        _binding?.chatWebView?.webViewClient  = object : WebViewClient(){
            val innerPageProgressBar: ProgressDialog? = ProgressDialog(context)


            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                _binding?.swipeRefreshLayout?.isRefreshing = false

                if (initialProgressBar == null) {
                    innerPageProgressBar?.setTitle("INNER PB Loading...")
                    innerPageProgressBar?.setMessage("Please Wait...")
                    innerPageProgressBar?.setCancelable(false)
                    innerPageProgressBar?.show()
                }

                // todo: not working!!
                // to remove specific section of website
                _binding?.chatWebView?.evaluateJavascript(
                    "(function() { " +
                            "   var section = document.querySelector('.navbar-header');" +
                            "   if (section) {" +
                            "       section.style.display = 'none';" +
                            "   }" +
                            "})();",
                    null
                )
            }
/*
*  onPageCommitVisible is called when the page content becomes visible,
*  onPageFinished is called when the entire page and its resources have finished loading.
* */
            override fun onPageCommitVisible(view: WebView?, url: String?) {
                super.onPageCommitVisible(view, url)

            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (initialProgressBar == null) {
                    innerPageProgressBar?.let {
                        it.dismiss()
                    }
                } else {
                    initialProgressBar?.let {
                        it.dismiss()
                        initialProgressBar = null
                    }
                }

            }
        }

        chatWebSettings = _binding?.chatWebView?.settings
        chatWebSettings?.javaScriptEnabled = true       // Enables or disables JavaScript support in the WebView.
        chatWebSettings?.cacheMode  = WebSettings.LOAD_DEFAULT      // Sets the caching mode for the WebView content.
        chatWebSettings?.setGeolocationEnabled(false)       // : Enables or disables geolocation in the WebView.

        chatWebSettings?.allowContentAccess = false     //  Sets whether the WebView allows access to content from other origins.
        chatWebSettings?.allowFileAccess = false        //  Sets whether the WebView allows access to the file system.
        chatWebSettings?.builtInZoomControls = false    // Enables or disables built-in zoom controls in the WebView.
        chatWebSettings?.databaseEnabled = false        //  Enables or disables database storage in the WebView
        chatWebSettings?.displayZoomControls = false    // Enables or disables the display of zoom controls in the WebView.
        chatWebSettings?.domStorageEnabled = true       // Enables or disables DOM storage (Web Storage) in the WebView.
        chatWebSettings?.saveFormData = false       // Enables or disables saving form data in the WebView.


        _binding?.chatWebView?.loadUrl(urlToLoad)


    }


/* tracks the backStack of screens, rather navigation home screen */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (_binding?.chatWebView?.canGoBack() == true && !_binding?.chatWebView?.url.equals("about:blank")) {
                        _binding?.chatWebView?.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}