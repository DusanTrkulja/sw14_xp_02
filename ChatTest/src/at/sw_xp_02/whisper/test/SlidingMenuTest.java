package at.sw_xp_02.whisper.test;

import android.test.ActivityInstrumentationTestCase2;
import at.sw_xp_02.whisper.ChatActivity;
import at.sw_xp_02.whisper.R;

import com.robotium.solo.Solo;

public class SlidingMenuTest extends ActivityInstrumentationTestCase2<ChatActivity> {

	private Solo solo;
	private final static int TIME_LIMIT = 5000;

	public SlidingMenuTest() {
		super(ChatActivity.class);
	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());	

	}

	private void addDummyUser(String email) {
	    solo.clickOnActionBarItem(R.id.action_add);
        solo.typeText(0, email);
        solo.clickOnButton("OK");
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
	   

}
