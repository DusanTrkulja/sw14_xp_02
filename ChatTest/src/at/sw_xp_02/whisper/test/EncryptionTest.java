package at.sw_xp_02.whisper.test;

import java.util.Random;

import android.test.ActivityInstrumentationTestCase2;
import at.sw_xp_02.whisper.Encryption;
import at.sw_xp_02.whisper.MainActivity;
import at.sw_xp_02.whisper.client.Constants;

public class EncryptionTest extends
ActivityInstrumentationTestCase2<MainActivity> {


	public EncryptionTest() {
		super(MainActivity.class);
	}
	
	public static String[] generateRandomWords(int numberOfWords)
	{
	    String[] randomStrings = new String[numberOfWords];
	    Random random = new Random();
	    for(int i = 0; i < numberOfWords; i++)
	    {
	        char[] word = new char[random.nextInt(8)+3]; // words of length 3 through 10. (1 and 2 letter words are boring.)
	        for(int j = 0; j < word.length; j++)
	        {
	            word[j] = (char)('a' + random.nextInt(26));
	        }
	        randomStrings[i] = new String(word);
	    }
	    return randomStrings;
	}

	public void setUp() throws Exception {
		}

	public void testEncryptionTest() {
		String[] randomWords = generateRandomWords(1000);
		Encryption encryption = new Encryption();
		for(String word:randomWords) {
			assertEquals(word, encryption.decrypt(Constants.ENCRYPT_KEY,encryption.encrypt(Constants.ENCRYPT_KEY, word)));
		}
	}

	@Override
	public void tearDown() throws Exception {
		
	}
}
