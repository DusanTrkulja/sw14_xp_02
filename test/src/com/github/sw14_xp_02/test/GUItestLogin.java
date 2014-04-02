package com.github.sw14_xp_02.test;

import com.github.sw14_xp_02.LoginActivity;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

public class GUItestLogin extends ActivityInstrumentationTestCase2<LoginActivity> {

	private Solo LoginSolo;
	
	public GUItestLogin(String LoginActivity) {
		super(LoginActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		LoginSolo = new Solo(getInstrumentation(), getActivity());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testButtons(){
		LoginSolo.clickOnButton("Login");
		LoginSolo.clickOnButton("Register");
	}
	
	public void testInputFields() {
		LoginSolo.clickOnEditText(0);
		LoginSolo.typeText(0, "email@test.com");
		LoginSolo.getText("email@test.com");
		LoginSolo.clickOnEditText(1);
		LoginSolo.typeText(1, "password");
		LoginSolo.getText("password");
	}
	
	public void testLogin() {
		LoginSolo.clickOnEditText(0);
		LoginSolo.typeText(0, "admin");
		LoginSolo.getText("admin");
		LoginSolo.clickOnEditText(1);
		LoginSolo.typeText(1, "admin");
		LoginSolo.getText("admin");
		LoginSolo.sleep(500);
		LoginSolo.getText("Redirecting");
	}

}
