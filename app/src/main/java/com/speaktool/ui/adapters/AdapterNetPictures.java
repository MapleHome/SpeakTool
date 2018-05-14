package com.speaktool.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;

import com.speaktool.bean.NetPictureBean;
import com.speaktool.ui.base.AbsAdapter;
import com.speaktool.ui.layouts.ItemViewNetPicture;

import java.util.List;

/**
 * 网络图片
 *
 * @author shaoshuai
 */
public class AdapterNetPictures extends AbsAdapter<NetPictureBean> {

    public AdapterNetPictures(Context ctx, List<NetPictureBean> datas) {
        super(ctx, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ItemViewNetPicture(mContext);

            int h = parent.getRootView().getWidth() / 5;
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, h);

            convertView.setLayoutParams(lp);
        }
        ItemViewNetPicture item = (ItemViewNetPicture) convertView;
        NetPictureBean bean = (NetPictureBean) getItem(position);
        if (bean == null) {
            item.setLoading();
            return item;
        }

        String imageUrl = bean.thumbUrl;
        item.setTag(imageUrl);
        item.setLoading();

        return item;
    }

}
