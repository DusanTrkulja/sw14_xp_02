package at.sw_xp_02.whisper.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.sax.StartElementListener;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import at.sw_xp_02.whisper.ChatActivity;
import at.sw_xp_02.whisper.Common;
import at.sw_xp_02.whisper.DataProvider;
import at.sw_xp_02.whisper.MainActivity;
import at.sw_xp_02.whisper.R;
import at.sw_xp_02.whisper.SettingsActivity;
import at.sw_xp_02.whisper.client.Constants;
import at.sw_xp_02.whisper.client.ServerUtilities;

import com.robotium.solo.Solo;

public class MainActivityTest extends
ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	private EditText message;
	private final static int TIME_LIMIT = 5000;
	private boolean enable = true;
	//private boolean disable = false;

	public MainActivityTest() {
		super(MainActivity.class);
	}

	public void setUp() throws Exception {
		Activity currentActivity = getActivity();
		String activityName = currentActivity.getClass().getName();
		solo = new Solo(getInstrumentation(), currentActivity);
		if (!(activityName.equals("MainActivity"))){
			solo.hideSoftKeyboard();
//			//solo.sleep(2000);
			//solo.goBack();       
//			solo.sleep(2000);
			//enableDebugMode(enable);
//			addDummyUser("blargl@blabla.com");
//			//solo.sleep(1000);
//			//solo.clickOnActionBarHomeButton();// pressMenuItem(R.id.home);
//			
//			solo.searchText("blargl");
		}
	}		
		
	
	
//	
//	public void testDeleteAllContacts() {
//		ArrayList<View> list = solo.getCurrentViews();
//		
//		while(list.size() != 0) {
//			solo.clickLongInList(0);
//			solo.clickOnText("Delete");		
//		}
//	}
	
//	public void testGoBackFromChatActivity(){
//		
//			// do something
////			Intent intent = new Intent(getActivity(), MainActivity.class);
////			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////			intent.putExtra(Constants.STAY_ON_MAINSCREEN, true);
//			//solo.waitForActivity(MainActivity.class);
//			solo.hideSoftKeyboard();
//			//solo.sleep(2000);
//			solo.goBack();
//			solo.sleep(2000);
//			enableDebugMode(enable);
//			addDummyUser("blargl@blabla.com");
//			//solo.sleep(1000);
//			//solo.clickOnActionBarHomeButton();// pressMenuItem(R.id.home);
//			
//			solo.searchText("blargl");
//		
//	}

	private void addDummyUser(String email) {
		
	    solo.clickOnActionBarItem(R.id.action_add);
	    solo.waitForDialogToOpen(TIME_LIMIT);
        solo.typeText(0, email);
        solo.clickOnButton("OK");
        solo.sleep(3000);
        //solo.waitForDialogToClose();
	}

	public void testPreferenceActivity() {
		solo.assertCurrentActivity("MainActivity", MainActivity.class);
		solo.clickOnActionBarItem(R.id.action_settings);
		solo.assertCurrentActivity("SettingsActivity", SettingsActivity.class);

	}

	public void testSendMessage() throws InterruptedException {
		//solo.assertCurrentActivity("ChatActivity", ChatActivity.class);
		String email = "jupaldupal@gmail.com";
		addDummyUser(email);
		solo.clickOnText(email);
		//solo.assertCurrentActivity("ChatActivity", ChatActivity.class);
		message = (EditText) solo.getView(R.id.msg_edit);
		solo.typeText(0, "Test Message");
		solo.waitForText("Send");
		solo.clickOnButton("Send");
		solo.waitForText("Test Message",1,TIME_LIMIT);
		assertTrue(message.getText().length()== 0);
		
	}
	
	public void testOfflineMessageFailure() {
		solo.clickOnText("");
		solo.assertCurrentActivity("ChatActivity", ChatActivity.class);
		message = (EditText) solo.getView(R.id.msg_edit);
		solo.typeText(0, "Test Message");
		solo.clickOnButton("Send");
		solo.waitForText("Message could not be sent");
	}

    public void testDeleteUser() {
    	solo.hideSoftKeyboard();
    	//solo.goBackToActivity("MainActivity");
    	enableDebugMode(enable);
        String user = "dummy@dum.dum";
        addDummyUser(user);
        solo.clickLongOnText(user);
        solo.sleep(1000);
        solo.clickOnText("Delete");
        assert(!solo.searchText(user));
    }
    
    
    
   public void testContactAdd() {
  	 String email = "jupaldupal@gmail.com";
  	 solo.clickOnActionBarItem(R.id.action_add);
     solo.typeText(0, email);
     solo.clickOnButton("OK");
     solo.waitForText(email,1,TIME_LIMIT);
   }
   
   public void testSlidingContacts() {
  	 String email = "jupaldupal@gmail.com";
  	 String email2 = "dummy@dum.dum";
  	 addDummyUser(email);
  	 addDummyUser(email2);
  	 solo.clickOnText(email);
  	 solo.assertCurrentActivity("ChatActivity", ChatActivity.class);
  	 solo.scrollViewToSide(solo.getView(R.id.msg_list), Solo.RIGHT);
  	 solo.clickOnText("dummy");
   }
   
   public void testSlidingContactsChange() {
  	 String email = "jupaldupal@gmail.com";
  	 String email2 = "dummy@dum.dum";
  	 String dummymessage = "Top of the mountain";
  	 addDummyUser(email);
  	 addDummyUser(email2);
  	 solo.clickOnText(email2);
  	 solo.typeText(0, dummymessage);
  	 solo.clickOnButton("Send");
  	 solo.goBack();
  	 solo.clickOnText(email);
  	 solo.assertCurrentActivity("ChatActivity", ChatActivity.class);
  	 solo.scrollViewToSide(solo.getView(R.id.msg_list), Solo.RIGHT);
  	 solo.clickOnText("dummy");
  	 solo.assertCurrentActivity("ChatActivity", ChatActivity.class);
  	 solo.waitForText(dummymessage,1,TIME_LIMIT);
   }
   
   public void testAddContactInChatActivity() {
	   solo.goBackToActivity("MainActivity");
  	 String email = "jupaldupal@gmail.com";
  	 String email2 = "dummy@dum.dum";
  	 String email3 = "thisismy@test.user";
  	 addDummyUser(email3);
  	 //solo.clickOnActionBarItem(R.id.action_add);
  	 solo.clickOnText(email3);
  	 solo.hideSoftKeyboard();
  	 solo.sleep(1000);
  	 solo.clickOnActionBarItem(R.id.action_add_contacts);
     solo.typeText(0, email2);
     solo.clickOnButton("OK");
     //solo.clickOnActionBarItem(R.id.action_show_contacts);
  	 solo.goBack();
     solo.waitForText("dummy",1,TIME_LIMIT);
   }
   
   public void testContactMenuOptionBar() {
  	 String email = "jupaldupal@gmail.com";
  	 String email2 = "dummtext@dum.dum";
  	 addDummyUser(email);
  	 addDummyUser(email2);
  	 solo.clickOnText(email);
  	 solo.clickOnActionBarItem(R.id.action_show_contacts);
  	 solo.clickOnText("dummtext");
   }
   
   
