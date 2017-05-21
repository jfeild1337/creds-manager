package org.jfeild1337.credsmgr.db;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.jfeild1337.credsmgr.crypto.CryptoUtils;
import org.jfeild1337.credsmgr.db.DBManager;
import org.jfeild1337.credsmgr.ui.helpers.CredsUtils.DomainFields;

/**
 * Handles the SQL and other messy database connection stuff.
 * Contains one connection to the database as member variable.
 * 
 * @author Julian
 *
 */
public class CredentialsDBManager {

	public static final String DB_DIR = "datastore";
	public static final String DB_NAME = "credsdb";
	private final static String DB_FULL_PATH = DB_DIR + "/" + DB_NAME;	
	private final int MASTERKEY_ID = 1337;
	
	private DBManager dbManager;	
	private String dbName;	
	private Connection mDBConn;
	private boolean isConnectedToDB = false;
	
	private String STMT_CREATE_DOMAIN_TBL;
	private String STMT_CREATE_DATA_TBL;
	private String STMT_CREATE_MASTERKEY;	
	private String SELECT_ALL_DOMAIN_ENTITIES;
	private String UPDATE_DOMAIN;
	private String UPDATE_DOMAIN_NOSALT;
	private String UPDATE_DOMAIN_DATA;
	private String INSERT_DOMAIN;
	private String INSERT_DATA;
	private String INSERT_MASTERKEY;
	private String SELECT_MASTERKEY;
	private String SELECT_DOMAIN_ENTITY_BY_DOMAIN_NAME;
	private String DELETE_DOMAIN;
	private String UPDATE_MASTERKEY;
	
	/**
	 * Creates the INSERT/UPDATE/DELETE statements.
	 * Because the database name is not set until the constructor
	 * is called, this needs to be in a method (not just at the top of the file)
	 */
	private void initStatements()
	{
		STMT_CREATE_DOMAIN_TBL = "CREATE TABLE TBL_DOMAINS ("
				+ "DOMAIN_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), "
				+ "DOMAIN_NAME VARCHAR(500) UNIQUE, "
				+ "SALT LONG VARCHAR NOT NULL, "			
				+ "CONSTRAINT TBL_DOMAINS_PK PRIMARY KEY (DOMAIN_ID) "
				+ ")";
				
		STMT_CREATE_DATA_TBL = "CREATE TABLE TBL_DATA ("
				+ "DOMAIN_ID INT NOT NULL, "
				+ "USERNAME LONG VARCHAR, "
				+ "PASSWORD LONG VARCHAR, "	
				+ "OTHER_INFO LONG VARCHAR, "
				+ "CONSTRAINT TBL_DATA_PK PRIMARY KEY (DOMAIN_ID), "
				+ "CONSTRAINT TBL_DATA_FK FOREIGN KEY (DOMAIN_ID) REFERENCES TBL_DOMAINS (DOMAIN_ID) ON DELETE CASCADE"
				+ ")";
				
		STMT_CREATE_MASTERKEY = "CREATE TABLE TBL_MASTERKEY ("
				+ "MASTERKEY_ID INT NOT NULL, "
				+ "MASTERKEY LONG VARCHAR NOT NULL, "
				+ "SALT LONG VARCHAR NOT NULL, "
				+ "CONSTRAINT MASTERKEY_TBL_PK PRIMARY KEY (MASTERKEY_ID)"			
				+ ")";		
		
		SELECT_ALL_DOMAIN_ENTITIES = "SELECT " 
		        + "TBL_DOMAINS.DOMAIN_ID, DOMAIN_NAME, SALT, USERNAME, PASSWORD, OTHER_INFO"
		        + " FROM TBL_DOMAINS"
		        + " INNER JOIN TBL_DATA"
		        + " ON TBL_DOMAINS.DOMAIN_ID = TBL_DATA.DOMAIN_ID" ;
		
		SELECT_DOMAIN_ENTITY_BY_DOMAIN_NAME = "SELECT " 
		        + "TBL_DOMAINS.DOMAIN_ID, DOMAIN_NAME, USERNAME, PASSWORD, OTHER_INFO"
		        + " FROM TBL_DOMAINS"
		        + " INNER JOIN TBL_DATA"
		        + " ON TBL_DOMAINS.DOMAIN_ID = TBL_DATA.DOMAIN_ID WHERE TBL_DOMAINS.DOMAIN_ID = ?" ;
		
		UPDATE_DOMAIN = "UPDATE TBL_DOMAINS SET DOMAIN_NAME = ?, SALT = ? WHERE DOMAIN_ID = ?";
		
		//this updates the domains table without changing the salt. We won't be changing the salt if we just change the domain name...
		UPDATE_DOMAIN_NOSALT = "UPDATE TBL_DOMAINS SET DOMAIN_NAME = ? WHERE DOMAIN_ID = ?";
		
		UPDATE_DOMAIN_DATA = "UPDATE TBL_DATA SET USERNAME = ?, PASSWORD = ?, OTHER_INFO = ? WHERE DOMAIN_ID = ?";
		
		INSERT_DOMAIN = "INSERT INTO "   
			    + "TBL_DOMAINS (DOMAIN_NAME, SALT) VALUES (?, ?)";
		
		INSERT_DATA = "INSERT INTO "   
			    + "TBL_DATA (DOMAIN_ID, USERNAME, PASSWORD, OTHER_INFO) VALUES (?, ?, ?, ?)";
		
		INSERT_MASTERKEY = "INSERT INTO " 
			    + "TBL_MASTERKEY (MASTERKEY_ID, MASTERKEY, SALT) VALUES (?, ?, ?)";
		
		UPDATE_MASTERKEY = "UPDATE TBL_MASTERKEY " 
			    + "SET MASTERKEY = ?,  SALT = ? WHERE MASTERKEY_ID = ?";
		
		SELECT_MASTERKEY = "SELECT MASTERKEY, SALT FROM TBL_MASTERKEY WHERE MASTERKEY_ID = ?";
		
		DELETE_DOMAIN = "DELETE FROM TBL_DOMAINS WHERE DOMAIN_ID = ?";
	}
	
			
			
