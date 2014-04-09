package com.appsrox.instachat.test;

import junit.framework.Assert;
import android.test.ActivityInstrumentationTestCase2;

import com.appsrox.instachat.MainActivity;
import com.appsrox.instachat.SettingsActivity;
import com.robotium.solo.Solo;

public class MainActivityTest extends
ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;

	public MainActivityTest() {
		super(MainActivity.class);
	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void testPreferenceActivity() throws Exception {

		solo.sendKey(Solo.MENU);
		solo.clickOnMenuItem("Settings");
		solo.assertCurrentActivity("SettingsActivity", SettingsActivity.class);

	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}
}
