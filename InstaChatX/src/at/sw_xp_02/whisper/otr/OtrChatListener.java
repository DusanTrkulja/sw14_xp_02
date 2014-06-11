package at.sw_xp_02.whisper.otr;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import net.java.otr4j.OtrException;
import net.java.otr4j.session.SessionStatus;
import net.java.otr4j.session.TLV;

public class OtrChatListener {
// implements MessageListener

    public static final String LOG_TAG = "OtrChatListener";
    private OtrChatManager mOtrChatManager;


    public OtrChatListener(OtrChatManager otrChatManager, String listener) {
        this.mOtrChatManager = otrChatManager;
    }

    
    public boolean onIncomingMessage(String msg, String from) {

    	//TODO Do whatever is todo on which status is made
        return true;
    }
    
}
