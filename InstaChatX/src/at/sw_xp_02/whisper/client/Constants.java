package at.sw_xp_02.whisper.client;

import com.facebook.crypto.Entity;

public interface Constants {

    /**
     * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
     */

	String SERVER_URL = "http://1-dot-gaechatapp.appspot.com";

	/**
     * Google API project id registered to use GCM.
     */
	String SENDER_ID = "905468364069";
	
	String STAY_ON_MAINSCREEN = "stayOnMainScreen";

	Entity ENCRYPTION_ENTITY = new Entity("whisperCrypt");
}
