package settings.hometech.com.myapplication;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by yimin on 2017-05-13.
 */
public class NetworkManagerTwoFragment extends Fragment  {
	private List<ApplicationInfo> applicationInfoList = null;
	private View view = null;
	private DBManager mgr;
	private ListView listView;
	private Context mContext = null;
	private ApplicationAdapter applicationAdapter;
	private CheckBox gprs_all = null;
	private CheckBox wifi_all = null;
	private String isGprsSwitchOn = "true";
	private static final String SHAREDPREFERENCES_NAME = "my_pref";
	private static final String GPRS_ALL_SWITCH = "gprs_switch_two";
	private static final int SAVE_ALL_INFO = 1;
	private static final int SAVE_ALL_INFO_SUCCESS = 2;
	private static int WIFI_TYPE = 1;
	private static int GPRS_TYPE = 0;
	private static final HandlerThread sWorkerThread = new HandlerThread("NetworkManager-loader");
	private ProgressDialog progressDialog;
	static {
		sWorkerThread.start();
	}
	private static final Handler sWorker = new Handler(sWorkerThread.getLooper());
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.network_manger_fragment_two, null);
		Log.i("yimin","one fragment create");
		mgr = new DBManager(mContext);
		listView = (ListView) view.findViewById(R.id.application_list);
		gprs_all = (CheckBox) view.findViewById(R.id.gprs_all);
		wifi_all = (CheckBox) view.findViewById(R.id.wifi_all);
		isGprsSwitchOn = mContext.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE)
				.getString(GPRS_ALL_SWITCH, "true");
		if("true".equals(isGprsSwitchOn)) {
			gprs_all.setChecked(true);
		} else {
			gprs_all.setChecked(false);
		}
		gprs_all.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				progressDialog = ProgressDialog.show(mContext, getResources().getString(R.string.save_data), getResources().getString(R.string.please_waite));
				if("true".equals(isGprsSwitchOn)) {
					//progressDialog = ProgressDialog.show(mContext, "数据保存中", "请稍等", true, false);
					sWorker.post(new Runnable() {
						@Override
						public void run() {
							// ActivityUtils.persistAccountToShare(RegisterActivity.this,mAccountName,newPwd);
							// //保存修改后的密码到服务器，暂时本地存储,这部分需要在线程里面处理
							isGprsSwitchOn = "false";
							SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHAREDPREFERENCES_NAME, mContext.MODE_PRIVATE);
							sharedPreferences.edit().putString(GPRS_ALL_SWITCH, "false").commit();
							saveAllAppInfos("gprs","true");
							mHandler.post(new Runnable() { // ui线程操作
								@Override
								public void run() {
								}
							});
						}
					});
				} else {
					//progressDialog = ProgressDialog.show(mContext, "数据保存中", "请稍等", true, false);
					sWorker.post(new Runnable() {
						@Override
						public void run() {
							// ActivityUtils.persistAccountToShare(RegisterActivity.this,mAccountName,newPwd);
							// //保存修改后的密码到服务器，暂时本地存储,这部分需要在线程里面处理
							isGprsSwitchOn = "true";
							SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHAREDPREFERENCES_NAME, mContext.MODE_PRIVATE);
							sharedPreferences.edit().putString(GPRS_ALL_SWITCH, "true").commit();
							saveAllAppInfos("gprs","false");
							mHandler.post(new Runnable() { // ui线程操作
								@Override
								public void run() {
								}
							});
						}
					});
				}
			}
		});
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		mContext = activity;
		super.onAttach(activity);
	}

	@Override
	public void onResume() {
		//applicationInfoList = mgr.queryApplication();
		applicationInfoList = mgr.querySystemApplication();
		applicationAdapter = new ApplicationAdapter(mContext);
		listView.setAdapter(applicationAdapter);
		super.onResume();
	}

	public class ApplicationAdapter extends BaseAdapter {

		private LayoutInflater inflater=null;
		public ApplicationAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}
		public int getCount() {
			return applicationInfoList.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			//applicationInfo = applicationInfoList.get(position);
			if(convertView==null) {
				convertView = inflater.inflate(R.layout.application_list_item_two, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView.findViewById(R.id.application_icon);
				holder.gprsBtn = (CheckBox) convertView.findViewById(R.id.gprs_btn);
				holder.applicationName = (TextView) convertView.findViewById(R.id.application_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.icon.setImageBitmap(Bytes2Bimap(applicationInfoList.get(position).icon));
			if("true".equals(applicationInfoList.get(position).gprs)) {
				holder.gprsBtn.setChecked(false);
			} else {
				holder.gprsBtn.setChecked(true);
			}
			//holder.applicationName.setText(applicationInfoList.get(position).appName);
			holder.applicationName.setText(getProgramNameByPackageName(mContext,applicationInfoList.get(position).packageName));
			holder.gprsBtn.setTag(position);
			Log.i("yimin","holder setTag:" + holder.gprsBtn.getTag());
			holder.gprsBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int pos = (int)v.getTag();
					Log.i("yimin","holder getTag:" + pos);
					if("false".equals(applicationInfoList.get(pos).gprs)) {
						applicationInfoList.get(pos).gprs = "true";
						setFirewallUidChainRule(applicationInfoList.get(pos).uid,GPRS_TYPE,true);
					} else {
						applicationInfoList.get(pos).gprs = "false";
						setFirewallUidChainRule(applicationInfoList.get(pos).uid,GPRS_TYPE,false);
					}
					int num = 0;
					for(int i = 0;i < applicationInfoList.size();i++) {
						if("false".equals(applicationInfoList.get(i).gprs)) {
							num++;
						}
					}
					Log.i("yimin","num size:" + num + " list size:" + applicationInfoList.size());
					if(num == applicationInfoList.size()) {
						gprs_all.setChecked(true);
						SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHAREDPREFERENCES_NAME, mContext.MODE_PRIVATE);
						sharedPreferences.edit().putString(GPRS_ALL_SWITCH, "true").commit();
					} else {
						gprs_all.setChecked(false);
						SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHAREDPREFERENCES_NAME, mContext.MODE_PRIVATE);
						sharedPreferences.edit().putString(GPRS_ALL_SWITCH, "false").commit();
					}
					mgr.updateApplicationInfo( applicationInfoList.get(pos));
					applicationAdapter.notifyDataSetChanged();
				}
			});
			return convertView;
		}
	}

	public final class ViewHolder {
		public ImageView icon;
		public CheckBox gprsBtn;
		public TextView applicationName;
	}

	Bitmap Bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	public static String getProgramNameByPackageName(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		String name = null;
		try {
			name = pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)).toString();
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return name;
	}

	public void saveAllAppInfos(String type,String btn) {
		if("gprs".equals(type) && "true".equals(btn)) {
			for(int i = 0;i<applicationInfoList.size();i++) {
				ApplicationInfo applicationInfo = applicationInfoList.get(i);
				applicationInfo.gprs = "true";
				mgr.updateApplicationInfo(applicationInfo);
				setFirewallUidChainRule(applicationInfoList.get(i).uid,GPRS_TYPE,true);
			}
			//applicationAdapter.notifyDataSetChanged();
		} else if("gprs".equals(type) && "false".equals(btn)) {
			for(int i = 0;i<applicationInfoList.size();i++) {
				ApplicationInfo applicationInfo = applicationInfoList.get(i);
				applicationInfo.gprs = "false";
				mgr.updateApplicationInfo(applicationInfo);
				setFirewallUidChainRule(applicationInfoList.get(i).uid,GPRS_TYPE,false);
			}
		} else if("wifi".equals(type) && "true".equals(btn)) {
			for(int i = 0;i<applicationInfoList.size();i++) {
				ApplicationInfo applicationInfo = applicationInfoList.get(i);
				applicationInfo.wifi = "true";
				mgr.updateApplicationInfo(applicationInfo);
				setFirewallUidChainRule(applicationInfoList.get(i).uid,WIFI_TYPE,true);
			}
		} else if("wifi".equals(type) && "false".equals(btn)) {
			for(int i = 0;i<applicationInfoList.size();i++) {
				ApplicationInfo applicationInfo = applicationInfoList.get(i);
				applicationInfo.wifi = "false";
				mgr.updateApplicationInfo(applicationInfo);
				setFirewallUidChainRule(applicationInfoList.get(i).uid,WIFI_TYPE,false);
			}
		}
		mHandler.sendEmptyMessage(SAVE_ALL_INFO_SUCCESS);
	}
	Handler mHandler = new Handler() {
		@Override public void handleMessage(Message msg) {//覆盖handleMessage方法
			switch (msg.what) {//根据收到的消息的what类型处理
				case SAVE_ALL_INFO:
					break;
				case SAVE_ALL_INFO_SUCCESS:
					progressDialog.dismiss();
					applicationAdapter.notifyDataSetChanged();
					break;
				default:
					super.handleMessage(msg);//这里最好对不需要或者不关心的消息抛给父类，避免丢失消息
					break;
			}
		}
	};

	public  static void setFirewallUidChainRule(int uid, int networkType, boolean allow){
		try {
			Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
			IBinder binder = (IBinder) method.invoke(null, new Object[] {"network_management"});
			INetworkManagementService service = INetworkManagementService.Stub.asInterface(binder);
			if (service != null) {
				service.setFirewallUidChainRule(uid, networkType,allow);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
