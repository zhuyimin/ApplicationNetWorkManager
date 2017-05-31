package settings.hometech.com.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Broadcast receiver that set iptables rules on system startup.
 * This is necessary because the rules are not persistent.
 */
public class BootBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			Intent resetService=new Intent(context, ResetDataService.class);
			Log.i("yimin","boot complete");
        	context.startService(resetService);
			//Api.saveRules(context);
		}
	}

}
