package at.sw_xp_02.whisper;

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import at.sw_xp_02.whisper.DataProvider.MessageType;
import at.sw_xp_02.whisper.client.Constants;
import at.sw_xp_02.whisper.client.GcmUtil;
import at.sw_xp_02.whisper.client.ServerUtilities;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class ChatActivity extends ActionBarActivity implements MessagesFragment.OnFragmentInteractionListener, 
EditContactDialog.OnFragmentInteractionListener, OnClickListener {

	private EditText msgEdit;
	private Button sendBtn;
	private String profileId;
	private String profileName;
	private String profileEmail;
	private GcmUtil gcmUtil;
	ListView listView;
	public static PhotoCache photoCache;
	SlidingMenu menu;
	ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity);

		
		profileId = getIntent().getStringExtra(Common.PROFILE_ID);
		msgEdit = (EditText) findViewById(R.id.msg_edit);
		sendBtn = (Button) findViewById(R.id.send_btn);
		sendBtn.setOnClickListener(this);
		actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = settings.edit();
    editor.putString("lastMessageTo",profileId);
    editor.commit();
		
		// configure the SlidingMenu
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.RIGHT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		//menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowWidth(R.dimen.shadow_width);
	
		//menu.setShadowDrawable(R.drawable.shadowright);
		menu.setBehindScrollScale(0);
		menu.setFadeDegree(0);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		//menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.menu);
		
		



		Cursor c = getContentResolver().query(Uri.withAppendedPath(DataProvider.CONTENT_URI_PROFILE, profileId), null, null, null, null);
		if (c.moveToFirst()) {
			profileName = c.getString(c.getColumnIndex(DataProvider.COL_NAME));
			profileEmail = c.getString(c.getColumnIndex(DataProvider.COL_EMAIL));
			actionBar.setTitle(profileName);
		}
		actionBar.setSubtitle("connecting ...");

//		registerReceiver(registrationStatusReceiver, new IntentFilter(Common.ACTION_REGISTER));
		gcmUtil = new GcmUtil(getApplicationContext());
		
		//Send hidden message for online-status
		checkOnlineStatus();
		
	}
	
	private void checkOnlineStatus() {
		new AsyncTask<Void, Void, String>() {
			protected String doInBackground(Void... params) {
				try{
				 ServerUtilities.send(Common.ONLINE_QUESTION, profileEmail);

				} catch (IOException ex) {
					ex.printStackTrace();
				}
				return "ONLINE??";
			}
		}.execute(null, null, null);
		getSupportActionBar().setSubtitle("offline");
	}
	
	@Override 
	public void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(contactListRefreshReceiver,
				new IntentFilter("contactListRefresh"));
		checkOnlineStatus();
	}

	private BroadcastReceiver contactListRefreshReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent arg1) {
			if(arg1.getBooleanExtra("onlineStatus", false)) {
				actionBar.setSubtitle("online");
				actionBar.show();
			}
			menu.invalidate();
			menu.setMenu(R.layout.menu);
			Log.e("ChatActivity","Data refresh");
		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add_contacts:
			AddContactDialog newFragment = AddContactDialog.newInstance();
			newFragment.show(getSupportFragmentManager(), "AddContactDialog");
			return true;
		case R.id.action_show_contacts:
			menu.toggle();
			return true;
		case R.id.action_edit:
			EditContactDialog dialog = new EditContactDialog();
			Bundle args = new Bundle();
			args.putString(Common.PROFILE_ID, profileId);
			args.putString(DataProvider.COL_NAME, profileName);
			dialog.setArguments(args);
			dialog.show(getSupportFragmentManager(), "EditContactDialog");
			return true;

		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(Constants.STAY_ON_MAINSCREEN, true);
			startActivity(intent);
			return true;			
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.send_btn:
			String sendText = msgEdit.getText().toString();
			if(sendText.isEmpty()) {
				break;
			}

			send(sendText);
			msgEdit.setText(null);
			break;
		}
	}

	@Override
	public void onEditContact(String name) {
		getSupportActionBar().setTitle(name);
	}	

	@Override
	public String getProfileEmail() {
		return profileEmail;
	}	

	private void send(final String txt) {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				Encryption encrypt = new Encryption();
				String cipherText = encrypt.encrypt(Constants.ENCRYPT_KEY, txt);
				try {
					Log.e("SendingMessage","Sending message:" + txt + " to " + profileEmail);
					ServerUtilities.send(cipherText, profileEmail);
					ContentValues values = new ContentValues(2);
					values.put(DataProvider.COL_TYPE,  MessageType.OUTGOING.ordinal());
					values.put(DataProvider.COL_MESSAGE, cipherText);
					values.put(DataProvider.COL_RECEIVER_EMAIL, profileEmail);
					values.put(DataProvider.COL_SENDER_EMAIL, Common.getPreferredEmail());		
					getContentResolver().insert(DataProvider.CONTENT_URI_MESSAGES, values);

				} catch (IOException ex) {
					ex.printStackTrace();
					msg = "Message could not be sent";
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				if (!TextUtils.isEmpty(msg)) {
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
				}
			}
		}.execute(null, null, null);		
	}	

	@Override
	protected void onPause() {
		ContentValues values = new ContentValues(1);
		values.put(DataProvider.COL_COUNT, 0);
		getContentResolver().update(Uri.withAppendedPath(DataProvider.CONTENT_URI_PROFILE, profileId), values, null, null);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(contactListRefreshReceiver);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		//unregisterReceiver(registrationStatusReceiver);
		gcmUtil.cleanup();
		super.onDestroy();
	}
@Override
public void onBackPressed(){
	Intent intent = new Intent(this, MainActivity.class);
	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	intent.putExtra(Constants.STAY_ON_MAINSCREEN, true);
	startActivity(intent);

}
//	private BroadcastReceiver registrationStatusReceiver = new  BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			if (intent != null && Common.ACTION_REGISTER.equals(intent.getAction())) {
//				switch (intent.getIntExtra(Common.EXTRA_STATUS, 100)) {
//				case Common.STATUS_SUCCESS:
//					getSupportActionBar().setSubtitle("online");
//					sendBtn.setEnabled(true);
//					break;
//
//				case Common.STATUS_FAILED:
//					getSupportActionBar().setSubtitle("offline");					
//					break;					
//				}
//			}
//		}
//	};	



}

