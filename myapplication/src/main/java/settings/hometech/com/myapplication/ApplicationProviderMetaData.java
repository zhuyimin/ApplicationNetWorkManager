package settings.hometech.com.myapplication;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by zhuyimin on 2017/5/31.
 */

public class ApplicationProviderMetaData {

    private final static String AUTHORITY = "settings.hometech.com.myapplication";

    public static final String DATABASE_NAME="appNetInfo.db";
    public static final int DATABASE_VERSION=1;
    public static final String TABLE_NAME="application";
    public static final String COLUMN_APPLICATION_UID="uid";
    public static final String COLUMN_APPLICATION_GPRS="gprs";
    public static final String COLUMN_APPLICATION_WIFI="wifi";
    public static final String COLUMN_APPLICATION_ICON="icon";
    public static final String COLUMN_APPLICATION_BACKDATA="backData";
    public static final String COLUMN_APPLICATION_ROAMDATA="roamData";
    public static final String COLUMN_APPLICATION_PACKAGENAME="packageName";
    public static final String COLUMN_APPLICATION_ISSYSTEMAPP="isSystemApp";
    public static final String COLUMN_APPLICATION_APPNAME="appName";
    public static final String COLUMN_ID = "_id";
    public static final Uri CONTENT_URI=Uri.parse("content://"+AUTHORITY+"/application");
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/application";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/application";


}