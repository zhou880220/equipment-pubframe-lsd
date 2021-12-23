package com.honeycombtech.equipment_pubframe_lsd.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.ProgressBar;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.honeycombtech.equipment_pubframe_lsd.R;
import com.honeycombtech.equipment_pubframe_lsd.uitl.CleanDataUtils;
import com.honeycombtech.equipment_pubframe_lsd.uitl.SPUtils;
import com.honeycombtech.equipment_pubframe_lsd.webClient.MWebChromeClient;
import com.honeycombtech.equipment_pubframe_lsd.webClient.MyWebViewClient;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
* author : zhoujr
* date : 2021/2/19 15:40
* desc : 
*/
public class MainActivity extends Activity {
    /*****************prams******************/
    private final String TAG = "MainActivity";
    //调试  http://172.16.23.34:3001
    private String content_url = "http://fgk8sequipment.zhizaoyun.com:30010/equipment/lsd/login?r="+new Date().getTime(); //"http://119.45.19.115/";//https://mestest-lsd.zhizaoyun.com:30443/

    //生产环境
//    private String content_url = "http://indapp-lsd.zhizaoyun.com/login?r="+new Date().getTime();

    /*****************view******************/
    @BindView(R.id.Solgan_wv)
    BridgeWebView solgan_wv;
    @BindView(R.id.progress_bar)
    ProgressBar progress_bar;
    @BindView(R.id.error_rl)
    View error_layout;
    @BindView(R.id.loding_page)
    View lodingPage;

    /*****************object******************/
    private WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
        init();
    }

    private void initView() {

        solgan_wv.setBackgroundColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= 19) {
            solgan_wv.getSettings().setLoadsImagesAutomatically(true);
        } else {
            solgan_wv.getSettings().setLoadsImagesAutomatically(false);
        }
        //设置Webview需要的条件
//        WebSettings webSettings = solgan_wv.getSettings();
//        String userAgentString = webSettings.getUserAgentString();
//        webSettings.setUserAgentString(userAgentString + "; application-center");
//        if (webSettings != null) {
            setSettings();
//        }

        solgan_wv.setDefaultHandler(new MyHandlerCallback());
        solgan_wv.loadUrl(content_url);

        //与浏览器交互(设置用户信息)
        solgan_wv.registerHandler("saveData", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.e(TAG, "handler = saveData, data from web = " + data);
                try{
                    if (!TextUtils.isEmpty(data)) {
                        SPUtils.getInstance().put("webData", data);
                        function.onCallBack("android receive success");
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


//        获取用户信息
        solgan_wv.registerHandler("getUserInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try{
                    String _data = (String)SPUtils.getInstance().get("webData", "");
                    Log.e(TAG, "_data : "+_data);
                    function.onCallBack(_data);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

//        清除用户信息
        solgan_wv.registerHandler("clearUserInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try{
                    SPUtils.getInstance().remove("webData");
                    Log.e(TAG, "clearUserInfo : success");
                    function.onCallBack("android clear userInfo success");
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        //退出
        solgan_wv.registerHandler("CloseApp", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try{
                    initClearCache();
                    finish();
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });



    }

    private void init() {
        error_layout = findViewById(R.id.error_rl);
        //处理页面加载各个阶段
//        MWebViewClient mWebViewClient = new MWebViewClient(solgan_wv, getApplicationContext(), progress_bar, error_layout);
        MWebChromeClient myChromeWebClient = new MWebChromeClient(this, progress_bar);
        solgan_wv.setWebChromeClient(myChromeWebClient);
        MyWebViewClient myWebViewClient = new MyWebViewClient(solgan_wv, error_layout);
        solgan_wv.setWebViewClient(myWebViewClient);
        //提供网页加载过程中提供的数据
//        MWebChromeClient mWebChromeClient = new MWebChromeClient(getApplicationContext(), progress_bar);
//        solgan_wv.setWebChromeClient(mWebChromeClient);
        myChromeWebClient.setOnCityClickListener(new MWebChromeClient.OnCityChangeListener() {
            @Override
            public void onCityClick(int newProgress) {
                Log.i(TAG, "onCityClick: "+newProgress);
                if (newProgress == 100) {
//                    lodingPage.setVisibility(View.GONE);
                    progress_bar.setVisibility(View.GONE);
                }
            }
        });
    }

    class MyHandlerCallback extends DefaultHandler {
        @Override
        public void handler(String data, CallBackFunction function) {
            super.handler(data, function);
            if (null != function) {
                Log.e(TAG, "收到JS消息回复。" );
                function.onCallBack("收到JS消息回复。");
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initClearCache() {
        String totalCacheSize = CleanDataUtils.getTotalCacheSize(Objects.requireNonNull(MainActivity.this));
        CleanDataUtils.clearAllCache(Objects.<Context>requireNonNull(MainActivity.this));
        String totalCacheSize_again = CleanDataUtils.getTotalCacheSize(Objects.<Context>requireNonNull(MainActivity.this));
        Log.i(TAG,"前："+totalCacheSize+"------后："+totalCacheSize_again+"");
    }

    private void setSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            solgan_wv.getSettings().setSafeBrowsingEnabled(false);
        }
        //声明子类
        webSettings = solgan_wv.getSettings();
        //js交互
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        //自适应屏幕
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setTextZoom(100);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //缩放操作
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        //细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDefaultTextEncodingName("utf-8");
    }

}