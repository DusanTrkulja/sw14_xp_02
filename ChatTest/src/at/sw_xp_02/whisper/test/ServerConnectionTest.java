package at.sw_xp_02.whisper.test;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.test.ActivityInstrumentationTestCase2;
import at.sw_xp_02.whisper.MainActivity;
import at.sw_xp_02.whisper.client.Constants;

public class ServerConnectionTest extends
ActivityInstrumentationTestCase2<MainActivity> {


	public ServerConnectionTest() {
		super(MainActivity.class);
	}
	

	public void setUp() throws Exception {
		}

	public void testServerConnection() throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient();
	    HttpResponse response = httpclient.execute(new HttpGet(Constants.SERVER_URL));
	    StatusLine statusLine = response.getStatusLine();
	    assertEquals(statusLine.getStatusCode(),HttpStatus.SC_OK);
	}

	@Override
	public void tearDown() throws Exception {
		
	}
}
