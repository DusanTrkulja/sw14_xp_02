package at.sw_xp_02.whisper.otr;

import info.guardianproject.bouncycastle.util.encoders.Hex;
import android.util.Log;
import at.sw_xp_02.whisper.DataProvider;
import at.sw_xp_02.whisper.Encryption;

//import info.guardianproject.otr.app.im.engine.Address;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;
import java.util.Vector;

import net.java.otr4j.OtrKeyManager;
import net.java.otr4j.OtrKeyManagerListener;
import net.java.otr4j.crypto.OtrCryptoEngineImpl;
import net.java.otr4j.crypto.OtrCryptoException;
import net.java.otr4j.session.SessionID;



import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;


public class OtrAndroidKeyManagerImpl extends Object implements OtrKeyManager {

    private static final boolean REGENERATE_LOCAL_PUBLIC_KEY = false;

    private OtrCryptoEngineImpl cryptoEngine;

    private final static String KEY_ALG = "DSA";
    private final static int KEY_SIZE = 1024;  

    private static final String LOG_TAG = "KeyManagerImpl";
    
    private static Encryption encrypt = new Encryption();
    
    private static Activity activity;
    

    private OtrAndroidKeyManagerImpl(Activity act2) throws IOException {
    		activity = act2;
        cryptoEngine = new OtrCryptoEngineImpl();
        
    }

    private List<OtrKeyManagerListener> listeners = new Vector<OtrKeyManagerListener>();

    public void addListener(OtrKeyManagerListener l) {
        synchronized (listeners) {
            if (!listeners.contains(l))
                listeners.add(l);
        }
    }

