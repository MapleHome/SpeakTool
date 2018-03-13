package com.speaktool.ui.base;

import java.util.List;

import android.content.Context;
import android.widget.BaseAdapter;

import com.google.common.collect.Lists;

public abstract class AbsAdapter<T> extends BaseAdapter {
	protected Context mContext;
	protected List<T> mDatas;

	public AbsAdapter(Context ctx, List<T> datas) {
		mContext = ctx.getApplicationContext();
		if (datas == null)
			mDatas = Lists.newArrayList();
		else
			mDatas = datas;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public final void add(T t) {
		if (t == null)
			return;
		mDatas.add(t);
		this.notifyDataSetChanged();
	}

	public final void remove(int index) {
		if (index < 0 || index >= mDatas.size())
			return;
		mDatas.remove(index);
		this.notifyDataSetChanged();
	}

	public final void remove(T data) {
		if (data == null)
			return;
		mDatas.remove(data);
		this.notifyDataSetChanged();
	}

	public final void clear() {
		mDatas.clear();
		this.notifyDataSetChanged();
	}

	public final void refresh(List<T> datas) {
		if (datas == null)
			mDatas = Lists.newArrayList();
		else
			mDatas = datas;
		this.notifyDataSetChanged();
	}

	// 测试此列表是否包含指定的对象。
	public final boolean contains(T t) {
		return mDatas.contains(t);
	}
}
