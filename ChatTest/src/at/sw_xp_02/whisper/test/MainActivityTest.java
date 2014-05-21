package at.sw_xp_02.whisper.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import at.sw_xp_02.whisper.ChatActivity;
import at.sw_xp_02.whisper.MainActivity;
import at.sw_xp_02.whisper.SettingsActivity;
import at.sw_xp_02.whisper.R;

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

	private void addDummyUser(String email) {
	    solo.clickOnActionBarItem(R.id.action_add);
        solo.typeText(0, email);
        solo.clickOnButton(1);
	}

	public void testPreferenceActivity() {

		solo.sendKey(Solo.MENU);
		solo.clickOnMenuItem("Settings");
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
  	 if(!solo.searchText("dummy"))
  	 {
  		 solo.scrollViewToSide(solo.getView(R.id.msg_list), solo.DOWN);
  	 }
  	 solo.getText("dummy");
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
  	 solo.clickOnActionBarItem(R.id.action_add);
     solo.typeText(0, email2);
     solo.clickOnButton(1);
     solo.sleep(500);
     if(!solo.searchText("dummy"))
  	 {
  		 solo.scrollViewToSide(solo.getView(R.id.msg_list), solo.DOWN);
  	 }
  	 solo.getText("dummy");
   }
   
   public void testContactMenuOptionBar() {
  	 String email = "jupaldupal@gmail.com";
  	 String email2 = "dummy@dum.dum";
  	 String dummymessage = "Top of the mountain";
  	 addDummyUser(email);
  	 addDummyUser(email2);
  	 solo.clickOnText(email);
  	 solo.clickOnActionBarItem(R.id.action_show_contacts);
  	 if(!solo.searchText("dummy"))
  	 {
  		 solo.scrollViewToSide(solo.getView(R.id.msg_list), solo.DOWN);
  	 }
  	 solo.getText("dummy");
   }
   

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}
}