	public CredentialsDBManager(){
		dbName = DB_NAME;
		dbManager = new DBManager(DB_FULL_PATH);
		initStatements();
	}
	
	/**
	 * Constructor
	 * @param dbPath
	 * @param dbName
	 */
	public CredentialsDBManager(String dbPath, String dbName){
		this.dbName = dbName;
		dbManager = new DBManager(dbPath + "/" + dbName);
		initStatements();
	}
	/**
	 * Creates the database
	 * @throws SQLException 
	 */
	public void createDatabase() throws SQLException
	{		
		dbManager.createDatabase(DB_NAME);
		//System.out.println("\tDEBUG - created DB");
		dbManager.runStatement(STMT_CREATE_DOMAIN_TBL);	
		//System.out.println("\tDEBUG - created DOMAIN table");
		dbManager.runStatement(STMT_CREATE_DATA_TBL);
		//System.out.println("\tDEBUG - created DATA TBL");
		dbManager.runStatement(STMT_CREATE_MASTERKEY);	
		//System.out.println("\tDEBUG - created MASTERKEY tbl");		
	}
	
	/**
	 * Creates a connection to the database
	 * @throws SQLException
	 */
	public void connectToDB() throws SQLException
	{
		 mDBConn = dbManager.setUpConnection();
		 isConnectedToDB = true;		 
	}
	
	/**
	 * Closes the database connection
	 */
	public void disconnectFromDB()
	{
		DBManager.closeJDBCConnection(mDBConn);
		isConnectedToDB = false;
	}
	
	/**
	 * shuts down the database
	 */
	public void shutDownDB()
	{
		dbManager.shutDownDB();
	}
	
	public boolean getIsConnectedToDB()
	{
		return isConnectedToDB;
	}
	
		
	/**
	 * Returns ArrayList of DomainEntities containing all the domain data in the DB.
	 * NOTE: must have invoked connectToDB() before invoking this method
	 * @param masterkey Base64-encoded masterkey (needed for the CryptoS's of each DomainEntity)
	 * @return ArrayList of DomainEntities representing all domain data
	 * @throws SQLException 
	 */
	public ArrayList<DomainEntity> getAllDomainEntities(String masterkey) throws SQLException
	{
		ArrayList<DomainEntity> domainEntityList = new ArrayList<>();		
		Statement stmt = null;
		ResultSet rs = null;
		try
		{			
			stmt = mDBConn.createStatement();		
			rs = stmt.executeQuery(SELECT_ALL_DOMAIN_ENTITIES);	
			domainEntityList = convertResultSetToEntities(rs, masterkey);
			mDBConn.commit();
		}
		finally
		{
			DBManager.closeJDBCStatement(stmt);
			DBManager.closeJDBCResultSet(rs);
		}		
		return domainEntityList;
	}
	
