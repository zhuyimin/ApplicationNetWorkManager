package settings.hometech.com.myapplication;

import android.graphics.Bitmap;

/**
 * Created by zhuyimin on 2017/5/24.
 */

public class ApplicationInfo {
    public int _id;
    public int uid;
    public String gprs;
    public String wifi;
    public byte[] icon;
    public String backData;
    public String roamData;
    public String packageName;
    public String isSystemApp;
    public String appName;
    public ApplicationInfo() {
    }

    public ApplicationInfo(int uid, String gprs, String wifi,byte[] icon) {
        this.uid = uid;
        this.gprs = gprs;
        this.wifi = wifi;
        this.icon = icon;
    }

    public ApplicationInfo(int uid, String packageName) {
        this.uid = uid;
        this.packageName = packageName;
    }

    public ApplicationInfo(int uid, String gprs, String wifi,byte[] icon,String backData,String roamData,String packageName,String isSystemApp,String appName) {
        this.uid = uid;
        this.gprs = gprs;
        this.wifi = wifi;
        this.icon = icon;
        this.backData = backData;
        this.roamData = roamData;
        this.packageName = packageName;
        this.isSystemApp = isSystemApp;
        this.appName = appName;
    }
}
