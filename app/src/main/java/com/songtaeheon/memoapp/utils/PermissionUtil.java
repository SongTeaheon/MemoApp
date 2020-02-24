package com.songtaeheon.memoapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtil {
    public static final int REQUEST_PERMISSION_READ = 0;//수 바뀌면 안됨
    public static final int REQUEST_PERMISSION_CAMERA = 1;//수 바뀌면 안됨

    public static boolean checkReadPermission(Activity activity){
        return checkPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PERMISSION_READ);
    }

    public static boolean checkCameraPermission(Activity activity){
        return checkPermission(activity, Manifest.permission.CAMERA, REQUEST_PERMISSION_CAMERA);
    }

    private static boolean checkPermission(Activity activity, String permission, int requestCode){
        if (android.os.Build.VERSION.SDK_INT > 23) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{permission},
                        requestCode);
                return false;
            }
        }
        return true;
    }
}
