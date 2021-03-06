package com.tgcity.utils;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * @author TGCity
 * @date 2019/12/31
 * @description 系统工具类
 */
public class SystemUtils {

    /**
     * 判断App是否是debug环境
     *
     * @param context Context
     * @return boolean
     */
    public static boolean isApkDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * 判断App是否处于后台运行
     *
     * @param context Context
     * @return boolean
     */
    public static boolean isAppInBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return true;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                return appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND;
            }
        }
        return false;
    }

    /**
     * 获取应用名称
     *
     * @param context Context
     * @return String
     */
    public static String getAppName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo;
        if (!(context instanceof Application)) {
            context = context.getApplicationContext();
        }
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        if (applicationInfo != null) {
            CharSequence charSequence = packageManager.getApplicationLabel(applicationInfo);
            return (String) charSequence;
        } else {
            return "温馨提示";
        }
    }

    /**
     * 是否是主线程
     *
     * @param context Context
     * @return boolean
     */
    public static boolean isMainProcess(Context context) {
        try {
            ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
            List<ActivityManager.RunningAppProcessInfo> processInfo;
            if (am != null) {
                processInfo = am.getRunningAppProcesses();
                String mainProcessName = context.getPackageName();
                int myPid = android.os.Process.myPid();
                for (ActivityManager.RunningAppProcessInfo info : processInfo) {
                    if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
