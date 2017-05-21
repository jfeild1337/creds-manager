package org.jfeild1337.credsmgr.db;

import org.jfeild1337.credsmgr.crypto.CryptoS;
import org.jfeild1337.credsmgr.crypto.CryptoUtils;
import org.jfeild1337.credsmgr.ui.helpers.CredsUtils;
import org.jfeild1337.credsmgr.ui.helpers.CredsUtils.DomainFields;
import org.jfeild1337.credsmgr.ui.helpers.CredsUtils.DomainFields.*;

/**
 * Holds all the info for a domain. Domain name, username, password,
 * salt, and secret key (master password in base 64 encrypted format).
 * Provides methods to update all fields and gives the SQL statements
 * to update the fields in the database.
 * 
 * You SET the domain name/username/password by passing in plain text Strings
 * to the SETTER functions. These will encrypt the strings using the salt and master key
 * provided in the constructor.
 * 
 * @author Julian
 *
 */
public class DomainEntity {

	private final String ALGORITHM = "PBEWithMD5AndDES";
	private final int ITERATION_COUNT = 16;
	
	private String mMasterKey; //base64 encoded master password
	private String mDomainName;
	private String mUsername;
	private String mPassword;
	private String mOtherInfo;
	private CryptoS mScrambler;
	private String mSaltStr; //NEED TO CONVERT TO BYTES USING THE CRYPTO  UTILS METHOD FOR DOING THIS
	private int mDomainID;
	private boolean isNew = false;
	private boolean isEdited = false;
		
	
	/**
	 * Constructor...parameters are straight from the database
	 * @param masterKey
	 * @param domainName
	 * @param username
	 * @param password
	 * @param saltStr
	 */
	public DomainEntity(String masterKey, String domainName, String username, String password, String otherInfo, String saltStr, int domainID)
	{
		mMasterKey = masterKey;
		mDomainName = domainName;
		mUsername = username;
		mPassword = password;
		mSaltStr = saltStr;
		mOtherInfo = otherInfo;
		mDomainID = domainID;
		setupScrambler();
	}
	
	/**
	 * Creates a DomainEntity with the specified  masterkey, and domain name. Puts placeholder
	 * values for the username/password/other info. Creates a new random salt.
	 * @param masterKey
	 * @param domainName
	 * @return
	 */
	public static DomainEntity createNewDomainEntityFromDomainName(String masterKey, String domainName)
	{
		byte[] salt = CryptoUtils.generateRandomSalt(CredsUtils.DEFAULT_SALT_LEN);
		String saltStr = CryptoUtils.byteArrayToString(salt);
		DomainEntity newEntity = new DomainEntity(masterKey, null, null, null, null, saltStr, -99);
		newEntity.setmDomainName(domainName);
		newEntity.setmUsername("USER");
		newEntity.setmPassword("PASSWORD");
		newEntity.setOtherInfo("OTHER INFO");
		newEntity.setIsNew(true);
		return newEntity;
	}
	
	/**
	 * Creates a new Domain Entity with the specified info. All info must be in plain text. It will be encrypted by this function.
	 * @param masterKey
	 * @param domainName
	 * @param username
	 * @param password
	 * @param otherInfo
	 * @return
	 */
	public static DomainEntity createNewDomainEntityFromComponents(String masterKey, String domainName, String username, String password, String otherInfo)
	{
		byte[] salt = CryptoUtils.generateRandomSalt(CredsUtils.DEFAULT_SALT_LEN);
		String saltStr = CryptoUtils.byteArrayToString(salt);
		DomainEntity newEntity = new DomainEntity(masterKey, null, null, null, null, saltStr, -99);
		newEntity.setmDomainName(domainName);
		newEntity.setmUsername(username);
		newEntity.setmPassword(password);
		newEntity.setOtherInfo(otherInfo);
		newEntity.setIsNew(true);
		return newEntity;
	}
	
	
	/**
	 * Initializes the Scrambler
	 */
	private void setupScrambler()
	{
		mScrambler = new CryptoS(mMasterKey, ALGORITHM, CryptoUtils.StringToByteArray(mSaltStr), ITERATION_COUNT);
	}	
	
