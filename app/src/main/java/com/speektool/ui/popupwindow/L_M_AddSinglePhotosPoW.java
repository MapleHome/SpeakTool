package com.speektool.ui.popupwindow;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.speektool.R;
import com.speektool.SpeekToolApp;
import com.speektool.adapters.AdapterPhotoDirs;
import com.speektool.adapters.AdapterPhotos;
import com.speektool.api.AsyncDataLoader;
import com.speektool.api.PhotoImporter.PickPhotoCallback;
import com.speektool.base.AbsListScrollListener;
import com.speektool.base.BasePopupWindow;
import com.speektool.bean.LocalPhotoDirBean;
import com.speektool.bean.PicDataHolder;
import com.speektool.busevents.LocalPhotoDirIconLoadedEvent;
import com.speektool.busevents.LocalPictureThumbnailLoadedEvent;
import com.speektool.factory.AsyncDataLoaderFactory;
import com.speektool.tasks.TaskLoadLocalPhotos;
import com.speektool.tasks.TaskLoadLocalPhotos.LoadLocalPhotosCallback;
import com.speektool.ui.layouts.ItemViewLocalPhotoDirs;
import com.speektool.ui.layouts.MulticheckableView;
import com.speektool.utils.BitmapScaleUtil;

import de.greenrobot.event.EventBus;

/**
 * 左侧功能栏——更多——添加图片——挑选单张照片
 * 
 * @author shaoshuai
 * 
 */
