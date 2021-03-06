package com.zzu.ehome.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.zzu.ehome.R;
import com.zzu.ehome.activity.SupperBaseActivity;

import static com.zzu.ehome.R.id.webView;

/**
 * Created by Administrator on 2017/1/4.
 */

public class WebPlatmFramet extends BaseFragment {
    private View mView;
    private SupperBaseActivity activity;
    private String url;
    private WebView mWebView;
    private boolean isFirst=false;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (SupperBaseActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.platm_smart_webview, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        url = getArguments().getString("url");
        mView = view;
        initViews();
    }

    private void initViews() {
        mWebView = (WebView) mView.findViewById(R.id.webview);
//        pg1 = (ProgressBar) mView.findViewById(R.id.progressBar1);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mWebView.loadUrl(url);
                return true;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                   stopProgressDialog();
                } else {
                    if(!isFirst) {
                        startProgressDialogTitle("正在加载中...");
                        isFirst=true;
                    }
                }
            }
        });
        mWebView.loadUrl(url);

    }

    public static Fragment getInstance(String path) {
        WebPlatmFramet framet = new WebPlatmFramet();
        Bundle bundle = new Bundle();
        bundle.putString("url", path);
        framet.setArguments(bundle);
        return framet;
    }


    @Override
    protected void lazyLoad() {

    }

    public void reload(String url) {

    }


}
