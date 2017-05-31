package settings.hometech.com.myapplication;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

public class ResetDataService extends Service {
	private List<ApplicationInfo> applicationInfoList = null;
	private DBManager mgr;
	private static int WIFI_TYPE = 1;
	private static int GPRS_TYPE = 0;
	@Override
	public IBinder onBind(Intent intent){
		return null;
	}

	@Override
	public void onCreate() {
		Log.i("yimin","start service");
		mgr = new DBManager(this);
		applicationInfoList = mgr.queryApplication();
		for(int i = 0;i < applicationInfoList.size(); i++){
			ApplicationInfo applicationInfo = applicationInfoList.get(i);
			String gprs = applicationInfo.gprs;
			String wifi = applicationInfo.wifi;
			int uid = applicationInfo.uid;
			if("true".equals(gprs)) {
				setFirewallUidChainRule(uid,GPRS_TYPE,true);
			} else {
				setFirewallUidChainRule(uid,GPRS_TYPE,false);
			}
			if("true".equals(wifi)) {
				setFirewallUidChainRule(uid,WIFI_TYPE,true);
			} else {
				setFirewallUidChainRule(uid,WIFI_TYPE,false);
			}
		}
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

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
