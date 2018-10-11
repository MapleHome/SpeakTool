package com.speaktool.ui.Setting;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maple.msdialog.ActionSheetDialog;
import com.maple.msdialog.AlertEditDialog;
import com.speaktool.Const;
import com.speaktool.R;
import com.speaktool.api.PhotoImporter;
import com.speaktool.api.PhotoImporter.PickPhotoCallback;
import com.speaktool.bean.UserBean;
import com.speaktool.tasks.TaskGetNetImage;
import com.speaktool.tasks.TaskGetNetImage.NetImageLoadListener;
import com.speaktool.tasks.TaskModifyUserInfo;
import com.speaktool.tasks.TaskModifyUserInfo.ModifyUserInfoCallback;
import com.speaktool.ui.base.BaseFragment;
import com.speaktool.utils.BitmapScaleUtil;
import com.speaktool.utils.T;
import com.speaktool.view.dialogs.LoadingDialog;
import com.speaktool.view.popupwindow.BasePopupWindow.WeiZhi;
import com.speaktool.view.popupwindow.L_M_AddSinglePhotosPoW;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 用户信息修改界面
 *
 * @author shaoshuai
 */
public class UserInfoChangePage extends BaseFragment implements OnClickListener {
    @BindView(R.id.rl_portrait_area) RelativeLayout rl_portrait_area;// 用户头像
    @BindView(R.id.rl_name_area) RelativeLayout rl_name_area;// 用户名称
    @BindView(R.id.rl_introduce_area) RelativeLayout rl_introduce_area;// 简介
    @BindView(R.id.rl_sex_area) RelativeLayout rl_sex_area;// 性别
    @BindView(R.id.rl_birthday_area) RelativeLayout rl_birthday_area;// 生日
    @BindView(R.id.rl_mail_area) RelativeLayout rl_mail_area;// 邮箱
    @BindView(R.id.rl_change_pwd_area) RelativeLayout rl_change_pwd_area;// 修改密码

    @BindView(R.id.iv_portrait) ImageView iv_portrait;// 头像
    @BindView(R.id.tv_name) TextView tv_name;// 用户名
    @BindView(R.id.tv_introduce) TextView tv_introduce;// 用户简介
    @BindView(R.id.tv_sex) TextView tv_sex;// 性别
    @BindView(R.id.tv_birthday) TextView tv_birthday;// 生日
    @BindView(R.id.tv_mail) TextView tv_mail;// 邮箱

    @BindView(R.id.bt_save_info) Button bt_save_info;// 保存信息

    private static final int REQUEST_CODE_IMAGE_CAPTURE = 1;// 捕获图片
    private static final int REQUEST_CODE_PICK_IMAGE = 2;// 挑选图片
    private static final String CAMERA_TEMP_IMAGE_PATH = Const.TEMP_DIR + "/camera_temp.jpg";
    private UserFMActivity mActivity;
    private LoadingDialog mLoadingDialog;
    private UserBean session;

    String niceName;
    String introduce;
    String email;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_user_info_change, null);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mActivity = (UserFMActivity) getActivity();
        mActivity.setTitle("个人信息修改");

        mLoadingDialog = new LoadingDialog(mActivity);
        session = null;
