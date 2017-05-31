package settings.hometech.com.myapplication;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class NetworkManagerActivity extends Activity {

    private DBManager mgr;
    private static final String SHAREDPREFERENCES_NAME = "my_pref";
    private static final String KEY_GUIDE_ACTIVITY = "guide_activity";
    private boolean mFirst;
    private ProgressDialog progressDialog;
    private static final int SAVE_DATABASE = 1;
    private static final int SAVE_DATABASE_SUCCESS = 2;
    private SaveDatabaseTask saveDatabaseTask = null;
    private LinearLayout textView1 = null;
    private LinearLayout textView2 = null;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private ActionBar myactionbar;
    private TextView myactionbar_title;
    private ImageButton myactionbar_back_btn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netmork_manager);
        initCustomActionBar();
        mFirst = isFirstEnter(this);
        //初始化DBManager
        mgr = new DBManager(this);
        fragmentManager = getFragmentManager();
        textView1 = (LinearLayout) findViewById(R.id.normal_app);
        textView2 = (LinearLayout) findViewById(R.id.system_app);
        fragment = new FragmentFactory().getInstanceByIndex(1);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.commit();
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView1.setBackgroundResource(R.drawable.tab_bg_click);
                textView2.setBackgroundResource(R.drawable.tab_bg_not_click);
                fragment = new FragmentFactory().getInstanceByIndex(1);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content, fragment);
                transaction.commit();
            }
        });
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView1.setBackgroundResource(R.drawable.tab_bg_not_click);
                textView2.setBackgroundResource(R.drawable.tab_bg_click);
                fragment = new FragmentFactory().getInstanceByIndex(2);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content, fragment);
                transaction.commit();
            }
        });
        if (mFirst) {
            SharedPreferences sharedPreferences = this.getSharedPreferences("my_pref", MODE_PRIVATE);
            sharedPreferences.edit().putString(KEY_GUIDE_ACTIVITY, "false").commit();
            //saveAppInfos();
            progressDialog = ProgressDialog.show(this, getResources().getString(R.string.initialize_data), getResources().getString(R.string.loading_data));
            Message msg = Message.obtain();
            msg.what = SAVE_DATABASE;
            mHandler.sendMessageDelayed(msg,2000);
            Log.i("yimin", "first in---add database data");
        } else {
            Log.i("yimin", "not first in");
        }
        //getTestResover();
    }

    private boolean isFirstEnter(Context context) {
        String mResultStr = context.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE)
                .getString(KEY_GUIDE_ACTIVITY, "true");//取得所有类名 如 com.my.MainActivity
        if (mResultStr.equalsIgnoreCase("true"))
            return true;
        else
            return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //应用的最后一个Activity关闭时应释放DB
        mgr.closeDB();
    }

    public void add(View view) {
        ArrayList<ApplicationInfo> persons = new ArrayList<ApplicationInfo>();
        ApplicationInfo person1 = new ApplicationInfo(10000, "true", "true", null, "false", "false", null, "true",null);
        persons.add(person1);
        mgr.addApplication(persons);
    }

    public void update(View view) {
        ApplicationInfo person = new ApplicationInfo();
        person.gprs = "true";
        person.wifi = "false";
        mgr.updateApplicationInfo(person);
    }

    public void delete(View view) {
        ApplicationInfo person = new ApplicationInfo();
        person.uid = 10000;
        mgr.deleteApplication(person);
    }

    public void query(View view) {
        List<ApplicationInfo> persons = mgr.queryApplication();
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (ApplicationInfo person : persons) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("gprs", person.gprs);
            map.put("wifi", person.wifi);
            list.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2,
                new String[]{"gprs", "wifi"}, new int[]{android.R.id.text1, android.R.id.text2});
    }

    /*
    public void queryTheCursor(View view) {
        Cursor c = mgr.queryTheCursor();
        startManagingCursor(c); //托付给activity根据自己的生命周期去管理Cursor的生命周期
        CursorWrapper cursorWrapper = new CursorWrapper(c) {
            @Override
            public String getString(int columnIndex) {
                //将简介前加上年龄
                if (getColumnName(columnIndex).equals("wifi")) {
                    int age = getInt(getColumnIndex("age"));
                    return age + " years old, " + super.getString(columnIndex);
                }
                return super.getString(columnIndex);
            }
        };
        //确保查询结果中有"_id"列
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2,
                cursorWrapper, new String[]{"name", "info"}, new int[]{android.R.id.text1, android.R.id.text2});
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }*/

    public static List<PackageInfo> getAllApps(Context context) {
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = context.getPackageManager();
        // 获取手机内所有应用
        List<PackageInfo> packlist = pManager.getInstalledPackages(0);
        for (int i = 0; i < packlist.size(); i++) {
            PackageInfo pak = (PackageInfo) packlist.get(i);
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                // 添加自己已经安装的应用程序
                apps.add(pak);
            }

        }
        return apps;
    }
