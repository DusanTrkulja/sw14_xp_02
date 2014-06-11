package at.sw_xp_02.whisper.otr;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Date;
import java.util.Hashtable;

import net.java.otr4j.OtrEngineHost;
import net.java.otr4j.OtrKeyManager;
import net.java.otr4j.OtrPolicy;
import net.java.otr4j.session.SessionID;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/*
 * OtrEngineHostImpl is the connects this app and the OtrEngine
 * http://code.google.com/p/otr4j/wiki/QuickStart
 */
public class OtrEngineHostImpl implements OtrEngineHost {
	
		private static final String LOG_TAG = "OtrEngineHostImpl";

    private OtrPolicy mPolicy;

    private OtrKeyManager mOtrKeyManager;
//TODO Context und Service ? 
//    private ImService mContext;

    private Hashtable<SessionID, String> mSessionResources;
    
//    private RemoteImService mImService;
    
    public OtrEngineHostImpl(OtrPolicy policy, OtrKeyManager otrKeyManager) throws IOException {
        mPolicy = policy;
//        mContext = context;

        mSessionResources = new Hashtable<SessionID, String>();

        mOtrKeyManager = otrKeyManager;
        
//        mImService = imService;


    }

    public void putSessionResource(SessionID session, String resource) {
        mSessionResources.put(session, resource);
    }

    public void removeSessionResource(SessionID session) {
        mSessionResources.remove(session);
    }

    public String appendSessionResource(SessionID session, String to) {
        String resource = mSessionResources.get(session);
        if (resource != null)
            return (to + '/' + resource);
        else
            return to;
    }

//    public ImConnectionAdapter findConnection(SessionID session) {
//        return mImService.getConnection(Address.stripResource(session.getLocalUserId()));
//    }

    public OtrKeyManager getKeyManager() {
        return mOtrKeyManager;
    }

    public void storeRemoteKey(SessionID sessionID, PublicKey remoteKey) {
        mOtrKeyManager.savePublicKey(sessionID, remoteKey);
    }

    public boolean isRemoteKeyVerified(SessionID sessionID) {
        return mOtrKeyManager.isVerified(sessionID);
    }

    public String getLocalKeyFingerprint(SessionID sessionID) {
        return mOtrKeyManager.getLocalFingerprint(sessionID);
    }

    public String getRemoteKeyFingerprint(SessionID sessionID) {
        return mOtrKeyManager.getRemoteFingerprint(sessionID);
    }

    public KeyPair getKeyPair(SessionID sessionID) {
        KeyPair kp = null;
        kp = mOtrKeyManager.loadLocalKeyPair(sessionID);

        if (kp == null) {
            mOtrKeyManager.generateLocalKeyPair(sessionID);
            kp = mOtrKeyManager.loadLocalKeyPair(sessionID);
        }
        return kp;
    }

    public OtrPolicy getSessionPolicy(SessionID sessionID) {
        return mPolicy;
    }

    public void setSessionPolicy(OtrPolicy policy) {
        mPolicy = policy;
    }

    private void sendMessage(SessionID sessionID, String body) {
        ImConnectionAdapter connection = findConnection(sessionID);
        if (connection != null)
        {
            ChatSessionManagerAdapter chatSessionManagerAdapter = (ChatSessionManagerAdapter) connection
                    .getChatSessionManager();
            ChatSessionAdapter chatSessionAdapter = (ChatSessionAdapter) chatSessionManagerAdapter
                    .getChatSession(Address.stripResource(sessionID.getRemoteUserId()));
            
            if (chatSessionAdapter != null)
            {
                
                Message msg = new Message(body);
                
                msg.setFrom(connection.getLoginUser().getAddress());
                final Address to = chatSessionAdapter.getAdaptee().getParticipant().getAddress();
                msg.setTo(appendSessionResource(sessionID, to));
                //msg.setTo(to);
                msg.setDateTime(new Date());
                
                // msg ID is set by plugin
                // msg.setID(msg.getFrom().getBareAddress() + ":" + msg.getDateTime().getTime());
                
                chatSessionManagerAdapter.getChatSessionManager().sendMessageAsync(chatSessionAdapter.getAdaptee(), msg);
            }
            else
            {
                Log.e(LOG_TAG, sessionID.toString() + ": could not find chatSession");
                
            }
        }   
        else
        {
            Log.e(LOG_TAG,sessionID.toString() + ": could not find ImConnection");
            
        }
    }

    public void injectMessage(SessionID sessionID, String text) {
      	Log.d(LOG_TAG,sessionID.toString() + ": injecting message: " + text);

        sendMessage(sessionID, text);
    }

    public void showError(SessionID sessionID, String error) {
        Log.d(LOG_TAG,sessionID.toString() + ": ERROR=" + error);
//
//        if (mImService != null)
//            mImService.showToast("Encryption Error: " + error,Toast.LENGTH_SHORT);
//        
        
    }

    public void showWarning(SessionID sessionID, String warning) {
        Log.d(LOG_TAG, sessionID.toString() + ": WARNING=" + warning);
       
//        if (mImService != null)
//            mImService.showToast("Encryption Warning: " + warning,Toast.LENGTH_SHORT);
//        
    }

    
}
