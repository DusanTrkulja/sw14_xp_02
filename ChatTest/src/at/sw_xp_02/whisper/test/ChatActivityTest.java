package at.sw_xp_02.whisper.test;

import android.test.ActivityInstrumentationTestCase2;
import at.sw_xp_02.whisper.ChatActivity;
import at.sw_xp_02.whisper.MainActivity;

import com.robotium.solo.Solo;

public class ChatActivityTest extends
ActivityInstrumentationTestCase2<ChatActivity> {

	private Solo solo;

	public ChatActivityTest() {
		super(ChatActivity.class);
	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void testSendButton() {

//		solo.clickOnButton("Send");

	}


	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}
}