//        session = UserDatabase.getUserLocalSession(mContext);
        if (session != null) {
            setPortrait(session.getPortraitPath());// 设置头像
            tv_name.setText(session.getNickName());// 用户名
            tv_introduce.setText(session.getIntroduce());// 个性签名
            tv_mail.setText(session.getEmail()); // 邮箱
        }

    }

    @Override
    public void initListener() {
        rl_portrait_area.setOnClickListener(this);// 头像
        rl_name_area.setOnClickListener(this);// 用户名
        rl_introduce_area.setOnClickListener(this);// 个人简介

        rl_sex_area.setOnClickListener(this);// 性别
        rl_birthday_area.setOnClickListener(this);// 生日
        rl_mail_area.setOnClickListener(this);// 邮箱
        rl_change_pwd_area.setOnClickListener(this);// 修改密码

        bt_save_info.setOnClickListener(this);// 保存信息
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_portrait_area:// 用户头像
                changePortrait(v);
                break;
            case R.id.rl_name_area:// 用户名
                changeName();
                break;
            case R.id.rl_introduce_area:// 个人简介
                changeIntroduce();
                break;
            case R.id.rl_sex_area:// 性别
                changeSex();
                break;
            case R.id.rl_birthday_area:// 生日
                changeBirthday();
                break;
            case R.id.rl_mail_area:// 邮箱
                changeMail();
                break;
            case R.id.rl_change_pwd_area:// 修改密码
                // TODO
                T.showShort(mContext, "暂不支持！");
                break;
            case R.id.bt_save_info:// 保存信息
                saveModifyInfo();
                break;
            default:
                break;
        }
    }

    /**
     * 选择头像
     */
    private void changePortrait(View anchor) {
        new ActionSheetDialog(mActivity)
                // .setTitle("请选择性别")
                .setCancelable(false).setCanceledOnTouchOutside(false)
                .addSheetItem("拍照", new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        photoList.getImageFromCamera(iv_portrait, mPickPhotoCallback);
                    }
                })
                .addSheetItem("本地相册", new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        photoList.getImageFromAlbum(iv_portrait, mPickPhotoCallback);
                    }
                }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK_IMAGE) {// 本地挑选
                // nothing
            } else if (requestCode == REQUEST_CODE_IMAGE_CAPTURE) {// 拍照
                // 判断拍照文件是否存在
                if (new File(CAMERA_TEMP_IMAGE_PATH).exists()) {
                    // 获取到图片
                    mPickPhotoCallback.onPhotoPicked(CAMERA_TEMP_IMAGE_PATH);
                } else {
                    T.showShort(mContext, "照片不存在!");
                }
            }
        } else {// not ok.
            T.showShort(mContext, "获取图片失败!");
        }
    }

    private PickPhotoCallback mPickPhotoCallback = new PickPhotoCallback() {

        @Override
        public void onPhotoPicked(List<String> multiPickImgPaths) {
        }

        @Override
        public void onPhotoPicked(String imgPath) {
            setPortrait(imgPath);
        }
    };

    private PhotoImporter photoList = new PhotoImporter() {
        @Override
        public void getImageFromCamera(View anchor, PickPhotoCallback callback) {
            String state = Environment.getExternalStorageState();
            if (state.equals(Environment.MEDIA_MOUNTED)) {
                Intent intentCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(CAMERA_TEMP_IMAGE_PATH)));
                intentCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                startActivityForResult(intentCamera, REQUEST_CODE_IMAGE_CAPTURE);
            } else {
                T.showShort(mContext, "SD卡不存在！");
            }
        }

        @Override
        public void getImageFromAlbum(View anchor, PickPhotoCallback callback) {
            L_M_AddSinglePhotosPoW popupWindow = new L_M_AddSinglePhotosPoW(mContext, anchor, callback);
            popupWindow.showPopupWindow(WeiZhi.Bottom);
        }

        @Override
        public void getImageFromNet(View anchor, PickPhotoCallback callback) {
            // ignore.
        }

        @Override
        public void importImageBatch(View anchor, PickPhotoCallback callback) {
            // ignore.
        }
    };

    /**
     * 修改昵称
     */
    private void changeName() {
        new AlertEditDialog(mActivity)
                .setTitle("请输入昵称")
                .setMessage("起一个好听的名字，让更多人认识你！")
                .setEditCallListener(new AlertEditDialog.EditTextCallListener() {
                    @Override
                    public void callBack(String s) {
                        niceName = s;
                    }
                })
                .setRightButton("确定", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(niceName)) {
                            tv_name.setText(niceName);
                        }
                    }
                })
                .show();
    }


    /**
     * 修改个人签名
     */
    private void changeIntroduce() {
        new AlertEditDialog(mActivity)
                .setTitle("请输入个性签名")
                .setEditCallListener(new AlertEditDialog.EditTextCallListener() {
                    @Override
                    public void callBack(String s) {
                        introduce = s;
                    }
                })
                .setRightButton("确定", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(introduce)) {
                            tv_introduce.setText(introduce);
                        }
                    }
                })
                .show();
    }

    /**
     * 修改性别
     */
    private void changeSex() {
        new ActionSheetDialog(mActivity)
                .setTitle("请选择性别")
                .setCancelable(false).setCanceledOnTouchOutside(false)
                .addSheetItem("男", new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        tv_sex.setText("男");
                    }
                })
                .addSheetItem("女", new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        tv_sex.setText("女");
                    }
                })
                .addSheetItem("保密", new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        tv_sex.setText("未知");
                    }
                }).show();
    }

    /**
     * 修改生日
     */
    private void changeBirthday() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dateDialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                // 更新EditText控件日期 小于10加0
                String date = new StringBuilder().append(year).append("-")
                        .append((month + 1) < 10 ? "0" + (month + 1) : (month + 1)).append("-")
                        .append((day < 10) ? "0" + day : day).toString();
                tv_birthday.setText(date);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dateDialog.setTitle("选择日期");
        dateDialog.show();
    }

    /**
     * 修改邮箱
     */
    private void changeMail() {
        new AlertEditDialog(mActivity)
                .setTitle("绑定新邮箱")
                .setEditCallListener(new AlertEditDialog.EditTextCallListener() {
                    @Override
                    public void callBack(String s) {
                        email = s;
                    }
                })
                .setRightButton("确定", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(email)) {
                            tv_mail.setText(email);
                        }
                    }
                })
                .show();
    }

    /**
     * 保存修改信息
     */
    private void saveModifyInfo() {
        mLoadingDialog.show("正在保存...");
        // TODO 判空
        session.setPortraitPath(portraitPath);// 头像地址
        session.setNickName(tv_name.getText().toString().trim());// 昵称
        session.setIntroduce(tv_introduce.getText().toString().trim());// 自我介绍
        // 性别
        // 生日
        session.setEmail(tv_mail.getText().toString().trim());// 邮箱
        // session.setPassword("");// 密码

        new Thread(new TaskModifyUserInfo(new ModifyUserInfoCallback() {
            @Override
            public void onSuccess() {
                mLoadingDialog.dismiss();
                T.showShort(mContext, "修改成功");
            }

            @Override
            public void onResponseFail() {
                mLoadingDialog.dismiss();
                T.showShort(mContext, "修改失败");
            }

            @Override
            public void onConnectFail() {
                mLoadingDialog.dismiss();
                T.showShort(mContext, "服务器链接失败！请检查网络");
            }
        }, session)).start();
    }

    private String portraitPath;// 头像路径

    private static LruCache<String, Bitmap> portraitCache = new LruCache<String, Bitmap>(1024 * 500) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getHeight() * value.getRowBytes();
        }
    };

    /**
     * 设置头像路径
     */
    public void setPortrait(final String imagepath) {
        if (TextUtils.isEmpty(imagepath))
            return;
        portraitPath = imagepath;
        Bitmap cache = portraitCache.get(imagepath);
        if (cache != null) {
            iv_portrait.setImageBitmap(cache);
            return;
        }
        if (portraitPath.startsWith("http://")) {
            new Thread(new TaskGetNetImage(new NetImageLoadListener() {
                @Override
                public void onNetImageLoaded(Bitmap result) {
                    if (result != null) {
                        iv_portrait.setImageBitmap(result);
                        portraitCache.put(imagepath, result);
                    }
                }
            }, portraitPath)).start();

        } else {
            Bitmap bp = BitmapScaleUtil.decodeSampledBitmapFromPath(portraitPath, Const.MAX_MEMORY_BMP_CAN_ALLOCATE);
            iv_portrait.setImageBitmap(bp);
            portraitCache.put(imagepath, bp);
        }
    }

}