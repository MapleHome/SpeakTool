package com.speaktool.ui.Setting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.speaktool.R;
import com.speaktool.tasks.ThreadPoolWrapper;
import com.speaktool.view.layouts.MyProgress;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 网页
 *
 * @author shaoshuai
 */
public class WebActivity extends FragmentActivity implements OnClickListener {
    @BindView(R.id.iv_back) ImageView iv_back;// 返回
    @BindView(R.id.tv_title) TextView tv_title;// 标题
    @BindView(R.id.mp_ProgressBar) MyProgress mp_ProgressBar;// 加载圈
    @BindView(R.id.wv_content) WebView webView;// 网页

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_URL = "extra_url";
    private ThreadPoolWrapper pool = ThreadPoolWrapper.newThreadPool(1);

    private String title;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);

        Intent it = getIntent();
        title = (String) it.getSerializableExtra(EXTRA_TITLE);
        url = (String) it.getSerializableExtra(EXTRA_URL);

        initView();
        initListener();
        initDate();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        if (TextUtils.isEmpty(title)) {
            tv_title.setText("新闻资讯");// 标题
        } else {
            tv_title.setText(title);// 标题
        }
        mp_ProgressBar.setVisibility(View.GONE);
        //
        WebSettings setting = webView.getSettings();
        setting.setUseWideViewPort(true);// 扩大比例的缩放
        setting.setJavaScriptEnabled(true);// 支持javascript
        setting.setBuiltInZoomControls(true);// 设置出现缩放工具
        // 自适应屏幕
        // setting.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        setting.setLoadWithOverviewMode(true);
        // setting.setSupportZoom(true);// 设置可以支持缩放

    }

    private void initDate() {

        webView.loadUrl(url);// "http://m.zol.com/tuan/"

    }

    private void initListener() {
        iv_back.setOnClickListener(this);

        webView.setWebChromeClient(new ChromClient());
        webView.setWebViewClient(new WebViewClient() {
            // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (TextUtils.isEmpty(url))
                    return true;
                view.loadUrl(url);
                return true;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:// 返回
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        webView.freeMemory();
        webView.destroy();

        pool.shutdownNow();// 关闭线程
        super.onDestroy();

    }


    private class ChromClient extends WebChromeClient {
        private View myView = null;
        private CustomViewCallback myCallback = null;

        /**
         * run when fullscreen.
         */
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (myCallback != null) {
                myCallback.onCustomViewHidden();
                myCallback = null;
                return;
            }
            ViewGroup parent = (ViewGroup) webView.getParent();
            String s = parent.getClass().getName();
            parent.removeView(webView);
            parent.addView(view);
            myView = view;
            myCallback = callback;
        }

        @Override
        public void onHideCustomView() {
            // long id = Thread.currentThread().getId();
            if (myView != null) {
                if (myCallback != null) {
                    myCallback.onCustomViewHidden();
                    myCallback = null;
                }
                ViewGroup parent = (ViewGroup) myView.getParent();
                parent.removeView(myView);
                parent.addView(webView);
                myView = null;
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress >= 100) {
                mp_ProgressBar.setProgress(100);
                mp_ProgressBar.setVisibility(View.GONE);
            } else {
                mp_ProgressBar.setVisibility(View.VISIBLE);
                mp_ProgressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }
}
