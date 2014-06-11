package at.sw_xp_02.whisper.otr;


import net.java.otr4j.OtrException;
import net.java.otr4j.session.SessionID;
import net.java.otr4j.session.SessionStatus;
import android.os.RemoteException;
import android.util.Log;

public class OtrChatSessionAdapter {

    private OtrChatManager _chatManager;
    private String _localUser;
    private String _remoteUser;
    private SessionID _sessionId;
    private static String LOG_TAG = "OtrChatSessionAdapter";
    
    public OtrChatSessionAdapter(String localUser, String remoteUser, OtrChatManager chatManager) {

        _chatManager = chatManager;
        _localUser = localUser;
        _remoteUser = remoteUser;

        _sessionId = chatManager.getSessionId(localUser, remoteUser);
    }

    public void startChatEncryption() throws RemoteException {
      _chatManager.startSession(_localUser, _remoteUser);
    }
    
    public void stopChatEncryption() throws RemoteException {
      _chatManager.endSession(_localUser, _remoteUser);
    }

    public boolean isChatEncrypted() throws RemoteException {

        return _chatManager.getSessionStatus(_localUser, _remoteUser) == SessionStatus.ENCRYPTED;

    }



    public int getChatStatus() throws RemoteException {
        SessionStatus sessionStatus = _chatManager.getSessionStatus(_localUser, _remoteUser);
        if (sessionStatus == null)
            sessionStatus = SessionStatus.PLAINTEXT;
        return sessionStatus.ordinal();
    }


    public void initSmpVerification(String question, String secret) throws RemoteException {

        try {
            _chatManager.initSmp(_chatManager.getSessionId(_localUser, _remoteUser), question,
                    secret);
        } catch (OtrException e) {
            Log.e(LOG_TAG, "initSmp");
            throw new RemoteException();
        }
    }


    public void respondSmpVerification(String answer) throws RemoteException {

        try {
            _chatManager.respondSmp(_chatManager.getSessionId(_localUser, _remoteUser), answer);
            
        } catch (OtrException e) {
            Log.e(LOG_TAG, "respondSmp");
            throw new RemoteException();
        }
    }
    

    public void verifyKey(String address) throws RemoteException {
        
        SessionID sessionId = _chatManager.getSessionId(_localUser, address);
        _chatManager.getKeyManager().verify(sessionId);
        
    }

    public void unverifyKey(String address) throws RemoteException {
        SessionID sessionId = _chatManager.getSessionId(_localUser, address);
        _chatManager.getKeyManager().unverify(sessionId);
    }


    public boolean isKeyVerified(String address) throws RemoteException {
        SessionID sessionId = _chatManager.getSessionId(_localUser, address);
        return _chatManager.getKeyManager().isVerified(sessionId);
    }


    public String getLocalFingerprint() throws RemoteException {
        return _chatManager.getKeyManager().getLocalFingerprint(_sessionId);
        
    }


    public String getRemoteFingerprint() throws RemoteException {
        SessionID sessionId = _chatManager.getSessionId(_localUser, _remoteUser);

        return _chatManager.getKeyManager().getRemoteFingerprint(sessionId);
    }


    public void generateLocalKeyPair() throws RemoteException {
        _chatManager.getKeyManager().generateLocalKeyPair(_sessionId);
    }

}
