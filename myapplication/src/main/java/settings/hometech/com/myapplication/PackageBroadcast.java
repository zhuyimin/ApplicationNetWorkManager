/**
 * Broadcast receiver responsible for removing rules that affect uninstalled apps.
 * 
 * Copyright (C) 2009-2011  Rodrigo Zechin Rosauro
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author Rodrigo Zechin Rosauro
 * @version 1.0
 */
package settings.hometech.com.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;

/**
 * Broadcast receiver responsible for removing rules that affect uninstalled apps.
 */
public class PackageBroadcast extends BroadcastReceiver {
	private DBManager mgr;
	private static int WIFI_TYPE = 1;
	private static int GPRS_TYPE = 0;
	@Override
	public void onReceive(Context context, Intent intent) {
		mgr = new DBManager(context);
		if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
			// Ignore application updates
			final boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
			if (!replacing) {
				Log.i("yimin","ACTION_PACKAGE_REMOVED");
				// Update the Firewall if necessary
				int uid = intent.getIntExtra(Intent.EXTRA_UID, -123);
				String packageName = intent.getData().getSchemeSpecificPart();
				setFirewallUidChainRule(uid,WIFI_TYPE,false);
				setFirewallUidChainRule(uid,GPRS_TYPE,false);
				//String appName = getProgramNameByPackageName(context,packageName);
				ApplicationInfo applicationInfo = new ApplicationInfo(uid,packageName);
				mgr.deleteApplication(applicationInfo);
				Log.i("yimin","uid:" + uid + "   packageName:" + packageName);
				//Api.applicationRemoved(context, uid);
				// Force app list reload next time
				//Api.applications = null;
			}
		} else if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
			// Force app list reload next time
			Log.i("yimin","ACTION_PACKAGE_ADDED");
			int uid = intent.getIntExtra(Intent.EXTRA_UID, -123);
			byte[] icon = null;
			String packageName = intent.getData().getSchemeSpecificPart();
			String appName = getProgramNameByPackageName(context,packageName);
			try {
				Drawable drawable = context.getPackageManager().getPackageInfo(packageName,0).applicationInfo.loadIcon(context.getPackageManager());
				icon = BitmapToBytes(drawableToBitmap(drawable));
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
			ApplicationInfo applicationInfo = new ApplicationInfo(uid,"false","false",icon,"false","false",packageName,"false",appName);
			//mgr.deleteApplication(applicationInfo);
			mgr.addApplication(applicationInfo);
			Log.i("yimin","uid:" + uid + "   packageName:" + packageName + "   appName:" + appName);
			//Api.applications = null;
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
	byte[] BitmapToBytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
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
