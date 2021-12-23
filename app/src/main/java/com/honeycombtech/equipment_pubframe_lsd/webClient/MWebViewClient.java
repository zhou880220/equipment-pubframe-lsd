package com.honeycombtech.equipment_pubframe_lsd.webClient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;


/**
 * Created by zhoujr on 2020/1/7
 */
public class MWebViewClient extends WebViewClient {
    private WebView webView;
    private Context context;
    private ProgressBar progressBar;
    private View error_layout;

    public MWebViewClient(WebView webView) {
        this.webView = webView;
    }

    public MWebViewClient(WebView webView, Context context, ProgressBar progressBar, View error_layout) {
        this.webView = webView;
        this.context = context;
        this.progressBar = progressBar;
        this.error_layout = error_layout;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url == null) return false;
        if (url.startsWith("http:") || url.startsWith("https:")) {
            view.loadUrl(url);
            return false;
        } else {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            } catch (Exception e) {
                // ToastUtils.showShort("暂无应用打开此链接");
            }
            return true;
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
//        progressBar.setVisibility(View.VISIBLE);
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
//        progressBar.setVisibility(View.GONE);
        if(!webView.getSettings().getLoadsImagesAutomatically()) {
            webView.getSettings().setLoadsImagesAutomatically(true);
        }
        super.onPageFinished(view, url);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return;
        }
        ChangErrorView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        if (request.isForMainFrame()){
            ChangErrorView();
        }
    }

    private void ChangErrorView() {
        webView.setVisibility(View.GONE);
        error_layout.setVisibility(View.VISIBLE);
    }
}
