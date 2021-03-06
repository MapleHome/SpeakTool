package com.speaktool.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.speaktool.R;
import com.speaktool.base.BaseFragment;
import com.speaktool.tasks.TaskLoadRecords;
import com.speaktool.tasks.TaskLoadRecords.RecordsUi;
import com.speaktool.tasks.ThreadPoolWrapper;
import com.speaktool.ui.adapters.RecordsAdapter;
import com.speaktool.ui.draw.RecordBean;
import com.speaktool.view.dialogs.CourseItemDesDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 所有记录界面
 *
 * @author shaoshuai
 */
public class HomePage extends BaseFragment {
    @BindView(R.id.srl_refreshLayout) SmartRefreshLayout srl_refreshLayout;
    @BindView(R.id.gv_records) GridView gv_records;// 所有记录

    private RecordsAdapter recordsAdapter;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_home;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        // 记录列表
        recordsAdapter = new RecordsAdapter(mContext, null);
        gv_records.setAdapter(recordsAdapter);
        gv_records.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecordBean course = (RecordBean) parent.getAdapter().getItem(position);
                new CourseItemDesDialog(getActivity(), course).show();
            }
        });

        srl_refreshLayout
                .setRefreshHeader(new ClassicsHeader(mContext))
                .setOnRefreshListener(new OnRefreshListener() {
                    @Override
                    public void onRefresh(RefreshLayout refreshlayout) {
                        refreshIndexPage();
                    }
                })
                .setEnableLoadMore(false);

        // 默认设置
        refreshIndexPage();
    }

    /**
     * 刷新数据
     */
    public void refreshIndexPage() {
        ThreadPoolWrapper.newThreadPool(1)
                .execute(new TaskLoadRecords(new RecordsUi() {
                    @Override
                    public void onRecordsLoaded(List<RecordBean> datas) {
                        if (datas == null || datas.isEmpty()) {
                            // tvSearchEmpty.setText("未找到录像");
                            // tvSearchEmpty.setVisibility(View.VISIBLE);
                            // tvSearchEmpty.setProgressbarVisibility(View.GONE);
                        }
                        recordsAdapter.refresh(datas);
                        srl_refreshLayout.finishRefresh();
                    }
                }));
    }


}
