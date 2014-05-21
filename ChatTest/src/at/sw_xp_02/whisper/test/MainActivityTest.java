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
     solo.searchText(email);
   }

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}
}
