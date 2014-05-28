package at.sw_xp_02.whisper.test;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import at.sw_xp_02.whisper.ChatActivity;
import at.sw_xp_02.whisper.MainActivity;
import at.sw_xp_02.whisper.R;


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
		setMobileDataEnabled(getActivity(),false);
		solo.goBackToActivity("MainActivity");
	}
	
//	private void enableDebugMode(Boolean enable) {
//		solo.goBackToActivity("MainActivity"); 
//		solo.clickOnActionBarItem(R.id.action_add);
//		 if(enable)
//			 solo.typeText(0, "#debug*!");
//		 else
//			 solo.typeText(0, "#nodebug*!");
//     solo.clickOnButton(1);
//	}
	
	private void addDummyUser(String email) {
	    solo.clickOnActionBarItem(R.id.action_add);
        solo.typeText(0, email);
        solo.clickOnButton(1);
	}
	
	private void setMobileDataEnabled(Context context, boolean enabled) {
	
		try {
			final ConnectivityManager conman = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
		  final Class conmanClass = Class.forName(conman.getClass().getName());
		  final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
		  iConnectivityManagerField.setAccessible(true);
		  final Object iConnectivityManager = iConnectivityManagerField.get(conman);
		  final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
		  final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
		  setMobileDataEnabledMethod.setAccessible(true);
		  setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
		} catch (Exception ignored) {
			// TODO Auto-generated catch block
		}
	  
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
		solo.waitForText("Message could not be sent");
	}
	
	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		
		setMobileDataEnabled(getActivity(),true);

		// todo connect wifi
		turnWifi(true);
    
	}
}