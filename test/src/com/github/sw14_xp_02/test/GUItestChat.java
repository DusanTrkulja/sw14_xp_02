package com.github.sw14_xp_02.test;

import com.github.sw14_xp_02.ChatActivity;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

public class GUItestChat extends ActivityInstrumentationTestCase2<ChatActivity> {

	
	private Solo ChatSolo;
	
	public GUItestChat(String ChatActivity) {
		super(ChatActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		ChatSolo = new Solo(getInstrumentation(),getActivity());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

//	public void testOnCreateBundle() {
//		fail("Not yet implemented");
//	}
//
//	public void testStartActivityIntent() {
//		fail("Not yet implemented");
//	}
	
	public void testButtons() {
		ChatSolo.clickOnButton("Send");
	}
	
	public void testMessageField() {
		ChatSolo.clickOnEditText(0);
		ChatSolo.enterText(0, "Test");
		ChatSolo.getText("Test");
	}
	
	public void testChatmessage() {
		ChatSolo.clickOnEditText(0);
		ChatSolo.enterText(0, "Test");
		ChatSolo.getText("Test");
		
		ChatSolo.clickOnButton("Send");
	}

}
