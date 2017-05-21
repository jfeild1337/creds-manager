package org.jfeild1337.credsmgr.crypto;

/**
 * NOTE on SALTS: generate the salt with generateRandomSalt and use it.
 * Store in the DB by converting it to a string with byteArrayToString()
 * and then convert it back to a byte array with StringToByteArray()
 * --------------------------------------------------------------------
 */
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.apache.derby.impl.sql.compile.HasVariantValueNodeVisitor;

/**
 * Provides various helper methods for the encryption stuff
 * @author Julian
 *
 */
public class CryptoUtils {	

	private static String BYTE_ARR_DELIM = ",";
	
	public static String HASH_ENCODING = "UTF-16";
	public static String HASH_ALG = "SHA-1";

	/**
	 * Generates a random number in the range [min, max]
	 * @param min
	 * @param max
	 * @return
	 */
	public static int generateRandomInt(int min, int max)
	{
		return min + (int)(Math.random() * ((max - min) + 1));
	}
	
	/**
	 * Generates a random salt with the specified number of bytes
	 * @param length
	 * @return
	 */
	public static byte[] generateRandomSalt(int length)
	{
		final Random r = new SecureRandom();
		byte[] salt = new byte[length];
		r.nextBytes(salt);
		return salt;
	}
	
	/**
	 * Creates a random Salt and returns it in String format (exactly what will be stored in the
	 * database). Use this to generate the salt for a new Domain
	 * 
	 * @param length number of bytes for salt
	 * @return Byte array in comma-separated String format
	 */
	public static String generateRandomSaltString(int length)
	{		
		byte[] salt = generateRandomSalt(length);
		return byteArrayToString(salt);		
	}
	
	/**
	 * Takes an array of bytes and transforms it to a single comma-separated String.
	 * For example, if the byte array is [-125 0 255 -17] it will return -125,0,255,-17
	 * @param salt
	 * @return
	 */
	public static String byteArrayToString(byte[] salt)
	{
		String retVal = "";
		for(int i = 0; i < salt.length; i++)
		{
			retVal += Byte.toString(salt[i]);
			if(i < (salt.length - 1))
			{
				retVal += BYTE_ARR_DELIM;
			}
		}
		return retVal;
	}
	
	/**
	 * Takes a comma-separated String and puts the bye value of each element into a byte array.
	 * IE, "12,255,-125" --> [Byte.valueOf("12"), Byte.valueOf("255"), Byte.valueOf("-125")]
	 * @param saltStr
	 * @return
	 */
	public static byte[] StringToByteArray(String saltStr)
	{
		String[] octets = saltStr.split(BYTE_ARR_DELIM);
		if(octets.length == 0)
		{
			return null;
		}
		byte[] salt = new byte[octets.length];
		for(int i = 0; i< octets.length; i++)
		{
			salt[i] = Byte.valueOf(octets[i]);
		}
		return salt;
	}
	
	/**
	 * Performs a hash + base64 encoding of the specified string using SHA-1 and UTF-16
	 * @param stringToHash the string to hash
	 * @param salt salt
	 * @return
	 */
	public static String basicOneWayHash(String stringToHash, String salt)
	{
		MessageDigest msgDigest = null;
        String hashValue = null;
        try {
        	String[] saltArr = splitString(salt);
        	String saltedString = saltArr[0] + saltArr[1] + stringToHash + saltArr[0];
            msgDigest = MessageDigest.getInstance(HASH_ALG);
            msgDigest.update(saltedString.trim().getBytes(HASH_ENCODING));
            byte rawByte[] = msgDigest.digest();
            hashValue = Base64.encodeBase64String(rawByte);
 
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No Such Algorithm Exists");
        } catch (UnsupportedEncodingException e) {
            System.out.println("The Encoding Is Not Supported");
        }
        return hashValue;
	}
	
	/**
	 * Splits a String in half
	 * @param str
	 * @return
	 */
	private static String[] splitString(String str)
	{
		int midpoint = str.length() / 2;
		String firstHalf = str.substring(0, midpoint);
		String secondHalf = str.substring(midpoint);
		return new String[]{firstHalf, secondHalf};
	}
	/**
	 * MAIN for testing
	 * @param args
	 */
	public static void main(String[] args)
	{
		byte[] salt = generateRandomSalt(16);
		System.out.println("BYTES: ");
		for(byte b:salt)
		{
			System.out.print(b);
			System.out.print(" ");
		}
		System.out.println();
		
		String saltStr = byteArrayToString(salt);
		System.out.println("CONVERTED TO STRING:");
		System.out.println(saltStr);
		
		byte[] salt2 = StringToByteArray(saltStr);
		System.out.println("CONVERTED BACK TO BYTES:");
		for(byte b:salt2)
		{
			System.out.print(b);
			System.out.print(" ");
		}
		System.out.println();
	}
}