	/**
	 * Returns DomainEntity for specified DomainName
	 * NOTE: must have invoked connectToDB() before invoking this method
	 * @param masterkey Base64-encoded masterkey (needed for the CryptoS's of each DomainEntity)
	 * @param domainName encrypted domain name
	 * @return DomainEntity corresponding to specified domain name
	 * @throws SQLException 
	 */
	public DomainEntity getDomainEntityByDomainName(String masterkey, String domainName) throws SQLException
	{
		ArrayList<DomainEntity> domainEntityList = new ArrayList<>();		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try
		{	
			stmt = mDBConn.prepareStatement(SELECT_DOMAIN_ENTITY_BY_DOMAIN_NAME);	
			stmt.setString(1,domainName);
			rs = stmt.executeQuery();	
			domainEntityList = convertResultSetToEntities(rs, masterkey);
			mDBConn.commit();
		}
		finally
		{
			DBManager.closeJDBCStatement(stmt);
			DBManager.closeJDBCResultSet(rs);
		}
		if(domainEntityList.isEmpty())
		{
			return null;
		}
		return domainEntityList.get(0);
	}
	
	
	/**
	 * Takes a ResultSet that contains all the domain info for any domain (TBL_DOMAINS inner joined on the
	 * TBL_DATA table) and creates a DomainEntity object from each "row" of the ResultSet.
	 * @param rs ResultSet to parse
	 * @param masterkey Base64-encoded masterkey (needed for the CryptoS's of each DomainEntity)
	 * @return
	 * @throws SQLException 
	 */
	private ArrayList<DomainEntity> convertResultSetToEntities(ResultSet rs, String masterkey) throws SQLException
	{
		ArrayList<DomainEntity> domainEntityList = new ArrayList<>();
		//NOTE - we can be assured that there will be a value for each field because we're only
		//adding data to the DB by retrieving it from a DomainEntity object, and we only set fields for those
		//objects using the setEncryptedStringField() method which sets a default value if none is specified
		while(rs.next())
		{			
			int domainID = rs.getInt(DomainFields.DOMAIN_ID.getName());
			String domainName = rs.getString(DomainFields.DOMAIN_NAME.getName());
			String salt = rs.getString(DomainFields.SALT.getName());
			String username = rs.getString(DomainFields.USER_NAME.getName());
			String password = rs.getString(DomainFields.PASSWORD.getName());
			String otherInfo = rs.getString(DomainFields.OTHER_INFO.getName());
			DomainEntity domain1 = new DomainEntity(masterkey, domainName, username, password, otherInfo, salt, domainID);
			domainEntityList.add(domain1);
			
			//debug!
			//System.out.println("ENTITY ENC NAME = " + domainName);
			//end debug!
		}
		return domainEntityList;
	}
	
	/**
	 * Updates the domain info for each domain entity in the list.
	 *  NOTE: must have invoked connectToDB() before invoking this method 
	 * @param domainsToUpdate
	 */
	public int updateDomainInfo(DomainEntity[] domainsToUpdate, boolean updateSalt)
	{
		PreparedStatement stmtUpdateDomain = null;
	    PreparedStatement stmtUpdateData = null;
		try
		{
			stmtUpdateDomain = updateSalt ? mDBConn.prepareStatement(UPDATE_DOMAIN) : mDBConn.prepareStatement(UPDATE_DOMAIN_NOSALT);
			stmtUpdateData = mDBConn.prepareStatement(UPDATE_DOMAIN_DATA);
			
			for(DomainEntity entity : domainsToUpdate)
			{
				if(updateSalt)
				{
					//"SET DOMAIN_NAME = ?, SET SALT = ? WHERE DOMAIN_ID = ?";
					stmtUpdateDomain.setString(1, entity.getEncryptedDomainName());	
					stmtUpdateDomain.setString(2, entity.getSaltStr());
					stmtUpdateDomain.setInt(3, entity.getDomainID());							
					stmtUpdateDomain.executeUpdate();
				}
				else
				{
					//"SET DOMAIN_NAME = ? WHERE DOMAIN_ID = ?";
					stmtUpdateDomain.setString(1, entity.getEncryptedDomainName());	
					stmtUpdateDomain.setInt(2, entity.getDomainID());							
					stmtUpdateDomain.executeUpdate();
				}
				
				
				//"SET USERNAME = ?, SET PASSWORD = ?, SET OTHER_INFO = ? WHERE DOMAIN_ID = ?";
				stmtUpdateData.setString(1, entity.getEncryptedUsername());
				stmtUpdateData.setString(2, entity.getEncryptedPassword());
				stmtUpdateData.setString(3, entity.getEncryptedOtherInfo());
				stmtUpdateData.setInt(4, entity.getDomainID());
				stmtUpdateData.executeUpdate();
				
				mDBConn.commit();
			}
			return 1;
		} 
		catch (SQLException e)
		{
			System.out.println("ERROR updating data - " + e.getMessage());
			e.printStackTrace();
			try
			{
				mDBConn.rollback();
			}
			catch(SQLException ex1){/*IGNORE, WE DON'T CARE*/}
			return -1;
		}
		finally
		{
			DBManager.closeJDBCStatement(stmtUpdateData);		
			DBManager.closeJDBCStatement(stmtUpdateDomain);			
		}
	}
	