public class L_M_AddSinglePhotosPoW extends BasePopupWindow implements OnClickListener, OnDismissListener,
		OnTouchListener {
	private ViewFlipper viewFlipperPhotos;
	
	protected PickPhotoCallback mDraw;
	private ListView listViewPhotoDirs;
	private GridView gridViewPhotos;
	private View llBack;
	private TextView tvSecondPageTitle;

	protected TextView tvSecondPageFinish;
	private View llBackOverlay;
	private View llFinishOverlay;
	protected AdapterPhotos mAdapterPhotos;

	private static final int FIRST_PAGE = 0;
	private static final int SECOND_PAGE = 1;

	private WeakReference<AdapterPhotoDirs> mAdapterPhotoDirsRef;
	private AsyncDataLoader<String, PicDataHolder> mDataLoader;
	private final AsyncDataLoader<String, byte[]> mLocalPhotoDirLoader = AsyncDataLoaderFactory
			.newLocalPhotoDirLoader();

	@Override
	public View getContentView() {
		return LayoutInflater.from(mContext).inflate(R.layout.pow_pickphotos, null);
	}

	public L_M_AddSinglePhotosPoW(Context context, View anchor, PickPhotoCallback draw) {
		this(context, anchor, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, draw);
	}

	public L_M_AddSinglePhotosPoW(Context context, View anchor, int w, int h, PickPhotoCallback draw) {
		super(context, anchor, w, h);
		EventBus.getDefault().register(this);

		mDraw = draw;
		// mDataLoader = loader;
		mDataLoader = AsyncDataLoaderFactory.newLocalPicturesIconAsyncLoader();

		viewFlipperPhotos = (ViewFlipper) mRootView.findViewById(R.id.viewFlipperPhotos);
		listViewPhotoDirs = (ListView) mRootView.findViewById(R.id.listViewPhotoDirs);
		
		// 第二页
		llBack = mRootView.findViewById(R.id.llBack);// 返回
		gridViewPhotos = (GridView) mRootView.findViewById(R.id.gridViewPhotos);
		llBackOverlay = mRootView.findViewById(R.id.llBackOverlay);
		tvSecondPageTitle = (TextView) mRootView.findViewById(R.id.tvSecondPageTitle);
		tvSecondPageFinish = (TextView) mRootView.findViewById(R.id.tvSecondPageFinish);
		
		llFinishOverlay = mRootView.findViewById(R.id.llFinishOverlay);

		initAnim(anchor.getContext());

		AdapterPhotoDirs mAdapterPhotoDirs = new AdapterPhotoDirs(mContext, null);
		mAdapterPhotoDirsRef = new WeakReference<AdapterPhotoDirs>(mAdapterPhotoDirs);
		listViewPhotoDirs.setAdapter(mAdapterPhotoDirs);

		mAdapterPhotos = new AdapterPhotos(anchor.getContext(), null);
		gridViewPhotos.setAdapter(mAdapterPhotos);

		this.setOnDismissListener(this);
		listViewPhotoDirs.setOnItemClickListener(listItemClickListener);
		listViewPhotoDirs.setOnScrollListener(listOnScrollListener);
		gridViewPhotos.setOnItemClickListener(getOnGridItemClickListener());
		gridViewPhotos.setOnScrollListener(gridAbsListScrollListener);
		llBack.setOnClickListener(this);
		llBack.setOnTouchListener(this);
		tvSecondPageFinish.setOnClickListener(this);
		tvSecondPageFinish.setOnTouchListener(this);
		//
		loadDirs();
	}

	private AbsListScrollListener gridAbsListScrollListener = new AbsListScrollListener() {

		@Override
		public void whenIdle() {
			for (int i = mfirstVisibleItem; i <= mlastvisibleItem; i++) {
				final String imageUrl = (String) mAdapterPhotos.getItem(i);
				if (TextUtils.isEmpty(imageUrl))
					continue;
				final MulticheckableView item = (MulticheckableView) gridViewPhotos.findViewWithTag(imageUrl);
				if (item == null)
					continue;
				final PicDataHolder cache = mDataLoader.load(imageUrl, item.getWidth(), item.getHeight());
				if (cache != null) {
					if (BitmapScaleUtil.isGif(imageUrl)) {
						GifDrawable gifd;
						try {
							gifd = new GifDrawable(cache.gif);
							item.setImage(gifd);
						} catch (IOException e) {
							e.printStackTrace();
							item.setImage(new BitmapDrawable(getErrorBmp()));
						}
					} else {// bmp.
						Bitmap bpScaled = BitmapFactory.decodeByteArray(cache.bpScaled, 0, cache.bpScaled.length);
						item.setImage(new BitmapDrawable(bpScaled));
					}
				} else {
					item.setLoading();
				}
			}
		}

		@Override
		public void whenFling() {
			mDataLoader.cancelAll();
		}
	};

	private LoadLocalPhotosCallback mLoadLocalPhotosCallback = new LoadLocalPhotosCallback() {
		@Override
		public void onFinish(List<LocalPhotoDirBean> dirs) {
			AdapterPhotoDirs adp = mAdapterPhotoDirsRef.get();
			if (adp != null) {
				adp.refresh(dirs);
				SpeekToolApp.getUiHandler().post(new Runnable() {
					@Override
					public void run() {
						int first = listViewPhotoDirs.getFirstVisiblePosition();
						int last = listViewPhotoDirs.getLastVisiblePosition();
						listOnScrollListener.setVisibleItems(first, last);
						listOnScrollListener.whenIdle();
					}
				});
			}
		}
	};

	private void loadDirs() {
		new Thread(new TaskLoadLocalPhotos(mLoadLocalPhotosCallback)).start();
	}

	private AbsListScrollListener listOnScrollListener = new AbsListScrollListener() {

		@Override
		public void whenFling() {
			mLocalPhotoDirLoader.cancelAll();
		}

		@Override
		public void whenIdle() {
			AdapterPhotoDirs adp = mAdapterPhotoDirsRef.get();
			if (adp == null)
				return;
			for (int i = mfirstVisibleItem; i <= mlastvisibleItem; i++) {
				LocalPhotoDirBean bean = (LocalPhotoDirBean) adp.getItem(i);
				String iconpath = bean.getDirIconPath();
				ItemViewLocalPhotoDirs itemview = (ItemViewLocalPhotoDirs) listViewPhotoDirs.findViewWithTag(iconpath);
				if (itemview == null)
					continue;
				byte[] iconbytesCache = mLocalPhotoDirLoader.load(iconpath);
				if (iconbytesCache != null) {
					Bitmap bpScaled = BitmapFactory.decodeByteArray(iconbytesCache, 0, iconbytesCache.length);
					itemview.setDirIcon(bpScaled);
				} else {// no cache.
						// ignore.
					itemview.setDirIcon(getDefBmp());
				}
			}// for.
		}
	};

	private Bitmap getDefBmp() {
		return BitmapFactory.decodeResource(SpeekToolApp.app().getResources(), R.drawable.defalut_icon);
	}

	public void onEventMainThread(LocalPhotoDirIconLoadedEvent event) {
		String key = event.getKey();
		ItemViewLocalPhotoDirs itemview = (ItemViewLocalPhotoDirs) listViewPhotoDirs.findViewWithTag(key);
		if (itemview == null)
			return;
		//
		itemview.setDirIcon(event.getIcon());
	}

	private Animation intoAnim_in;
	private Animation intoAnim_out;
	private Animation backAnim_in;
	private Animation backAnim_out;

	private void initAnim(Context context) {
		intoAnim_in = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
		intoAnim_out = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right);
		backAnim_in = AnimationUtils.loadAnimation(context, R.anim.viewfliper_slide_in_right);
		backAnim_out = AnimationUtils.loadAnimation(context, R.anim.viewfliper_slide_out_left);
	}

	private void setIntoAnimation() {
		viewFlipperPhotos.setInAnimation(backAnim_in);
		viewFlipperPhotos.setOutAnimation(backAnim_out);
	}

	private void setBackAnimation() {
		viewFlipperPhotos.setInAnimation(intoAnim_in);
		viewFlipperPhotos.setOutAnimation(intoAnim_out);
	}

	protected OnItemClickListener getOnGridItemClickListener() {
		return gridItemClickListener;
	}

	private OnItemClickListener gridItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String imgPath = (String) parent.getAdapter().getItem(position);
			mDraw.onPhotoPicked(imgPath);
			//
			dismiss();
		}
	};

	private OnItemClickListener listItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			LocalPhotoDirBean item = (LocalPhotoDirBean) parent.getAdapter().getItem(position);
			setIntoAnimation();
			viewFlipperPhotos.setDisplayedChild(SECOND_PAGE);
			
			tvSecondPageTitle.setText(item.getDirName());
			mAdapterPhotos = new AdapterPhotos(mContext, item.getImagePathList());
			gridViewPhotos.setAdapter(mAdapterPhotos);

			SpeekToolApp.getUiHandler().post(new Runnable() {

				@Override
				public void run() {
					int first = gridViewPhotos.getFirstVisiblePosition();
					int last = gridViewPhotos.getLastVisiblePosition();
					gridAbsListScrollListener.setVisibleItems(first, last);
					gridAbsListScrollListener.whenIdle();
				}
			});
		}
	};

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.llBack:
			setBackAnimation();
			viewFlipperPhotos.setDisplayedChild(FIRST_PAGE);
			break;
		}
	}

	@Override
	public void onDismiss() {
		EventBus.getDefault().unregister(this);
		mLocalPhotoDirLoader.destroy();
		mDataLoader.destroy();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			down(v);
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			up(v);
			break;
		}
		return false;
	}

	private void up(View v) {
		if (v == llBack)
			llBackOverlay.setVisibility(View.GONE);
		else if (v == tvSecondPageFinish) {
			llFinishOverlay.setVisibility(View.GONE);
		}
	}

	private void down(View v) {
		if (v == llBack)
			llBackOverlay.setVisibility(View.VISIBLE);
		else if (v == tvSecondPageFinish) {
			llFinishOverlay.setVisibility(View.VISIBLE);
		}
	}

	private Bitmap getErrorBmp() {
		return BitmapFactory.decodeResource(SpeekToolApp.app().getResources(), R.drawable.error);
	}

	public void onEventMainThread(final LocalPictureThumbnailLoadedEvent event) {
		String key = event.getKey();
		final MulticheckableView item = (MulticheckableView) gridViewPhotos.findViewWithTag(key);
		if (item != null) {
			if (event.isError()) {
				item.setImage(new BitmapDrawable(getErrorBmp()));
				return;
			}
			item.setImage(event.getIcon());
		}
	}
}
