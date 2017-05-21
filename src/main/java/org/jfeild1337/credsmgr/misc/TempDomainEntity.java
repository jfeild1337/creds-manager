package org.jfeild1337.credsmgr.misc;

/**
 * Represents a DomainEntity but in plain-text format. Used for exporting DomainEntities to plain text for password change,
 * or for storing domain info when performing bulk imports of domain data from a text file
 * @author Julian
 *
 */
public class TempDomainEntity {
	
	private String mDomainName;
	private String mUsername;
	private String mPassword;
	private String mOtherInfo;
	
	public TempDomainEntity(String domainName, String username, String password, String otherInfo)
	{
		mDomainName = domainName;
		mUsername = username;
		mPassword = password;
		mOtherInfo = otherInfo;
	}
	
	public String getDomainName()
	{
		return mDomainName;
	}
	public String getUserName()
	{
		return mUsername;
	}
	public String getPassword()
	{
		return mPassword;
	}
	public String getOtherInfo()
	{
		return mOtherInfo;
	}
	
}