	/**
	 * Creates a new domain for each domain entity in the list.
	 *  NOTE: must have invoked connectToDB() before invoking this method 
	 * @param domainsToUpdate
	 * @return array of DomainEntities. They are the same as were passed in, except that
	 * they have their DOMAIN_ID's set
	 */
	public ArrayList<DomainEntity> createNewDomains(ArrayList<DomainEntity> domainsToUpdate)
	{
		ArrayList<DomainEntity> listToReturn = new ArrayList<>(domainsToUpdate.size());
		PreparedStatement stmtUpdateDomain = null;
	    PreparedStatement stmtUpdateData = null;
	    ResultSet rsID = null;
		try
		{
			stmtUpdateDomain = mDBConn.prepareStatement(INSERT_DOMAIN, java.sql.Statement.RETURN_GENERATED_KEYS);
			stmtUpdateData = mDBConn.prepareStatement(INSERT_DATA);
			
			for(int i = 0; i < domainsToUpdate.size(); i++)
			{	
				DomainEntity entity = domainsToUpdate.get(i);
				
				//"INSERT INTO TBL_DOMAINS (DOMAIN_ID, DOMAIN_NAME, SALT) VALUES (?, ?, ?)";				
				//stmtUpdateDomain.setInt(1, entity.getDomainID());
				stmtUpdateDomain.setString(1, entity.getEncryptedDomainName());				
				stmtUpdateDomain.setString(2, entity.getSaltStr());
				stmtUpdateDomain.executeUpdate();				
				//get the auto-generated ID:
				rsID = stmtUpdateDomain.getGeneratedKeys();
				//set dummy variable
				int domainID = -1;
				if(rsID.next())
				{
					System.out.println("ABOUT TO GET DOMAIN ID");
					domainID = rsID.getInt(1);
					System.out.println("GOT DOMAIN ID");
				}
				
				//"INSERT INTO TBL_DATA (DOMAIN_ID, USERNAME, PASSWORD, OTHER_INFO) VALUES (?, ?, ?, ?)";
				stmtUpdateData.setInt(1, domainID);
				stmtUpdateData.setString(2, entity.getEncryptedUsername());
				stmtUpdateData.setString(3, entity.getEncryptedPassword());
				stmtUpdateData.setString(4, entity.getEncryptedOtherInfo());				
				stmtUpdateData.executeUpdate();
				
				mDBConn.commit();
				
				entity.setDomainID(domainID);
				//shallow copy! 
				listToReturn.add(entity);
			}
			return listToReturn;
		} 
		catch (SQLException e)
		{
			System.out.println("ERROR updating data: " + e.getMessage());
			try
			{
				mDBConn.rollback();
			}
			catch(SQLException ex1){/*IGNORE, WE DON'T CARE*/}
			return null;
		}
		finally
		{			
			DBManager.closeJDBCStatement(stmtUpdateData);			
			DBManager.closeJDBCResultSet(rsID);			
			DBManager.closeJDBCStatement(stmtUpdateDomain);
		}
	}
	
	
	
