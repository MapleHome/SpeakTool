package com.speektool.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.speektool.R;
import com.speektool.impl.platforms.PartnerPlat.LoginCallbackReceiver;
import com.speektool.manager.AppManager;
import com.speektool.tasks.ThreadPoolWrapper;
import com.speektool.ui.layouts.MyProgress;

/**
 * 网页
 * 
 * @author shaoshuai
 * 
 */
public class WebActivity extends FragmentActivity implements OnClickListener {
	@ViewInject(R.id.iv_back)
	private ImageView iv_back;// 返回
	@ViewInject(R.id.tv_title)
	private TextView tv_title;// 标题

	@ViewInject(R.id.mp_ProgressBar)
	private MyProgress mp_ProgressBar;// 加载圈
	@ViewInject(R.id.wv_content)
	private WebView webView;// 网页

	public static final String EXTRA_TITLE = "extra_title";
	public static final String EXTRA_URL = "extra_url";
	private ThreadPoolWrapper pool = ThreadPoolWrapper.newThreadPool(1);

	private Context mContext;
	private String title;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		mContext = getApplicationContext();
		ViewUtils.inject(this);

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
		webView.addJavascriptInterface(new JsApi(), "speaktool");
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
		// TODO Auto-generated method stub
		Intent it = new Intent(LoginCallbackReceiver.ACTION_LOGIN_CANCEL);
		mContext.sendBroadcast(it);

		webView.freeMemory();
		webView.destroy();

		pool.shutdownNow();// 关闭线程
		super.onDestroy();

		// android.os.Process.killProcess(android.os.Process.myPid());
	}

	// @Override
	// public void onBackPressed() {
	// if (webView.canGoBack()) {
	// webView.goBack();
	// } else {
	// super.onBackPressed();
	// }
	// }

	private class JsApi {
		@JavascriptInterface
		public void onLoginCallback(String account, String token) {
			// send broadcast to ui process.
			Intent it = new Intent(LoginCallbackReceiver.ACTION_LOGIN_CALLBACK);
			it.putExtra(LoginCallbackReceiver.EXTRA_ACCOUNT, account);
			it.putExtra(LoginCallbackReceiver.EXTRA_TOKEN, token);
			mContext.sendBroadcast(it);
		}

		@JavascriptInterface
		public String getAppSign() {
			return AppManager.getAppSignEncodedByMd5(mContext);
		}
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
