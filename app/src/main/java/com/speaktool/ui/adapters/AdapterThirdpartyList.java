package com.speaktool.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

import com.speaktool.R;
import com.speaktool.bean.ThirdParty;
import com.speaktool.ui.base.AbsAdapter;
import com.speaktool.ui.layouts.ItemViewThirdparty;
import com.speaktool.utils.BitmapScaleUtil;

import java.util.List;

/**
 * 第三方平台列表
 *
 * @author shaoshuai
 */
public class AdapterThirdpartyList extends AbsAdapter<ThirdParty> {
    private Bitmap defbmp;

    public AdapterThirdpartyList(Context ctx, List<ThirdParty> datas) {
        super(ctx, datas);
        defbmp = BitmapScaleUtil.decodeSampledBitmapFromResource(ctx.getResources(), R.drawable.ic_launcher,
                1024 * 1024);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = new ItemViewThirdparty(mContext);
        }
        ThirdParty bean = (ThirdParty) getItem(position);
        if (bean == null) {
            return convertView;
        }
        ItemViewThirdparty item = (ItemViewThirdparty) convertView;
        item.setLogo(defbmp);
        item.setName(bean.getName());
        // set tag.
        if (bean.getIconType() == ThirdParty.ICON_TYPE_RES) {
            item.setTag(bean.getIconResId());
        } else if (bean.getIconType() == ThirdParty.ICON_TYPE_NET) {
            item.setTag(bean.getIconUrl());
        }

        return item;
    }

    public Bitmap getDefBmp() {
        return defbmp;
    }
}
