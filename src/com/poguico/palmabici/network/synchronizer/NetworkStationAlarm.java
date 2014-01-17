package com.poguico.palmabici.network.synchronizer;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import com.poguico.palmabici.R;
import com.poguico.palmabici.util.NetworkInformation;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NetworkStationAlarm extends IntentService {

	private static ArrayList<String> stationAlarms = null;
	private static boolean active = false;
	
	private NetworkInformation networkInformation;
	private static Context context;
	private Semaphore semaphore;
	
	public NetworkStationAlarm() {
		super("NetworkStationAlarm");
		Log.i("NetworkStationAlarm", "Initializing class");
		active = true;
		networkInformation = NetworkInformation.getInstance(context);
		semaphore = new Semaphore(1);
	}

	public static synchronized void addAlarm(Context context, String stationId) {
		if (stationAlarms == null) {
			Log.i("NetworkStationAlarm", "Initializing alarms list");
			NetworkStationAlarm.context = context;
			stationAlarms = new ArrayList<String>();
		}
		Log.i("NetworkStationAlarm", "Adding alarm for station " + stationId);
		stationAlarms.add(stationId);
	}
	
	public static synchronized void removeAlarm(String stationId) {
		if (stationAlarms.contains(stationId)) {
			stationAlarms.remove(stationId);
			Log.i("NetworkStationAlarm", "Alarm for station " + stationId + " removed");
		}
	}
	
	@Override
	protected void onHandleIntent(Intent arg0) {
		Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.launcher)
		        .setContentTitle("AlarmService")
		        .setContentText("Alarm service finished!")
		        .setSound(uri);
		
		NotificationManager mgr=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		Log.i("NetworkStationAlarm", "Starting thread");
		while (!NetworkStationAlarm.stationAlarms.isEmpty()) {
			Log.i("NetworkStationAlarm", "Getting network info...");
			NetworkSynchronizerTask.synchronize(context, new NetworkSyncCallback() {
				
				@Override
				public void onNetworkSynchronized(long updateTime) {
					Log.i("NetworkStationAlarm", "Network synchronized");
					for (String id : stationAlarms) {
						Log.i("NetworkStationAlarm", "Station " + id + " has " + networkInformation.get(id).getBusySlots() + "bikes available");
						if (networkInformation.get(id).getBusySlots() > 0) {
							//notify
							removeAlarm(id);
						}
					}
					semaphore.release();
				}
				
				@Override
				public void onNetworkError(String errorCode) {
					Log.i("NetworkStationAlarm", "Error when synchornizing");
					semaphore.release();
				}
			});
			
			try {
				semaphore.acquire();
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Log.i("NetworkStationAlarm", "Finishing thread");
		mgr.notify(1234, mBuilder.build());
	}

	@Override
	public void onDestroy() {
		Log.i("NetworkStationAlarm", "Destroying class");
		active = false;
		super.onDestroy();
	}

	public static boolean isActive() {
		return active;
	}
	
	public static boolean hasAlarm(String id) {
		return stationAlarms != null && stationAlarms.contains(id);
	}
}
