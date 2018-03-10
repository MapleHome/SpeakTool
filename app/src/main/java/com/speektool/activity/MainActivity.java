package com.speektool.activity;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;

import com.aidl.UploadAIDL;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.speektool.R;
import com.speektool.api.AsyncDataLoader;
import com.speektool.api.Draw.PlayMode;
import com.speektool.base.BasePopupWindow.WeiZhi;
import com.speektool.bean.SearchCategoryBean;
import com.speektool.busevents.CourseThumbnailLoadedEvent;
import com.speektool.busevents.RefreshCourseListEvent;
import com.speektool.factory.AsyncDataLoaderFactory;
import com.speektool.manager.AppUpdateManager;
import com.speektool.receiver.UploadStateReceiver;
import com.speektool.service.KeepAliveService;
import com.speektool.service.UploadService;
import com.speektool.tasks.TaskLoadRecordCategories;
import com.speektool.tasks.TaskLoadRecordCategories.RecordTypeLoadListener;
import com.speektool.tasks.ThreadPoolWrapper;
import com.speektool.ui.fragment.HomePage;
import com.speektool.ui.layouts.ItemViewLocalRecord;
import com.speektool.ui.layouts.SearchView;
import com.speektool.ui.popupwindow.CategoryPoW;
import com.speektool.ui.popupwindow.CategoryPoW.SearchCategoryChangedListener;
import com.speektool.utils.T;

import de.greenrobot.event.EventBus;

/**
 * 主界面
 * 
 * @author shaoshuai
 * 
 */
public class MainActivity extends FragmentActivity implements OnClickListener {
	// 顶部功能条
	@ViewInject(R.id.tvMakeVideo)
	private TextView tvMakeVideo;// 新建按钮
	@ViewInject(R.id.iv_back)
	private ImageView iv_back;// 返回
	@ViewInject(R.id.searchView)
	public SearchView searchView;// 整个搜索框
	// 底部功能条
	@ViewInject(R.id.ivSetting)
	private ImageView ivSetting;// 设置按钮

	private HomePage mHomePage;
	private Context mContext;
	public SearchCategoryBean mCurSearchType;
	public String mCurSearchKeyWords = null;

	private AsyncDataLoader<String, Bitmap> mAppIconAsyncLoader = AsyncDataLoaderFactory
			.newCourseThumbnailAsyncLoader();
	private ThreadPoolWrapper singleExecutor = ThreadPoolWrapper.newThreadPool(1);
	private UploadStateReceiver mUploadStateReceiver;
	private UploadAIDL mUploadAIDL;
	private ServiceConnection mServiceConnection;
	private AppUpdateManager mAppUpdateManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		ViewUtils.inject(this);