	/**
	 * Sets a new master key, creates a new salt and re-encrypts all data accordingly
	 * @param masterKey
	 */
	public void setNewMasterKey(String masterKey)
	{
		String plainTextDomainName = getDecryptedDomainName();
		String plainTextUsername = getDecryptedUsername();
		String plainTextPassword = getDecryptedPassword();
		String plainTextOtherInfo = getDecryptedOtherInfo();
		
		//might as well generate a new salt too
		byte[] salt = CryptoUtils.generateRandomSalt(CredsUtils.DEFAULT_SALT_LEN);
		String saltStr = CryptoUtils.byteArrayToString(salt);
		
		//assign new salt and masterkey
		setSaltStr(saltStr);
		setmMasterKey(masterKey);
		
		//create a new Scrambler with the new master key and salt
		mScrambler = null;
		setupScrambler();
		
		//re-encrypt everything
		setmDomainName(plainTextDomainName);
		setmUsername(plainTextUsername);
		setmPassword(plainTextPassword);
		setOtherInfo(plainTextOtherInfo);
		
	}
	
	
	//=============================================================
	//Get decrypted username/password/domain name
	//=============================================================
	/**
	 * Returns plain-text username
	 * @return
	 */
	public String getDecryptedUsername()
	{
		return mScrambler.decrypt(mUsername);
	}
	public String getDecryptedPassword()
	{
		return mScrambler.decrypt(mPassword);
	}	
	/**
	 * Returns plain-text Domain name
	 * @return
	 */
	public String getDecryptedDomainName()
	{
		return mScrambler.decrypt(mDomainName);
	}
	/**
	 * Returns the plain-text "other info" data
	 * @return
	 */
	public String getDecryptedOtherInfo()
	{
		return mScrambler.decrypt(mOtherInfo);
	}
	
	//=============================================================
	//Standard getters & setters. GETTERS return encrypted values.
	//SETTERS take plain-text values and encrypt them before 
	//setting the corresponding fields
	//=============================================================
	/**
	 * returns base-64 encoded master password
	 * @return
	 */
	public String getmMasterKey() {
		return mMasterKey;
	}
	/**
	 * sets base-64 encoded master password	 
	 */
	public void setmMasterKey(String mMasterKey) {
		this.mMasterKey = mMasterKey;
	}
	/**
	 * Returns encrypted domain name
	 * @return
	 */
	public String getEncryptedDomainName() {
		return mDomainName;
	}
	/**
	 * @param mDomainName plain-text domainName. will be encrypted by this function
	 */
	public void setmDomainName(String domainName) {
		this.mDomainName = mScrambler.encrypt(validateString(domainName));
	}
	/**
	 * Returns encrypted user name
	 * @return
	 */
	public String getEncryptedUsername() {
		return mUsername;
	}
	/** 
	 * @param username plain-text username. will be encrypted by this function
	 */
	public void setmUsername(String username) {
		this.mUsername = mScrambler.encrypt(validateString(username));
	}
	/** 
	 * @return encrypted password
	 */
	public String getEncryptedPassword() {
		return mPassword;
	}
	/**
	 * @param mPassword plain text password. this method will encrypt it
	 */
	public void setmPassword(String password) {
		this.mPassword = mScrambler.encrypt(validateString(password));
	}
	/**
	 * @param otherInfo plain-text String to be set as "other info". This method will encrypt it
	 */
	public void setOtherInfo(String otherInfo)
	{
		this.mOtherInfo = mScrambler.encrypt(validateString(otherInfo));
	}
	/**
	 * @return encrypted "other info" data
	 */
	public String getEncryptedOtherInfo()
	{
		return mOtherInfo;
	}
	
	
	public CryptoS getmScrambler() {
		return mScrambler;
	}
	public void setmScrambler(CryptoS mScrambler) {
		this.mScrambler = mScrambler;
	}
	public String getSaltStr() {
		return mSaltStr;
	}
	public void setSaltStr(String saltStr) {
		this.mSaltStr = saltStr;
	}
	public int getDomainID() {
		return mDomainID;
	}
	public void setDomainID(int mDBID) {
		this.mDomainID = mDBID;
	}
	public boolean isNew() {
		return isNew;
	}
	public void setIsNew(boolean isNew) {
		this.isNew = isNew;
	}
	public boolean isEdited() {
		return isEdited;
	}
	public void setIsEdited(boolean isEdited) {
		//System.out.println("SET EDITED TO " + isEdited);
		this.isEdited = isEdited;
	}
	
	//=====================================================
	// SETTERS for fields that need encryption. 
	// provides error checking for NULL or EMPTY
	// values and substitutes placeholders in those cases.
	//=====================================================	
	/**
	 * Takes a String, and returns the String if it's both not null and not empty.
	 * If it is either null or empty, returns "DEFAULT_VALUE"
	 * @param str
	 * @return
	 */
	private String validateString(String str)
	{
		String newValue; 
		if(str == null)
		{
			newValue = "VALUE NOT SET";
		}
		else
		{
			if(str.trim().isEmpty())
			{
				newValue = "VALUE NOT SET";
			}
			else
			{
				newValue = str; 
			}
		}
		return newValue;
	}	
	
	/**
	 * Clone method, creates a deep copy of a domain entity
	 */
	public DomainEntity clone()
	{
		DomainEntity newEnt = new DomainEntity(this.mMasterKey, this.mDomainName, this.mUsername, 
				              this.mPassword, this.mOtherInfo, this.mSaltStr, this.mDomainID);
		return newEnt;
	}
	/**
	 * MAIN for testing
	 * @param args
	 */
	public static void main(String[] args) {
		String s = "";
		System.out.println("IS BLANK WHEN TRIMMED? : " + s.trim().equals(""));
		System.out.println("IS EMPTY? : " + s.trim().isEmpty());

	}

}
