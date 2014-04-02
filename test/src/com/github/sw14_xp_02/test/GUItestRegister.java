//package com.github.sw14_xp_02.test;
//
//import com.robotium.solo.Solo;
//
//import android.test.ActivityInstrumentationTestCase2;
//
//public class GUItestRegister extends ActivityInstrumentationTestCase2<RegisterActivity> {
//
//	
//	private Solo RegisterSolo;
//	
//	public GUItestRegister(String RegisterActivity) {
//		super(RegisterActivity.class);
//	}
//
//	protected void setUp() throws Exception {
//		super.setUp();
//		RegisterSolo = new Solo(getInstrumentation(),getActivity());
//	}
//
//	protected void tearDown() throws Exception {
//		super.tearDown();
//	}
//
//	
//	public void testButtons() {
//		RegisterSolo.clickOnButton("Register");
//	}
//	
//	public void testInputFields() {
//		RegisterSolo.clickOnEditText(0);
//		RegisterSolo.typeText(0, "Max");
//		RegisterSolo.getText("Max");
//		
//		RegisterSolo.clickOnEditText(1);
//		RegisterSolo.typeText(1, "Mustermann");
//		RegisterSolo.getText("Mustermann");
//		
//		RegisterSolo.clickOnEditText(2);
//		RegisterSolo.typeText(2, "max@mustermann.com");
//		RegisterSolo.getText("max@mustermann.com");
//		
//		RegisterSolo.clickOnEditText(3);
//		RegisterSolo.typeText(3, "password123");
//		RegisterSolo.getText("password123");
//		
//		RegisterSolo.clickOnEditText(4);
//		RegisterSolo.typeText(4, "password456");
//		RegisterSolo.getText("password456");
//	}
//}
