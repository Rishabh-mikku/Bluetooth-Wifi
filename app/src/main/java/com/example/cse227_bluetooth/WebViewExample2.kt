package com.example.cse227_bluetooth

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class WebViewExample2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        val wv = findViewById<View>(R.id.webview) as WebView
        val customHtml = "<html>" +
                "<body>" +
                "<h1>Welcome to World of Sports</h1>" +
                "<h2>Welcome to World of Sports</h2>" +
                "<h3>Welcome to World of Sports</h3>" +
                "<p>It's a static web HTML content.</p>" +
                "</body>" +
                "</html>"
        wv.loadData(customHtml, "text/html", "UTF-8")
    }
}