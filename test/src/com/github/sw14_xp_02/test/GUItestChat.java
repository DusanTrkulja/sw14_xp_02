package com.github.sw14_xp_02.test;

import com.github.sw14_xp_02.ChatActivity;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

public class GUItestChat extends ActivityInstrumentationTestCase2<ChatActivity> {
	
	private Solo chatSolo;

	public GUItestChat(String ChatActivity) {
		super(ChatActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		chatSolo = new Solo(getInstrumentation(),getActivity());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testButton(){
		chatSolo.clickOnButton("Send");
	}

}
