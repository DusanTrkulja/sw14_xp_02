package at.sw_xp_02.whisper.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.ListView;
import at.sw_xp_02.whisper.ChatActivity;
import at.sw_xp_02.whisper.Common;
import at.sw_xp_02.whisper.DataProvider;
import at.sw_xp_02.whisper.MainActivity;
import at.sw_xp_02.whisper.SettingsActivity;
import at.sw_xp_02.whisper.R;
import at.sw_xp_02.whisper.client.ServerUtilities;

import com.robotium.solo.Solo;

public class OfflineTest extends
ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	private EditText message;

	public OfflineTest() {
		super(MainActivity.class);
	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());	
		
		// todo disconect wifi
		turnWifi(false);

	}
	
	private void addDummyUser(String email) {
	    solo.clickOnActionBarItem(R.id.action_add);
        solo.typeText(0, email);
        solo.clickOnButton(1);
	}
	
	protected void turnWifi(boolean enabled) {
	    try {
	        WifiManager wifiManager = (WifiManager) getInstrumentation()
	                .getTargetContext().getSystemService(Context.WIFI_SERVICE);
	        wifiManager.setWifiEnabled(enabled);
	    } catch (Exception ignored) {
	        // don't interrupt test execution, if there
	        // is no permission for that action
	    }
	}
	
	public void testOfflineMessageFailure() {
		String email = "jupaldupal@gmail.com";
		addDummyUser(email);
		solo.clickOnText(email);
		solo.assertCurrentActivity("ChatActivity", ChatActivity.class);
		message = (EditText) solo.getView(R.id.msg_edit);
		solo.typeText(0, "Test Message");
		solo.clickOnButton("Send");
		solo.sleep(5000);
		solo.getText("Message could not be sent");
	}
	
}