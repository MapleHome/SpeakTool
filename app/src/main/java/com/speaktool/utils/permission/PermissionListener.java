package com.speaktool.utils.permission;

/**
 * @author maple
 * @time 2018/4/2.
 */
public interface PermissionListener {

    void onPermissionGranted();

    void onPermissionDenied(String[] deniedPermissions);

    void onPermissionDeniedDotAgain(String[] deniedPermissions);
}
