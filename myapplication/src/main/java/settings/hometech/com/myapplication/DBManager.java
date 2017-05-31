package settings.hometech.com.myapplication;

/**
 * Created by zhuyimin on 2017/5/24.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * add persons
     * @param applicationInfos
     */
    public void addApplication(List<ApplicationInfo> applicationInfos) {
        db.beginTransaction();  //开始事务
        try {
            for (ApplicationInfo application : applicationInfos) {
                db.execSQL("INSERT INTO application VALUES(null, ?, ?, ?,?,?,?,?,?,?)", new Object[]{application.uid, application.gprs, application.wifi,application.icon,application.backData,application.roamData,application.packageName,application.isSystemApp,application.appName});
            }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    /**
     * add persons
     * @param application
     */
    public void addApplication(ApplicationInfo application) {
        db.beginTransaction();  //开始事务
        try {
            db.execSQL("INSERT INTO application VALUES(null, ?, ?, ?,?,?,?,?,?,?)", new Object[]{application.uid, application.gprs, application.wifi,application.icon,application.backData,application.roamData,application.packageName,application.isSystemApp,application.appName});
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    /**
     * update person's age
     * @param applicationInfo
     */
    public void updateApplicationInfo(ApplicationInfo applicationInfo) {
        ContentValues cv = new ContentValues();
        cv.put("gprs", applicationInfo.gprs);
        cv.put("wifi", applicationInfo.wifi);
        cv.put("backData", applicationInfo.backData);
        cv.put("roamData", applicationInfo.roamData);
        cv.put("isSystemApp", applicationInfo.isSystemApp);
        cv.put("appName", applicationInfo.appName);
        Log.i("yimin","update database,gprs:" + applicationInfo.gprs + "wifi:" + applicationInfo.wifi + "uid:" + applicationInfo.uid + "packagename:" + applicationInfo.packageName);
        db.update("application", cv, "uid = ? AND packageName = ?", new String[]{String.valueOf(applicationInfo.uid),applicationInfo.packageName});
    }

    /**
     * delete application
     * @param applicationInfo
     */
    public void deleteApplication(ApplicationInfo applicationInfo) {
        db.delete("application", "uid = ? AND packageName = ?", new String[]{String.valueOf(applicationInfo.uid),String.valueOf(applicationInfo.packageName)});
    }

    /**
     * delete application
     * @param uid
     */
    public void deleteApplication(int uid,String packageName) {
        db.delete("application", "uid = ?", new String[]{String.valueOf(uid),String.valueOf(packageName)});
    }

    /**
     * query all persons, return list
     * @return List<Person>
     */
    public List<ApplicationInfo> queryApplication() {
        ArrayList<ApplicationInfo> applicationInfos = new ArrayList<ApplicationInfo>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            ApplicationInfo application = new ApplicationInfo();
            application._id = c.getInt(c.getColumnIndex("_id"));
            application.uid = c.getInt(c.getColumnIndex("uid"));
            application.gprs = c.getString(c.getColumnIndex("gprs"));
            application.wifi = c.getString(c.getColumnIndex("wifi"));
            application.icon = c.getBlob(c.getColumnIndex("icon"));
            application.backData = c.getString(c.getColumnIndex("backData"));
            application.roamData = c.getString(c.getColumnIndex("roamData"));
            application.packageName = c.getString(c.getColumnIndex("packageName"));
            application.isSystemApp = c.getString(c.getColumnIndex("isSystemApp"));
            application.appName = c.getString(c.getColumnIndex("appName"));
            applicationInfos.add(application);
        }
        c.close();
        return applicationInfos;
    }

    public List<ApplicationInfo> queryNormalApplication() {
        ArrayList<ApplicationInfo> applicationInfos = new ArrayList<ApplicationInfo>();
        //Cursor c = queryTheCursor();
        Cursor c = queryNormalCursor();
        while (c.moveToNext()) {
            ApplicationInfo application = new ApplicationInfo();
            application._id = c.getInt(c.getColumnIndex("_id"));
            application.uid = c.getInt(c.getColumnIndex("uid"));
            application.gprs = c.getString(c.getColumnIndex("gprs"));
            application.wifi = c.getString(c.getColumnIndex("wifi"));
            application.icon = c.getBlob(c.getColumnIndex("icon"));
            application.backData = c.getString(c.getColumnIndex("backData"));
            application.roamData = c.getString(c.getColumnIndex("roamData"));
            application.packageName = c.getString(c.getColumnIndex("packageName"));
            application.isSystemApp = c.getString(c.getColumnIndex("isSystemApp"));
            application.appName = c.getString(c.getColumnIndex("appName"));
            if(application.appName.length()<20) {
                applicationInfos.add(application);
            }
        }
        c.close();
        return applicationInfos;
    }

    public List<ApplicationInfo> querySystemApplication() {
        ArrayList<ApplicationInfo> applicationInfos = new ArrayList<ApplicationInfo>();
        //Cursor c = queryTheCursor();
        Cursor c = querySystemCursor();
        while (c.moveToNext()) {
            ApplicationInfo application = new ApplicationInfo();
            application._id = c.getInt(c.getColumnIndex("_id"));
            application.uid = c.getInt(c.getColumnIndex("uid"));
            application.gprs = c.getString(c.getColumnIndex("gprs"));
            application.wifi = c.getString(c.getColumnIndex("wifi"));
            application.icon = c.getBlob(c.getColumnIndex("icon"));
            application.backData = c.getString(c.getColumnIndex("backData"));
            application.roamData = c.getString(c.getColumnIndex("roamData"));
            application.packageName = c.getString(c.getColumnIndex("packageName"));
            application.isSystemApp = c.getString(c.getColumnIndex("isSystemApp"));
            application.appName = c.getString(c.getColumnIndex("appName"));
            if(application.appName.length()<20) {
                applicationInfos.add(application);
            }
        }
        c.close();
        return applicationInfos;
    }

    /**
     * query all persons, return cursor
     * @return  Cursor
     */
    public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM application", null);
        return c;
    }

    /**
     * query all persons, return cursor
     * @return  Cursor
     */
    public Cursor queryNormalCursor() {
        Cursor c = db.rawQuery("select * FROM application where isSystemApp=?", new String[]{"false"});
        return c;
    }

    /**
     * query all persons, return cursor
     * @return  Cursor
     */
    public Cursor querySystemCursor() {
        Cursor c = db.rawQuery("select * FROM application where isSystemApp=?", new String[]{"true"});
        return c;
    }

    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }

}
