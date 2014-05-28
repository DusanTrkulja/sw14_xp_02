package at.sw_xp_02.whisper.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

public class MainActivityTest extends
ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	private EditText message;

	public MainActivityTest() {
		super(MainActivity.class);
	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());	

	}
	
	
//	
//	public void testDeleteAllContacts() {
//		ArrayList<ListView> list = solo.getCurrentListViews();
//		
//		while(list.size() != 0) {
//			solo.clickLongInList(0);
//			solo.clickOnText("Delete");		
//		}
//	}

	private void addDummyUser(String email) {
	    solo.clickOnActionBarItem(R.id.action_add);
        solo.typeText(0, email);
        solo.clickOnButton(1);
	}

	public void testPreferenceActivity() {

		solo.clickOnActionBarItem(R.id.action_settings);
		solo.assertCurrentActivity("SettingsActivity", SettingsActivity.class);

	}

	public void testSendMessage() throws InterruptedException {
		String email = "jupaldupal@gmail.com";
		addDummyUser(email);
		solo.clickOnText(email);
		solo.assertCurrentActivity("ChatActivity", ChatActivity.class);
		message = (EditText) solo.getView(R.id.msg_edit);
		solo.typeText(0, "Test Message");
		solo.clickOnButton("Send");
		solo.sleep(500);
		solo.waitForText("Test Message");
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
        String user = "dummy@dum.dum";

        addDummyUser(user);
        solo.clickLongOnText(user);
        solo.clickOnText("Delete");
        assert(!solo.searchText(user));
    }
    
    
    
   public void testContactAdd() {
  	 String email = "jupaldupal@gmail.com";
  	 solo.clickOnActionBarItem(R.id.action_add);
     solo.typeText(0, email);
     solo.clickOnButton(1);
     solo.sleep(500);
     solo.getText(email);
   }
   
   public void testSlidingContacts() {
  	 String email = "jupaldupal@gmail.com";
  	 String email2 = "dummy@dum.dum";
  	 addDummyUser(email);
  	 addDummyUser(email2);
  	 solo.clickOnText(email);
  	 solo.assertCurrentActivity("ChatActivity", ChatActivity.class);
  	 solo.scrollViewToSide(solo.getView(R.id.msg_list), solo.RIGHT);
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
  	 solo.scrollViewToSide(solo.getView(R.id.msg_list), solo.RIGHT);
  	 solo.clickOnText("dummy");
  	 solo.assertCurrentActivity("ChatActivity", ChatActivity.class);
  	 solo.getText(dummymessage);
   }
   
   public void testAddContactInChatActivity() {
  	 String email = "jupaldupal@gmail.com";
  	 String email2 = "dummy@dum.dum";
  	 addDummyUser(email);
  	 
  	 solo.clickOnText(email);
  	 solo.clickOnActionBarItem(R.id.action_add_contacts);
     solo.typeText(0, email2);
     solo.clickOnButton(1);
     solo.clickOnActionBarItem(R.id.action_show_contacts);
     solo.sleep(500);
  	 solo.getText("dummy");
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
  	 solo.goBackToActivity("MainActivity");
  	 solo.clickOnActionBarItem(R.id.action_add);
  	 if(enable)
  		 solo.typeText(0, Common.DEBUG);
  	 else
  		 solo.typeText(0, Common.NODEBUG);
     solo.clickOnButton(1);
   }
   
	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		enableDebugMode(false);
	}
}