	/**
	 * Inserts the masterkey and its salt
	 * NOTE: must have invoked connectToDB() before invoking this method 
	 * @param masterKey
	 * @param salt
	 */
	public void insertMasterKey(String masterKey, String salt)
	{
		PreparedStatement stmtInsertMasterKey = null;
		try
		{
			//"INSERT INTO TBL_MASTERKEY (MASTERKEY_ID, MASTERKEY, SALT) VALUES (?, ?, ?)";
			stmtInsertMasterKey = mDBConn.prepareStatement(INSERT_MASTERKEY);
			stmtInsertMasterKey.setInt(1, MASTERKEY_ID);
			stmtInsertMasterKey.setString(2, masterKey);
			stmtInsertMasterKey.setString(3, salt);
			stmtInsertMasterKey.executeUpdate();
			mDBConn.commit();
		}
		catch(SQLException e)
		{
			System.out.println("ERROR adding master key: " + e.getMessage());
			try
			{
				mDBConn.rollback();
			}
			catch(SQLException ex1){/*IGNORE, WE DON'T CARE*/}
		}
		finally
		{			
			DBManager.closeJDBCStatement(stmtInsertMasterKey);			
		}
	}
	
	/**
	 * Updates the stored masterkey and salt
	 * @param masterKey
	 * @param salt
	 */
	public boolean updateMasterKey(String masterKey, String salt)
	{
		PreparedStatement stmtInsertMasterKey = null;
		try
		{
			//"INSERT INTO TBL_MASTERKEY MASTERKEY, SALT ID VALUES (?, ?, ?)";
			stmtInsertMasterKey = mDBConn.prepareStatement(UPDATE_MASTERKEY);			
			stmtInsertMasterKey.setString(1, masterKey);
			stmtInsertMasterKey.setString(2, salt);
			stmtInsertMasterKey.setInt(3, MASTERKEY_ID);
			stmtInsertMasterKey.executeUpdate();
			mDBConn.commit();
			return true;
		}
		catch(SQLException e)
		{
			System.out.println("ERROR adding master key: " + e.getMessage());
			try
			{
				mDBConn.rollback();
			}
			catch(SQLException ex1){/*IGNORE, WE DON'T CARE*/}
			return false;
		}
		finally
		{			
			DBManager.closeJDBCStatement(stmtInsertMasterKey);			
		}
	}
	
	/**
	 * Returns the hashed masterkey and its salt in an array
	 * NOTE: must have invoked connectToDB() before invoking this method 
	 * @return String[]{hashed_masterkey, salt}
	 */
	public String[] getMasterKey()
	{
		String masterkey = null;
		String salt = null;
		PreparedStatement stmtGetMasterkey = null;
		ResultSet rs = null;
		try
		{
			stmtGetMasterkey = mDBConn.prepareStatement(SELECT_MASTERKEY);
			stmtGetMasterkey.setInt(1, MASTERKEY_ID);
			rs = stmtGetMasterkey.executeQuery();
			
			while(rs.next())
			{
				masterkey = rs.getString("MASTERKEY");
				salt = rs.getString("SALT");
			}
			mDBConn.commit();
		}
		catch(SQLException e)
		{
			System.out.println("ERROR getting master key: " + e.getMessage());			
		}
		finally
		{
			DBManager.closeJDBCResultSet(rs);
			DBManager.closeJDBCStatement(stmtGetMasterkey);
		}
		return new String[]{masterkey, salt};
	}
	
	/**
	 * Deletes the specified domain from the database
	 * NOTE: must have invoked connectToDB() before invoking this method 
	 * @param domainEntity
	 */
	public int deleteDomain(DomainEntity domainEntity)
	{
		PreparedStatement stmtDelete = null;
		try
		{
			stmtDelete = mDBConn.prepareStatement(DELETE_DOMAIN);
			stmtDelete.setInt(1, domainEntity.getDomainID());
			int rowsAffected = stmtDelete.executeUpdate();
			mDBConn.commit();
			
			if(rowsAffected < 1)
			{
				System.out.println("ERROR - affected < 1 row");
				return -1;
			}
			else
			{
				return 1;
			}
		}
		catch(SQLException ex)
		{
			System.out.println("ERROR DELETING DOMAIN - " + ex.getMessage());
			return -1;
		}
		finally
		{			
			DBManager.closeJDBCStatement(stmtDelete);
		}
	}
	
	/**
	 * MAIN for testing
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		CredentialsDBManager tester = new CredentialsDBManager("TESTER", "testDB");
		tester.createDatabase();
	}

}
