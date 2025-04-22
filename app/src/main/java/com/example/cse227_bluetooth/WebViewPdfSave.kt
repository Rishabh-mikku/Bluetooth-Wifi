package com.example.cse227_bluetooth

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintJob
import android.print.PrintManager
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class WebViewPdfSave : AppCompatActivity() {
    // creating object of WebView
    var printWeb: WebView? = null
    // object of print job
    var printJob: PrintJob? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview_preview)
        // Initializing the WebView
        val webView = findViewById<View>(R.id.webViewMain) as WebView
        // Initializing the Button
        val savePdfButton: Button = findViewById<View>(R.id.savePdfButton) as Button
        // Setting View Client
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                // Initializing the printWeb Object
                printWeb = webView
            }
        } // loading the URL
        webView.loadUrl("https://www.lpu.in")
        // setting clickListener for Save Pdf Button
        savePdfButton.setOnClickListener {
            if (printWeb != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)  {
                    // Calling createWebPrintJob()
                    printTheWebPage(printWeb!!)
                } else {
                    Toast.makeText(this@WebViewPdfSave,
                        "Not available for device below Android LOLLIPOP",
                        Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@WebViewPdfSave, "WebPage not fully loaded",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    // a boolean to check the status of printing
    var printBtnPressed = false
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun printTheWebPage(webView: WebView) {
        // set printBtnPressed true
        printBtnPressed = true
        // Creating PrintManager instance
        val printManager = this
            .getSystemService(Context.PRINT_SERVICE) as PrintManager
        // setting the name of job
        val jobName = "CSE227_Bluetooth" + " webpage" + webView.url
        // Creating PrintDocumentAdapter instance
        val printAdapter = webView.createPrintDocumentAdapter(jobName)
        assert(printManager != null)
        printJob = printManager.print(jobName, printAdapter,
            PrintAttributes.Builder().build())
    }

    override fun onResume() {
        super.onResume()
        if (printJob != null && printBtnPressed) {
            if (printJob!!.isCompleted()) {
                Toast.makeText(this, "Completed", Toast.LENGTH_SHORT).show()
            } else if (printJob!!.isStarted()) {
                Toast.makeText(this, "isStarted", Toast.LENGTH_SHORT).show()
            } else if (printJob!!.isBlocked()) {
                Toast.makeText(this, "isBlocked", Toast.LENGTH_SHORT).show()
            } else if (printJob!!.isCancelled()) {
                Toast.makeText(this, "isCancelled", Toast.LENGTH_SHORT).show()
            } else if (printJob!!.isFailed()) {
                Toast.makeText(this, "isFailed", Toast.LENGTH_SHORT).show()
            } else if (printJob!!.isQueued()) {
                Toast.makeText(this, "isQueued", Toast.LENGTH_SHORT).show()
            }
        }
        printBtnPressed = false
    }
}