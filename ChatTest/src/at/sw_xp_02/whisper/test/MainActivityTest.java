package at.sw_xp_02.whisper.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import at.sw_xp_02.whisper.ChatActivity;
import at.sw_xp_02.whisper.Common;
import at.sw_xp_02.whisper.DataProvider;
import at.sw_xp_02.whisper.MainActivity;
import at.sw_xp_02.whisper.R;
import at.sw_xp_02.whisper.client.ServerUtilities;

import com.robotium.solo.Solo;

public class MainActivityTest extends
ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	private EditText message;
	private final static int TIME_LIMIT = 5000;
	private boolean enable = true;
	private boolean disable = false;

	public MainActivityTest() {
		super(MainActivity.class);
	}

	public void setUp() throws Exception {
		Activity currentActivity = getActivity();
		String activityName = currentActivity.getClass().getName();
		solo = new Solo(getInstrumentation(), currentActivity);
		if (!(activityName.equals("MainActivity"))){
			solo.hideSoftKeyboard();
			solo.goBackToActivity("MainActivity");       
		}
	}		
		
	
	
	private void addDummyUser(String email) {
		
	    solo.clickOnActionBarItem(R.id.action_add);
	    solo.waitForView(EditText.class);
        solo.typeText(0, email);
        solo.waitForText("OK",1,TIME_LIMIT);
        solo.clickOnButton("OK");
        solo.waitForDialogToClose();
	}
	
	

	public void testPreferenceActivity() {
		solo.assertCurrentActivity("MainActivity", MainActivity.class);
		solo.clickOnActionBarItem(R.id.action_settings);
		solo.waitForText("Vibrate",1,TIME_LIMIT);
		solo.clickOnText("Vibrate");
		solo.waitForText("New message notifications",1,TIME_LIMIT);
		solo.clickOnText("New message notifications");
		solo.sleep(2000);
		boolean checkboxnotify = solo.isCheckBoxChecked(0);
		boolean checkboxvibrate = solo.isCheckBoxChecked(1);
		assertFalse(checkboxnotify);
		assertFalse(checkboxvibrate);
		solo.waitForText("New message notifications",1,TIME_LIMIT);
		solo.clickOnText("New message notifications");
		solo.waitForText("Vibrate",1,TIME_LIMIT);
		solo.clickOnText("Vibrate");
		solo.sleep(500);
		checkboxnotify = solo.isCheckBoxChecked(0);
		checkboxvibrate = solo.isCheckBoxChecked(1);
		assertTrue(checkboxnotify);
		assertTrue(checkboxvibrate);
	}

	
	public void testContactChanges(){
		addDummyUser("thisismydummy@user.com");
		solo.waitForText("thisismydumm",1,TIME_LIMIT);
		solo.clickOnText("thisismydummy");
		solo.clickOnActionBarItem(R.id.action_edit);
		solo.enterText(0, "dings");
		solo.waitForText("thisismydummydings",1,TIME_LIMIT);
		solo.getText("thisismydummydings");
		
		
	}
	public void testSendMessage() throws InterruptedException {
		//solo.assertCurrentActivity("ChatActivity", ChatActivity.class);
		String email = "jupaldupal@gmail.com";
		addDummyUser(email);
		solo.waitForText(email,1,TIME_LIMIT);
		solo.clickOnText(email);
		//solo.assertCurrentActivity("ChatActivity", ChatActivity.class);
		message = (EditText) solo.getView(R.id.msg_edit);
		solo.waitForView(EditText.class);
		solo.typeText(0, "Test Message");
		solo.waitForText("Send",1,TIME_LIMIT);
		solo.waitForText("Send",1,TIME_LIMIT);
		solo.clickOnButton("Send");
		solo.waitForText("Test Message",1,TIME_LIMIT);
		solo.getText("Test Message");
		assertTrue(message.getText().length()== 0);
		
	}

    public void testDeleteUser() {
    	solo.hideSoftKeyboard();
    	enableDebugMode(enable);
        String user = "dummy@dum.dum";
        addDummyUser(user);
        solo.waitForText(user,1,TIME_LIMIT);
        solo.clickLongOnText(user);
        solo.sleep(1000);
        solo.waitForText("Delete",1,TIME_LIMIT);
        solo.clickOnText("Delete");
        assert(!solo.searchText(user));
        enableDebugMode(disable);
    }
    
    
   public void testContactAdd() {
  	 String email = "jupaldupal@gmail.com";
  	 solo.clickOnActionBarItem(R.id.action_add);
  	solo.waitForView(EditText.class);
     solo.typeText(0, email);
     solo.waitForText("OK",1,TIME_LIMIT);
     solo.clickOnButton("OK");
     solo.getText(email);
   }
   
   public void testInvalidEmail() {
	  	 String email = "jupaldupal@gmail";
	  	 solo.clickOnActionBarItem(R.id.action_add);
	     solo.waitForView(EditText.class);
	     solo.typeText(0, email);
	     solo.waitForText("OK",1,TIME_LIMIT);
	     solo.clickOnButton("OK");
	     solo.getText("Invalid email!");
	   }
   
   public void testSlidingContacts() {
  	 String email = "jupaldupal@gmail.com";
  	 String email2 = "dummy@dum.dum";
  	 addDummyUser(email);
  	 addDummyUser(email2);
  	solo.waitForText(email,1,TIME_LIMIT);
  	 solo.clickOnText(email);
  	 solo.assertCurrentActivity("ChatActivity", ChatActivity.class);
  	 solo.scrollViewToSide(solo.getView(R.id.msg_list), Solo.RIGHT);
  	solo.waitForText("dummy",1,TIME_LIMIT);
  	 solo.clickOnText("dummy");
   }
   
   public void testSlidingContactsChange() {
  	 String email = "jupaldupal@gmail.com";
  	 String email2 = "dummyyy@dum.dum";
  	 String dummymessage = "Top of the mountain";
  	 addDummyUser(email);
  	 addDummyUser(email2);
  	solo.waitForText(email2,1,TIME_LIMIT);
  	 solo.clickOnText(email2);
  	solo.waitForView(EditText.class);
  	 solo.typeText(0, dummymessage);
  	solo.waitForText("Send",1,TIME_LIMIT);
  	 solo.clickOnButton("Send");
  	 solo.goBack();
  	 solo.clickOnText(email);
  	// solo.assertCurrentActivity("ChatActivity", ChatActivity.class);
  	 solo.scrollViewToSide(solo.getView(R.id.msg_list), Solo.RIGHT);
  	 solo.waitForText("dummyyy",1,TIME_LIMIT);
  	 solo.clickOnText("dummyyy");
  	 //solo.assertCurrentActivity("ChatActivity", ChatActivity.class);
  	 solo.waitForText(dummymessage,1,TIME_LIMIT);
  	 solo.getText(dummymessage);
   }
   
   public void testAddContactInChatActivity() {
	   

  	 String email = "jupaldupal@gmail.com";
  	 String email2 = "dummy@dum.dum";
  	 String email3 = "thisismy@test.user";
  	 addDummyUser(email3);
  	 //solo.clickOnActionBarItem(R.id.action_add);
  	solo.waitForText(email3,1,TIME_LIMIT);
  	 solo.clickOnText(email3);
  	 solo.hideSoftKeyboard();
  	 solo.sleep(1000);
  	 solo.clickOnActionBarItem(R.id.action_add_contacts);
  	solo.waitForView(EditText.class);
     solo.typeText(0, email2);
     solo.waitForText("OK",1,TIME_LIMIT);
     solo.clickOnButton("OK");
     //solo.clickOnActionBarItem(R.id.action_show_contacts);
  	 solo.goBack();
     solo.getText("dummy");
   }
   
   public void testContactMenuOptionBar() {
  	 String email = "jupaldupal@gmail.com";
  	 String email2 = "dummtext@dum.dum";
  	 addDummyUser(email);
  	 addDummyUser(email2);
  	solo.waitForText(email,1,TIME_LIMIT);
  	 solo.clickOnText(email);
  	 solo.clickOnActionBarItem(R.id.action_show_contacts);
  	solo.waitForText("dummytext",1,TIME_LIMIT);
  	 solo.clickOnText("dummtext");
   }
   
   public void testLayout(){
	   //	solo.getCurrentActivity().getActionBar().
   }
   
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
		assert(!solo.waitForText("jowossoll.denndos@whatevermail.com",1,TIME_LIMIT));	
		}
   
   public void testOnlineStatus() {
  	 enableDebugMode(enable);
  	 String email = Common.getPreferredEmail();
  	 addDummyUser(email);
  	 solo.waitForText(email,1,TIME_LIMIT);
 	 solo.clickOnText(email, 2);
  	 solo.waitForText("online",1,TIME_LIMIT);
  	 solo.getText("online");
  	 solo.goBack();
 	 enableDebugMode(disable);
   }
   
   public void testDebugMode(){
	   enableDebugMode(enable);
	   solo.getText("DEBUG-MODE Enabled.");
	   enableDebugMode(disable);
	   solo.getText("DEBUG-MODE Disabled.");
   }
  
   private void enableDebugMode(Boolean enable) {
  	 //solo.goBackToActivity("MainActivity");
  	 solo.clickOnActionBarItem(R.id.action_add);
  	solo.waitForView(EditText.class);
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
