package at.sw_xp_02.whisper.test;

import android.support.v4.app.Fragment;
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
	private android.app.Fragment fragment;

	public MainActivityTest() {
		super(MainActivity.class);
	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
//		solo.clickOnActionBarItem(R.id.action_add);
//		solo.typeText(0, "strass.michael@gmx.net");
//		solo.clickOnButton(1);
//		solo.clickOnText("strass.michael@gmx.net");
	}

	public void testPreferenceActivity() {

		solo.sendKey(Solo.MENU);
		solo.clickOnMenuItem("Settings");
		solo.assertCurrentActivity("SettingsActivity", SettingsActivity.class);

	}

	public void testSendMessage() throws InterruptedException {
		solo.clickOnText("strass.michael@gmx.net");
		solo.assertCurrentActivity("ChatActivity", ChatActivity.class);
		message = (EditText) solo.getView(R.id.msg_edit);
		solo.typeText(0, "Test Message");
		solo.clickOnButton("Send");
		solo.sleep(500);
		solo.waitForText("Test Message");
		assertTrue(message.getText().length()== 0);
		
	}
	
	public void testOfflineMessageFailure() {
		solo.clickOnText("strass.michael@gmx.net");
		solo.assertCurrentActivity("ChatActivity", ChatActivity.class);
		message = (EditText) solo.getView(R.id.msg_edit);
		solo.typeText(0, "Test Message");
		solo.clickOnButton("Send");
		solo.waitForText("Message could not be sent");
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}
}