//   public void testReceiveMessage() {
//	   String debug = "#debug*!";
//	   String email = Common.getPreferredEmail();
//	   addDummyUser(debug);
//	   solo.waitForText("DEBUG-MODE Enabled.");
//	   addDummyUser(email);
//	   solo.clickOnText(email);
//	   //solo.assertCurrentActivity("ChatActivity", ChatActivity.class);
//	   message = (EditText) solo.getView(R.id.msg_edit);
//	   solo.typeText(0, "Test Message");
//	   solo.clickOnButton("Send");
//	   solo.getText("Test Message");
//	   
//	   
//	   
//   }
   
   public void testAddUnknownUser(){
	//public static void send(String msg, String to) throws IOException {
		//Log.i(TAG, "sending message (msg = " + msg + ")");
		String serverUrl = Common.getServerUrl() + "/send";
		Map<String, String> params = new HashMap<String, String>();
		params.put(DataProvider.MESSAGE, "testmsg");
		params.put(DataProvider.SENDER_EMAIL, "jowossoll.denndos@whatevermail.com");
		params.put(DataProvider.RECEIVER_EMAIL, Common.getPreferredEmail());        
			try {
				ServerUtilities.post(serverUrl, params, 5);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		assert(!solo.searchText("jowossoll.denndos@whatevermail.com"));	
		}
   
   public void testOnlineStatus() {
  	 enableDebugMode(true);
  	 addDummyUser(Common.getPreferredEmail());
  	 solo.clickOnText(Common.getPreferredEmail());
  	 solo.waitForText("online", 1, 5000);
   }
  
   private void enableDebugMode(Boolean enable) {
  	 //solo.goBackToActivity("MainActivity");
  	 solo.clickOnActionBarItem(R.id.action_add);
  	 if(enable)
  		 solo.typeText(0, Common.DEBUG);
  	 else
  		 solo.typeText(0, Common.NODEBUG);
     solo.clickOnButton(1);
   }
   
	@Override
	public void tearDown() throws Exception {
		//enableDebugMode(false);
		solo.finishOpenedActivities();
		
	}
}
