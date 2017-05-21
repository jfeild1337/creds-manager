package org.jfeild1337.credsmgr.crypto;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;

import java.security.spec.KeySpec;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEParameterSpec;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;



public class CryptoS {

	 private Cipher ecipher;
	 private Cipher dcipher;	 
	
	 /**
	  * Constructor
	  * @param key
	  * @param algorithm
	  */
	 public CryptoS(SecretKey key, String algorithm) {
	     try {
	         ecipher = Cipher.getInstance(algorithm);
	         dcipher = Cipher.getInstance(algorithm);
	         ecipher.init(Cipher.ENCRYPT_MODE, key);
	         dcipher.init(Cipher.DECRYPT_MODE, key);	         
	     } catch (NoSuchPaddingException e) {
	         System.out.println("EXCEPTION: NoSuchPaddingException");
	     } catch (NoSuchAlgorithmException e) {
	         System.out.println("EXCEPTION: NoSuchAlgorithmException");
	     } catch (InvalidKeyException e) {
	         System.out.println("EXCEPTION: InvalidKeyException");
	     }
	 }
	 
	 /**
	  * Constructor
	  * @param key
	  * @param algorithm
	  */
	 public CryptoS(String passPhrase, String algorithm, byte[] salt, int iterationCount) {
	     try {
	    	 KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
	         SecretKey key = SecretKeyFactory.getInstance(algorithm).generateSecret(keySpec);
	
	         ecipher = Cipher.getInstance(key.getAlgorithm());
	         dcipher = Cipher.getInstance(key.getAlgorithm());
	         
	         AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
	
	         ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
	         dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
	
	     } catch (NoSuchPaddingException e) {
	         System.out.println("EXCEPTION: NoSuchPaddingException");
	     } catch (NoSuchAlgorithmException e) {
	         System.out.println("EXCEPTION: NoSuchAlgorithmException");
	     } catch (InvalidKeyException e) {
	         System.out.println("EXCEPTION: InvalidKeyException");
	     } catch (InvalidKeySpecException e) {
	    	 System.out.println("EXCEPTION: InvalidKeySpecException");
		} catch (InvalidAlgorithmParameterException e) {
			System.out.println("EXCEPTION: InvalidArgumentException - " + e.getMessage());
		}
	 }
	
	
	 /**
	  * Constructor
	  * @param passPhrase
	  */
	public  CryptoS(String passPhrase) {
	
	     // Iteration count
	     int iterationCount = 21;
	     byte[] salt = {
		         (byte)0x20, (byte)0x9B, (byte)0x68, (byte)0x50,
		         (byte)0x56, (byte)0x55, (byte)0xE3, (byte)0x03
		     };
	     try {
	
	         KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
	         SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
	
	         ecipher = Cipher.getInstance(key.getAlgorithm());
	         dcipher = Cipher.getInstance(key.getAlgorithm());
	         
	         AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
	
	         ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
	         dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
	
	     } catch (InvalidAlgorithmParameterException e) {
	         System.out.println("EXCEPTION: InvalidAlgorithmParameterException");
	     } catch (InvalidKeySpecException e) {
	         System.out.println("EXCEPTION: InvalidKeySpecException");
	     } catch (NoSuchPaddingException e) {
	         System.out.println("EXCEPTION: NoSuchPaddingException");
	     } catch (NoSuchAlgorithmException e) {
	         System.out.println("EXCEPTION: NoSuchAlgorithmException");
	     } catch (InvalidKeyException e) {
	         System.out.println("EXCEPTION: InvalidKeyException");
	     }
	 }
	
	
	 /**
	  * Takes a single String as an argument and returns an Encrypted version
	  * of that String.
	  * @param str String to be encrypted
	  * @return <code>String</code> Encrypted version of the provided String
	  */
	 public String encrypt(String str) {
	     try {
	         // Encode the string into bytes using utf-8
	         byte[] utf8 = str.getBytes("UTF8");
	
	         // Encrypt
	         byte[] enc = ecipher.doFinal(utf8);
	
	         // Encode bytes to base64 to get a string
	         return Base64.encodeBase64String(enc);  //.encode(enc);
	
	     } catch (BadPaddingException e) 
	     {
	    	 System.out.println("ERROR - BadPaddingException @ Scrambler.encrypt()");
	     } catch (IllegalBlockSizeException e) 
	     {
	    	 System.out.println("ERROR - IllegalBlockSizeException @ Scrambler.encrypt()");
	     } catch (UnsupportedEncodingException e) 
	     {
	    	 System.out.println("ERROR - UnsupportedEncodingException @ Scrambler.encrypt()");
	     } catch (IOException e) 
	     {
	    	 System.out.println("ERROR - IOException @ Scrambler.encrypt()");
	     }
	     return null;
	 }
	
	
	 /**
	  * Takes a encrypted String as an argument, decrypts and returns the 
	  * decrypted String.
	  * @param str Encrypted String to be decrypted
	  * @return <code>String</code> Decrypted version of the provided String
	  */
	 public String decrypt(String str) {
	
	     try {
	
	         // Decode base64 to get bytes
	         byte[] dec = Base64.decodeBase64(str);
	
	         // Decrypt
	         byte[] utf8 = dcipher.doFinal(dec);
	
	         // Decode using utf-8
	         return new String(utf8, "UTF8");
	
	     } catch (BadPaddingException e) {
	     } catch (IllegalBlockSizeException e) {
	     } catch (UnsupportedEncodingException e) {
	     } catch (IOException e) {
	     }
	     return null;
	 }
	 
	 /**
	  * Main for testing
	  * 
	  * @param arrgs
	  */
	 public static void main(String[] arrgs){
		 String s = "255";
		 byte[] bytes = s.getBytes();
		 System.out.println();
		 
		 for(int i = 0; i < 512; i++)
		 {
			 System.out.println(i % 4);
		 }
	 }
 
}