		initView();
		initData();
		initListener();

	}

	private void initView() {
		// 注册EventBus订阅者
		EventBus.getDefault().register(this);
		// 注册广播接受者
		mUploadStateReceiver = new UploadStateReceiver();
		this.registerReceiver(mUploadStateReceiver, new IntentFilter(UploadService.ACTION_UPLOAD_STATE));
		//
		ShareSDK.initSDK(this);
		KeepAliveService.start(this);
		// 检查更新
		mAppUpdateManager = new AppUpdateManager(this, singleExecutor, false);
		mAppUpdateManager.checkAppUpdate();
		// 填充视图
		mHomePage = new HomePage();
		loadView(mHomePage);
	}

	private void initData() {
		// 搜索框
		setSearchView(new SearchCategoryBean(0, "全部分类", SearchCategoryBean.CID_ALL), null);
		//
		mServiceConnection = new ServiceConnection() {
			// 服务中断
			@Override
			public void onServiceDisconnected(ComponentName name) {
				mUploadAIDL = null;
			}

			// 服务连接
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mUploadAIDL = UploadAIDL.Stub.asInterface(service);
			}
		};
		this.bindService(new Intent(mContext, UploadService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
	}

	/** 更新搜索框 */
	public void setSearchView(SearchCategoryBean mSearchType, String mSearchKeyWords) {
		if (TextUtils.isEmpty(mSearchKeyWords)) {// 显示全部
			tvMakeVideo.setVisibility(View.VISIBLE);
			iv_back.setVisibility(View.INVISIBLE);
		} else {// 显示搜索的部分
			iv_back.setVisibility(View.VISIBLE);
			tvMakeVideo.setVisibility(View.INVISIBLE);
		}

		searchView.setCategory(mSearchType);// 设置搜索框
		searchView.setSearchKey(mSearchKeyWords);

		mCurSearchType = mSearchType;
		mCurSearchKeyWords = mSearchKeyWords;
	}

	private void initListener() {
		tvMakeVideo.setOnClickListener(this);// 新建
		iv_back.setOnClickListener(this);// 返回
		ivSetting.setOnClickListener(this);// 设置
		//
		searchView.setSearchClickListener(this);
		searchView.setDropdownClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvMakeVideo:// 新建
			make();
			break;
		case R.id.iv_back:// 返回
			setSearchView(new SearchCategoryBean(0, "全部分类", SearchCategoryBean.CID_ALL), null);
			mHomePage.refreshIndexPage();
			break;
		case R.id.layDropdownHandle:// 分类下拉框
			showPopDropdown(v);
			break;
		case R.id.ivSearch:// 搜索
			search();
			break;
		case R.id.ivSetting:// 设置
			toUserMGPage(UserFMActivity.INIT_USER_INFO);
			break;
		}
	}

	public void onEventMainThread(CourseThumbnailLoadedEvent event) {
		ItemViewLocalRecord item = (ItemViewLocalRecord) mHomePage.gridViewAllRecords.findViewWithTag(event.getKey());
		if (item != null)
			item.setThumbnail(event.getIcon());

	}

	public void onEventMainThread(RefreshCourseListEvent event) {
		mHomePage.refreshIndexPage();
	}

	// =========================================================================

	private boolean isonStop = false;

	@Override
	protected void onRestart() {
		if (isonStop) {
			isonStop = false;
			// EventBus.getDefault().post(new RefreshCourseListEvent());
		}
		super.onRestart();
	}

	@Override
	protected void onStop() {
		isonStop = true;
		super.onStop();
	}

	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				T.showShort(mContext, "再按一次退出程序");
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				// System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);// 解除注册EventBus订阅者
		singleExecutor.shutdownNow();
		mAppIconAsyncLoader.destroy();
		mAppIconAsyncLoader = null;
		// mLocalPicturesIconAsyncLoader.destroy();
		// mLocalPicturesIconAsyncLoader = null;
		ShareSDK.stopSDK(this);

		KeepAliveService.stop(this);
		this.unregisterReceiver(mUploadStateReceiver);
		this.unbindService(mServiceConnection);
		super.onDestroy();
	}

	/** 横竖屏配置改变 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		mHomePage = new HomePage();
		replacePage(mHomePage);

	}

	/** 替换视图 */
	public void replacePage(Fragment fg) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.id_content, fg);
		ft.commitAllowingStateLoss();
	}

	// =========================================================================
	/** 是否需要上传 */
	public boolean isUploading(String courseKey) {
		if (mUploadAIDL == null) {
			return false;
		} else {
			try {
				return mUploadAIDL.isUploading(courseKey);
			} catch (RemoteException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	/** 更新上传状态UI */
	public void updateUploadStateUi(String tag, boolean isUploading) {
		ItemViewLocalRecord item = (ItemViewLocalRecord) mHomePage.gridViewAllRecords.findViewWithTag(tag);
		if (item != null)
			item.setUploadingState(isUploading);

	}

	/** 更新上传进度 */
	public void updateUploadProgressUi(String tag, int progress) {
		ItemViewLocalRecord item = (ItemViewLocalRecord) mHomePage.gridViewAllRecords.findViewWithTag(tag);
		if (item != null)
			item.setProgress(progress);
	}

	// ===========================================================================
	/** 搜索 */
	private void search() {
		SearchCategoryBean type = searchView.getCategory();
		String keywords = searchView.getSearchKeywords();
		if (TextUtils.isEmpty(keywords)) {
			T.showShort(mContext, "搜索关键字不能为空！");
			return;
		}
		// 和上次完全相同
		if (mCurSearchType.equals(type) && keywords.equals(mCurSearchKeyWords)) {
			T.showShort(mContext, "请不要频繁点击！");
			return;
		}

		mHomePage.searchRecords(type, keywords, true);
	}

	private CategoryPoW popupWindow;

	private void showPopDropdown(View anchor) {
		popupWindow = new CategoryPoW(mContext, anchor, anchor, new SearchCategoryChangedListener() {
			@Override
			public void onSearchCategoryChanged(SearchCategoryBean categoryNew) {
				SearchCategoryBean befCategory = searchView.getCategory();
				if (!befCategory.equals(categoryNew)) {
					setSearchView(categoryNew, "");
					// 通过EventBus订阅者发送消息
					EventBus.getDefault().post(new RefreshCourseListEvent());
				}
			}
		});
		popupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				popupWindow = null;
			}
		});
		// int offsetx = popupWindow.getShowOffsetXPix();
		// int offsetY = popupWindow.getShowOffsetYPix();
		singleExecutor.execute(new TaskLoadRecordCategories(new RecordTypeLoadListener() {

			@Override
			public void onRecordTypeLoaded(List<SearchCategoryBean> result) {
				if (popupWindow != null) {
					popupWindow.refreshCategoryList(result);
				}
			}
		}, true));
		popupWindow.showPopupWindow(WeiZhi.Bottom);
		// (Gravity.NO_GRAVITY, offsetx, offsetY,
		// PopupMenuWindow.ANIM_GROW_FORM_TOP);
	}

	/** 加载填充视图 */
	private void loadView(Fragment fg) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.id_content, fg).commit();
	}

	/** 新建一个讲讲画板 */
	private void make() {
		Intent it = new Intent(this, DrawActivity.class);
		it.putExtra(DrawActivity.EXTRA_PLAY_MODE, PlayMode.MAKE);
		startActivity(it);
	}

	/** 去用户设置页面 */
	private void toUserMGPage(int viewIndex) {
		Intent intent = new Intent(mContext, UserFMActivity.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(UserFMActivity.IN_LOAGING_PAGE_INDEX, viewIndex);// 默认加载界面
		startActivity(intent);// 开启目标Activity
	}
}