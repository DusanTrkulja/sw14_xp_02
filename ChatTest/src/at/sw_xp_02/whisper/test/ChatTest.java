package at.sw_xp_02.whisper.test;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import at.sw_xp_02.whisper.ChatActivity;
import at.sw_xp_02.whisper.R;

public class ChatTest extends
    android.test.ActivityUnitTestCase<ChatActivity> {

  private ChatActivity activity;
  private EditText msgEdit;
  private Button sendBtn;

  public ChatTest() {
    super(ChatActivity.class);
  }
  
  public void testSendButton() {
	  
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    Intent intent = new Intent(getInstrumentation().getTargetContext(),
    		ChatActivity.class);
    startActivity(intent, null, null);
    activity = getActivity();
	msgEdit = (EditText) activity.findViewById(R.id.msg_edit);
	sendBtn = (Button) activity.findViewById(R.id.send_btn);
  }

} 