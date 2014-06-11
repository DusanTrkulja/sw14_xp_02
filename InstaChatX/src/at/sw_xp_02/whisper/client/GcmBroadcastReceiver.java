package at.sw_xp_02.whisper.client;

import java.io.IOException;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import at.sw_xp_02.whisper.Common;
import at.sw_xp_02.whisper.DataProvider;
import at.sw_xp_02.whisper.DataProvider.MessageType;
import at.sw_xp_02.whisper.MainActivity;
import at.sw_xp_02.whisper.R;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmBroadcastReceiver extends BroadcastReceiver {
	
	private static final String TAG = "GcmBroadcastReceiver";
	private Context ctx;
	private String senderEmail;

	@Override
	public void onReceive(Context context, Intent intent) {
		ctx = context;
		PowerManager mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		WakeLock mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		mWakeLock.acquire();
		
		try {
			GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
			String messageType = gcm.getMessageType(intent);
			String msg = intent.getStringExtra(DataProvider.COL_MESSAGE);
			String prefix = msg.substring(0, Common.OTRPREFIX.length()-1);
			senderEmail = intent.getStringExtra(DataProvider.COL_SENDER_EMAIL);
			Log.d("Msg Received", "Received message: " + msg);
			Log.d("Msg Received", "Received from: "+ intent.getStringExtra(DataProvider.COL_SENDER_EMAIL));
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				sendNotification("Send error", false);
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				sendNotification("Deleted messages on server", false);
			} else if(msg.equals(Common.ONLINE_QUESTION)) {
				new AsyncTask<Void, Void, String>() {
					protected String doInBackground(Void... params) {
						try{
							ServerUtilities.send(Common.ONLINE_ANSWER, senderEmail);
						} catch (IOException ex) {
							ex.printStackTrace();
						}
						return Common.ONLINE_ANSWER;
					}
				}.execute(null, null, null);	
			} else if(msg.equals(Common.ONLINE_ANSWER)) {
				Intent intent2 = new Intent("contactListRefresh");
				intent2.putExtra("onlineStatus", true);
				LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
			} else if(prefix.equals(Common.OTRPREFIX)) {
				Intent intent2 = new Intent("OTRMessage");
				intent2.putExtra("message", msg.substring(Common.OTRPREFIX.length()));
				LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
			} else {
				String senderEmail = intent.getStringExtra(DataProvider.COL_SENDER_EMAIL);
				String receiverEmail = intent.getStringExtra(DataProvider.COL_RECEIVER_EMAIL);
				ContentValues values = new ContentValues(2);
				values.put(DataProvider.COL_TYPE,  MessageType.INCOMING.ordinal());				
				values.put(DataProvider.COL_MESSAGE, msg);
				values.put(DataProvider.COL_SENDER_EMAIL, senderEmail);
				values.put(DataProvider.COL_RECEIVER_EMAIL, receiverEmail);
				
				Cursor otherUser = context.getContentResolver().query(DataProvider.CONTENT_URI_PROFILE,null,DataProvider.COL_EMAIL + "= ?",new String[] { senderEmail},null);
				if(otherUser.getCount() == 0) {
					ContentValues values2 = new ContentValues(2);
					values2.put(DataProvider.COL_NAME, senderEmail.substring(0, senderEmail.indexOf('@')));
					values2.put(DataProvider.COL_EMAIL, senderEmail);
					context.getContentResolver().insert(DataProvider.CONTENT_URI_PROFILE, values2);
				}
                
				context.getContentResolver().insert(DataProvider.CONTENT_URI_MESSAGES, values);
				
				if (Common.isNotify()) {
					sendNotification("New message", true);
				}
				
				Intent intent2 = new Intent("contactListRefresh");
				LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
			}
			setResultCode(Activity.RESULT_OK);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		finally {
			mWakeLock.release();
		}
	}

	@SuppressWarnings("deprecation")
	private void sendNotification(String text, boolean launchApp) {
		NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder notification = new NotificationCompat.Builder(ctx);
		notification.setContentTitle(ctx.getString(R.string.app_name));
		notification.setContentText(text);
		notification.setAutoCancel(true);
		notification.setSmallIcon(R.drawable.logo);
		notification.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
		
		if (!TextUtils.isEmpty(Common.getRingtone())) {
			notification.setSound(Uri.parse(Common.getRingtone()));
		}
		
		if (launchApp) {
			Intent intent = new Intent(ctx, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent pi = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			notification.setContentIntent(pi);
		}
	
		mNotificationManager.notify(1, notification.build());
	}
}