    public void removeListener(OtrKeyManagerListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    public void generateLocalKeyPair(SessionID sessionID) {
        if (sessionID == null)
            return;

        generateLocalKeyPair(sessionID.getLocalUserId());
    }

    public void regenerateLocalPublicKey(KeyFactory factory, String foreignAddress, DSAPrivateKey privKey) {
                
        BigInteger x = privKey.getX();
        DSAParams params = privKey.getParams();
        BigInteger y = params.getG().modPow(x, params.getP());
        DSAPublicKeySpec keySpec = new DSAPublicKeySpec(y, params.getP(), params.getQ(), params.getG());
        PublicKey pubKey;
        try {
            pubKey = factory.generatePublic(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
        storeLocalPublicKey(foreignAddress, pubKey);
    }
    
    public void generateLocalKeyPair(String foreignAddress) {

        Log.d(LOG_TAG, "generating local key pair for: " + foreignAddress);

        KeyPair keyPair;
        try {

            KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEY_ALG);
            kpg.initialize(KEY_SIZE);

            keyPair = kpg.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            Log.e(LOG_TAG,"no such algorithm", e);
            return;
        }

        Log.d(LOG_TAG,"SUCCESS! generating local key pair for: " + foreignAddress);

        // Store Private Key.
        PrivateKey privKey = keyPair.getPrivate();
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privKey.getEncoded());

        ContentValues values = new ContentValues(2);
				values.put(DataProvider.COL_FOREIGN_EMAIL, foreignAddress);
				values.put(DataProvider.COL_LOCAL_PRIVATE_KEY, pkcs8EncodedKeySpec.getEncoded());
				activity.getContentResolver().insert(DataProvider.CONTENT_URI_KEYS, values);

        // Store Public Key.
        PublicKey pubKey = keyPair.getPublic();

        storeLocalPublicKey(foreignAddress, pubKey);
        
        
    }

    private void storeLocalPublicKey(String foreignAddress, PublicKey pubKey) {
        
  		ContentValues values = new ContentValues(2);
			
      X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(pubKey.getEncoded());
      values.put(DataProvider.COL_LOCAL_PUBLIC_KEY, x509EncodedKeySpec.getEncoded());

      // Stash fingerprint for consistency.
      String fingerprintString;
			try {
				fingerprintString = new OtrCryptoEngineImpl().getFingerprint(pubKey);
				values.put(DataProvider.COL_LOCAL_FINGERPRINT, Hex.decode(fingerprintString));
			} catch (OtrCryptoException e) {
				Log.e(LOG_TAG, "Fingerprint not saved");
			}

			activity.getContentResolver().update(DataProvider.CONTENT_URI_KEYS, values, DataProvider.COL_FOREIGN_EMAIL + " = ?", new String[] {foreignAddress});
    }
    
    public String getLocalFingerprint(SessionID sessionID) {
        return getLocalFingerprint(sessionID.getLocalUserId());
    }

    public String getLocalFingerprint(String foreignAddress) {

        KeyPair keyPair = loadLocalKeyPair(foreignAddress);

        if (keyPair == null)
            return null;

        PublicKey pubKey = keyPair.getPublic();

        try {
            String fingerprint = cryptoEngine.getFingerprint(pubKey);

            Log.d(LOG_TAG,"got fingerprint for: " + foreignAddress + "=" + fingerprint);

            return fingerprint;

        } catch (OtrCryptoException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getRemoteFingerprint(SessionID sessionID) {
        return getRemoteFingerprint(sessionID.getRemoteUserId());
    }

    public String getRemoteFingerprint(String foreignAddress) {

        //if (!Address.hasResource(fullUserId))
          //  return null;
    		Cursor c = activity.getContentResolver().query(DataProvider.CONTENT_URI_KEYS, null, DataProvider.COL_FOREIGN_EMAIL + " = ?", new String[]{foreignAddress}, null);
    		byte[] fingerprint = c.getString(c.getColumnIndex(DataProvider.COL_REMOTE_FINGERPRINT)).getBytes();
        if (fingerprint != null) {
            // If we have a fingerprint stashed, assume it is correct.
            return new String(Hex.encode(fingerprint, 0, fingerprint.length));
        }
        
        PublicKey remotePublicKey = loadRemotePublicKeyFromStore(foreignAddress);
        if (remotePublicKey == null)
            return null;
        try {
            // Store the fingerprint, for posterity.
        		ContentValues values = new ContentValues(1);
            String fingerprintString = new OtrCryptoEngineImpl().getFingerprint(remotePublicKey);
            values.put(DataProvider.COL_REMOTE_FINGERPRINT, Hex.decode(fingerprintString));
            activity.getContentResolver().update(DataProvider.CONTENT_URI_KEYS, values, DataProvider.COL_FOREIGN_EMAIL + " = ?", new String[]{foreignAddress});
            return fingerprintString;
        } catch (OtrCryptoException e) {
            Log.d(LOG_TAG,"OtrCryptoException getting remote fingerprint");
            return null;
        }
    }

//    public String[] getRemoteFingerprints(String userId) {
//
//        Enumeration<Object> keys = store.getKeys();
//        
//        ArrayList<String> results = new ArrayList<String>();
//        
//        while (keys.hasMoreElements())
//        {
//            String key = (String)keys.nextElement();
//            
//            if (key.startsWith(userId + '/') && key.endsWith(".fingerprint"))
//            {
//            
//                byte[] fingerprint = this.store.getPropertyHexBytes(userId + ".fingerprint");
//                if (fingerprint != null) {
//                    // If we have a fingerprint stashed, assume it is correct.
//                    results.add(new String(Hex.encode(fingerprint, 0, fingerprint.length)));
//                }
//                
//            }
//             
//        }
//        
//        String[] resultsString = new String[results.size()];
//        return results.toArray(resultsString);
//    }
    
    public boolean isVerified(SessionID sessionID) {
        if (sessionID == null)
            return false;
        
        String remoteFingerprint =getRemoteFingerprint(sessionID.getRemoteUserId());
        
        if (remoteFingerprint != null)
        {
            String username = sessionID.getRemoteUserId();
        		Cursor c = activity.getContentResolver().query(DataProvider.CONTENT_URI_KEYS, null, DataProvider.COL_FOREIGN_EMAIL + " = ?", new String[]{username}, null);
        		int fingerprint = c.getInt(c.getColumnIndex(DataProvider.COL_VERIFIED));
        		if(fingerprint != 0)
        			return true;
        		return false;
        }
        else
        {
            return false;
        }
    }

    public boolean isVerifiedUser(String foreignAddress) {        

        String remoteFingerprint = getRemoteFingerprint(foreignAddress);
        
        if (remoteFingerprint != null)
        {
        		Cursor c = activity.getContentResolver().query(DataProvider.CONTENT_URI_KEYS, null, DataProvider.COL_FOREIGN_EMAIL + " = ?", new String[]{foreignAddress}, null);
        		int fingerprint = c.getInt(c.getColumnIndex(DataProvider.COL_VERIFIED));
        		if(fingerprint != 0)
        			return true;
        		return false;
        }
        else
            return false;
    }

    public KeyPair loadLocalKeyPair(SessionID sessionID) {
        if (sessionID == null)
            return null;

        return loadLocalKeyPair(sessionID.getLocalUserId());
    }

    private KeyPair loadLocalKeyPair(String foreignAddress) {
        PublicKey publicKey;
        PrivateKey privateKey;


        try {
            // Load Private Key.
        		
        		Cursor c = activity.getContentResolver().query(DataProvider.CONTENT_URI_KEYS, null, DataProvider.COL_FOREIGN_EMAIL + " = ?", new String[]{foreignAddress}, null);
        		byte[] b64PrivKey = c.getString(c.getColumnIndex(DataProvider.COL_LOCAL_PRIVATE_KEY)).getBytes();
            if (b64PrivKey == null)
                return null;

            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(b64PrivKey);

            // Generate KeyPair.
            KeyFactory keyFactory;
            keyFactory = KeyFactory.getInstance(KEY_ALG);
            privateKey = keyFactory.generatePrivate(privateKeySpec);

            if (REGENERATE_LOCAL_PUBLIC_KEY) {
                regenerateLocalPublicKey(keyFactory, foreignAddress, (DSAPrivateKey)privateKey);
            }
            
            // Load Public Key.
            Cursor c2 = activity.getContentResolver().query(DataProvider.CONTENT_URI_KEYS, null, DataProvider.COL_FOREIGN_EMAIL + " = ?", new String[]{foreignAddress}, null);
        		byte[] b64PubKey = c2.getString(c2.getColumnIndex(DataProvider.COL_LOCAL_PUBLIC_KEY)).getBytes();

            if (b64PubKey == null)
                return null;

            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(b64PubKey);
            publicKey = keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }

        return new KeyPair(publicKey, privateKey);
    }

    public PublicKey loadRemotePublicKey(SessionID sessionID) {

        return loadRemotePublicKeyFromStore(sessionID.getRemoteUserId());
    }

    private PublicKey loadRemotePublicKeyFromStore(String foreignAddress) {

      //  if (!Address.hasResource(fullUserId))
        //    return null;
        
    	Cursor c = activity.getContentResolver().query(DataProvider.CONTENT_URI_KEYS, null, DataProvider.COL_FOREIGN_EMAIL, new String[] {foreignAddress}, null);	
    	byte[] b64PubKey = c.getString(c.getColumnIndex(DataProvider.COL_REMOTE_KEY)).getBytes();
      if (b64PubKey == null) {
          return null;

      }

      X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(b64PubKey);

      // Generate KeyPair from spec
      KeyFactory keyFactory;
      try {
          keyFactory = KeyFactory.getInstance(KEY_ALG);

          return keyFactory.generatePublic(publicKeySpec);
      } catch (NoSuchAlgorithmException e) {
          e.printStackTrace();
          return null;
      } catch (InvalidKeySpecException e) {
          e.printStackTrace();
          return null;
      }
    }

    public void savePublicKey(SessionID sessionID, PublicKey pubKey) {
        if (sessionID == null)
            return;

        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(pubKey.getEncoded());

      //  if (!Address.hasResource(fullUserId))
        //    return;
        ContentValues values = new ContentValues(2);
        values.put(DataProvider.COL_REMOTE_KEY, x509EncodedKeySpec.getEncoded());
        
        // Stash the associated fingerprint.  This saves calculating it in the future
        // and is useful for transferring rosters to other apps.
        try {
            String fingerprintString = new OtrCryptoEngineImpl().getFingerprint(pubKey);
//            String verifiedToken = buildPublicKeyVerifiedId(sessionID.getRemoteUserId(), fingerprintString.toLowerCase());
//TODO        if (!this.store.hasProperty(verifiedToken))
//                this.store.setProperty(verifiedToken, false);
            values.put(DataProvider.COL_REMOTE_FINGERPRINT, Hex.decode(fingerprintString));
        } catch (OtrCryptoException e) {
            e.printStackTrace();
        }
        activity.getContentResolver().update(DataProvider.CONTENT_URI_KEYS, values, DataProvider.COL_FOREIGN_EMAIL, new String[] {sessionID.getRemoteUserId()});
    }

    public void unverify(SessionID sessionID) {
        if (sessionID == null)
            return;

        if (!isVerified(sessionID))
            return;

        unverifyUser(sessionID.getRemoteUserId());
        
        for (OtrKeyManagerListener l : listeners)
            l.verificationStatusChanged(sessionID);

    }

    public void unverifyUser(String foreignAddress) {
        
        if (!isVerifiedUser(foreignAddress))
            return;

       ContentValues values = new ContentValues(1);
       values.put(DataProvider.COL_VERIFIED, false);
       activity.getContentResolver().update(DataProvider.CONTENT_URI_KEYS, values, DataProvider.COL_FOREIGN_EMAIL + " = ?", new String[] {foreignAddress});        

    }

    public void verify(SessionID sessionID) {
        if (sessionID == null)
            return;

        if (this.isVerified(sessionID))
            return;
        
        verifyUser(sessionID.getRemoteUserId());

    }

    public void remoteVerifiedUs(SessionID sessionID) {
        if (sessionID == null)
            return;

        for (OtrKeyManagerListener l : listeners)
            l.remoteVerifiedUs(sessionID);
    }

//    private static String buildPublicKeyVerifiedId(String foreignAddress, String fingerprint) {
//        if (fingerprint == null)
//            return null;
//        return foreignAddress + "." + fingerprint + ".publicKey.verified";
//    }

    public void verifyUser(String foreignAddress) {
        if (foreignAddress == null)
            return;

        if (this.isVerifiedUser(foreignAddress))
            return;
        
        ContentValues values = new ContentValues(1);
        values.put(DataProvider.COL_VERIFIED, true);
        activity.getContentResolver().update(DataProvider.CONTENT_URI_KEYS, values, DataProvider.COL_FOREIGN_EMAIL + " = ?", new String[] {foreignAddress});
        //for (OtrKeyManagerListener l : listeners)
        //l.verificationStatusChanged(userId);

    }  
}
