package com.speaktool.api;

import java.io.Serializable;

import android.content.Context;

import com.speaktool.api.Page.Page_BG;
import com.speaktool.bean.MusicBean;
import com.speaktool.bean.RecordUploadBean;
import com.speaktool.impl.recorder.PageRecorder;
import com.speaktool.impl.recorder.RecorderContext;
import com.speaktool.impl.shapes.EditWidget;
import com.speaktool.impl.shapes.ImageWidget;

public interface Draw extends PhotoImporter {
	Context context();

	/** 上一页 */
	void preBoardClick();

	/** 下一页 */
	void nextBoardClick();

	/** 获取当前画纸 */
	Page getCurrentBoard();

	/** 根据索引获取界面 */
	Page getPageAtPosition(int position);

	/**
	 * 创建画板纸张
	 * 
	 * @param bgType
	 *            - 纸张背景类型
	 * @param position
	 *            - 纸张在画册中的索引
	 * @param pageId
	 *            - 纸张ID
	 */
	void createPageImpl(Page_BG bgType, int position, int pageId);

	/**
	 * 设置指定id的画纸为当前活动画纸
	 * 
	 * @param pageId
	 *            - 画纸ID
	 */
	void setActivePageImpl(int pageId);

	/**
	 * 设置指定id的画纸为当前互动画纸
	 * 
	 * @param pageId
	 *            - 画纸ID
	 */
	void setActivePageSendcmd(int pageId);

	Page deletePageImpl(int pageId);

	//
	/** 获取画纸页面记录器 */
	PageRecorder getPageRecorder();

	/** 获取画纸页面记录器上下文 */
	RecorderContext getRecorderContext();

	/** 创建画纸ID */
	int makePageId();

	//
	void newEmptyBoardClick();

	/**
	 * 复制当前页面
	 * 
	 * @param option
	 *            操作类型
	 */
	void copyPageClick(String option);

	/**
	 * 复制页面图片
	 * 
	 * @param srcPageId
	 *            源页面ID
	 * @param destPageId
	 *            目标页面ID
	 * @param option
	 *            操作类型
	 */
	void copyPageImpl(int srcPageId, int destPageId, String option);

	/**
	 * 清除页面内容
	 * 
	 * @param pageId
	 *            页面ID
	 * @param option
	 *            清除类型
	 */
	void clearPageClick(int pageId, String option);

	/**
	 * 清除页面图片
	 * 
	 * @param pageId
	 * @param option
	 */
	void clearPageImpl(int pageId, String option);

	void postTaskToUiThread(Runnable task);

	void removeAllHandlerTasks();

	//
	void setPageBackgroundImpl(int pageId, Page_BG bgType);

	void setPageBackgroundClick(int pageId, Page_BG bgType);

	/** 背景暗淡 */
	void dim();

	/** 背景正常 */
	void undim();

	/** 启动记录器 */
	void bootRecord();

	/** 暂停记录器 */
	void pauseRecord();

	/** 继续记录器 */
	void continueRecord();

	/** 保存课程记录 */
	void saveRecord(RecordUploadBean saveInfo);

	/** 删除课程记录 */
	void deleteRecord();

	/** 退出画板 */
	void onExitDraw();

	void exitDrawWithoutSave();

	/** 显示视频控制器 */
	void showVideoController();

	//
	void setRecordDir(String dir);

	String getRecordDir();

	/** 获取画板模式 */
	PlayMode getPlayMode();

	/** 画板模式 -【播放】or【绘制】 */
	public enum PlayMode implements Serializable {
		/** 播放脚本 */
		PLAY,
		/** 绘制 */
		MAKE
	}

	//
	void resetAllViews();

	void onPlayComplete();

	void onPlayStart();

	//
	void preChangePage(final Runnable successRunnable);

	//
	int makePageWidth();

	int makePageHeight();

	//
	void showViewFlipperOverlay();

	void hideViewFlipperOverlay();

	//
	void showEditClickPopup(EditWidget edit);

	void showImageClickPopup(ImageWidget imageWidget);

	//
	void addGlobalMusic(MusicBean music);
}