/*
    public List<ApplicationInfo> getAllApplications(Context context) {

        List<ApplicationInfo> applicationInfoList = null;
        ApplicationInfo application = new ApplicationInfo();
        applicationInfoList.add(application);
        return applicationInfoList;
        PackageManager packageManager = this.getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
        //判断是否系统应用：
        List<ApplicationInfo> applicationInfos = new ArrayList<ApplicationInfo>();
        for (int i = 0; i < packageInfoList.size(); i++) {
            PackageInfo pak = (PackageInfo) packageInfoList.get(i);
            //判断是否为系统预装的应用
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                // 第三方应用
                int uid;
                String gprs = "false";
                String wifi = "false";
                byte[] icon = BitmapToBytes(drawable2Bitmap(pak.applicationInfo.loadIcon(packageManager)));
                String backData = "false";
                String roamData = "false";
                String packageName = pak.packageName;
                String isSystemApp = "false";
                String appName = pak.applicationInfo.label;
            } else {
                //系统应用
            }
        }
    }*/

    public void saveAppInfos(){
        PackageManager pm = getApplication().getPackageManager();
        List<PackageInfo>  packgeInfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        ArrayList<ApplicationInfo> appInfos = new ArrayList<ApplicationInfo>();
        /* 获取应用程序的名称，不是包名，而是清单文件中的labelname
            String str_name = packageInfo.applicationInfo.loadLabel(pm).toString();
            appInfo.setAppName(str_name);
         */
        for(PackageInfo packgeInfo : packgeInfos){
            //String appName = packgeInfo.applicationInfo.loadLabel(pm).toString();
            //String packageName = packgeInfo.packageName;
            //Drawable drawable = packgeInfo.applicationInfo.loadIcon(pm);
            int uid = packgeInfo.applicationInfo.uid;
            String gprs = "false";
            String wifi = "false";
            byte[] icon = BitmapToBytes(drawableToBitmap(packgeInfo.applicationInfo.loadIcon(pm)));
            String backData = "false";
            String roamData = "false";
            String packageName = packgeInfo.packageName;
            String isSystemApp = "false";
            if(uid<10000) {
                isSystemApp = "true";
            } else {
                isSystemApp = "false";
            }
            String appName = packgeInfo.applicationInfo.loadLabel(pm).toString();;
            ApplicationInfo appInfo = new ApplicationInfo(uid,gprs, wifi,icon,backData,roamData,packageName,isSystemApp,appName);
            appInfos.add(appInfo);
        }
        mgr.addApplication(appInfos);
        mHandler.sendEmptyMessage(SAVE_DATABASE_SUCCESS);
    }

    byte[] BitmapToBytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {//覆盖handleMessage方法
            switch (msg.what) {//根据收到的消息的what类型处理
                case SAVE_DATABASE:
                    //saveAppInfos();
                    saveDatabaseTask = new SaveDatabaseTask();
                    saveDatabaseTask.execute();
                    break;
                case SAVE_DATABASE_SUCCESS:
                    progressDialog.dismiss();
                    fragment = new FragmentFactory().getInstanceByIndex(1);
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.content, fragment);
                    transaction.commit();
                    break;
                default:
                    super.handleMessage(msg);//这里最好对不需要或者不关心的消息抛给父类，避免丢失消息
                    break;
            }
        }
    };

    public class SaveDatabaseTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // We do the actual work of authenticating the user
            // in the NetworkUtilities class.
            saveAppInfos();
            return "";
        }

        @Override
        protected void onPostExecute(final String authToken) {
            // On a successful authentication, call back into the Activity to
            // communicate the authToken (or null for an error).
            // onAuthenticationResult(authToken);
        }

        @Override
        protected void onCancelled() {
            // If the action was canceled (by the user clicking the cancel
            // button in the progress dialog), then call back into the
            // activity to let it know.
            // onAuthenticationCancel();
        }
    }

    public final class ViewHolder {
        public ImageView icon;
        public CheckBox gprsBtn;
        public CheckBox wifiBtn;
        public TextView applicationName;
    }

    private boolean initCustomActionBar() {
        myactionbar = getActionBar();
        if (myactionbar == null) {
            return false;
        } else {
            myactionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            myactionbar.setDisplayShowCustomEnabled(true);
            myactionbar.setCustomView(R.layout.my_actionbar_style);
            myactionbar_title = (TextView) myactionbar.getCustomView().findViewById(R.id.my_actionbar_title);
            myactionbar_back_btn = (ImageButton) myactionbar.getCustomView().findViewById(R.id.ps_back_btn);
            myactionbar_back_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //((NetworkManagerOneFragment) fragment).applyOrSaveRules();
                    finish();
                }
            });
            myactionbar_title.setText(getResources().getString(R.string.app_name));
            return true;
        }
    }

    public void getTestResover() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ApplicationProviderMetaData.CONTENT_URI,new String[]{ApplicationProviderMetaData.COLUMN_ID,ApplicationProviderMetaData.COLUMN_APPLICATION_UID},null,null,null);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(cursor.getColumnIndex(ApplicationProviderMetaData.COLUMN_APPLICATION_UID));
            Log.i("yimin","the first id is:" + s);
        }
    }
}
