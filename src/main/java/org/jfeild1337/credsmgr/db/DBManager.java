/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jfeild1337.credsmgr.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

/**
 * -------------------------------------------------------
 * PURPOSE:
 * This class handles connections to the Derby database.
 * Recommended usage is to use this class as a member variable
 * and use its methods for all database actions. The database
 * should be shut down before program termination using this
 * class's shutdown method
 * --------------------------------------------------------
 * 
 * @author Julian
 *
 */
public class DBManager {
	 private String framework = "embedded";
	 private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	 private String protocol = "jdbc:derby:";
	 private String dbName = "database/embeddedDB"; //default!!	 

	 /**
	  * Constructor. Sets the target database name to the path specified.
	  * @param datbaseName the path to the database. For example if the database
	  * is in datastore/creds, specify "datastore/creds"
	  */
	public DBManager(String databaseName) {
		dbName = databaseName;
	}
	
	 /**
	  * Constructor. Sets the target database name to the default value of
	  * database/embeddedDB (database folder is at same level as "src" and "bin")
	  */
	public DBManager() {
		
	}
	
	/**
	 * Creates a database with the given name.
	 * @param sDBName the database name, including the relative file path. Path is relative
	 * to location that the Java code is running from (most likely the 'bin' folder). 
	 * Example: /datastore/myNewDB
	 * @throws SQLException 
	 * 
	 */
	public void createDatabase(String sDBName) throws SQLException{
		loadDriver();
		Connection conn = DriverManager.getConnection(protocol + dbName
                + ";create=true", new Properties());
		closeJDBCResources(conn);		
	}

	/**
	 * Closes a JDBC statement in a try-catch block
	 * @param stmt
	 */
	public static void closeJDBCStatement(Statement stmt)
	{
		try
		{
			if(stmt != null)
			{
				stmt.close();
				stmt = null;
			}
		}
		catch(SQLException e)
		{ 
			System.out.println("DBManager.java > closeJDBCStatement() - ERROR closing JDBC statement: " + e.getMessage()); 
		}
	}
	
	/**
	 * Closes a JDBC connection in a try-catch block
	 * @param conn
	 */
	public static void closeJDBCConnection(Connection conn)
	{
		try
		{
			if(conn != null)
			{
				conn.close();
				conn = null;
			}
		}
		catch(SQLException e)
		{ 
			System.out.println("DBManager.java > closeJDBCConnection() - ERROR closing JDBC connection: " + e.getMessage()); 
		}
	}
	/**
	 * Closes a JDBC ResultSet in a try-catch block
	 * @param conn
	 */
	public static void closeJDBCResultSet(ResultSet rs)
	{
		try
		{
			if(rs != null)
			{
				rs.close();
				rs = null;	
			}
		}
		catch(SQLException e)
		{ 
			System.out.println("DBManager.java > closeJDBCResultSet() - ERROR closing JDBC ResultSet: " + e.getMessage()); 
		}
	}
	
	/**
	 * Closes a Statement and a Connection (in that order). Be sure to run this
	 * from inside a FINALLY block to ensure proper deallocation of resources.
	 * @param conn
	 * @param stmt
	 */
	public static void closeJDBCResources(Connection conn, Statement stmt)
	{		
		closeJDBCStatement(stmt);		
		closeJDBCConnection(conn);
	}
	
	/**
	 * Closes a ResultSet, a Statement and a Connection (in that order). Be sure to run this
	 * from inside a FINALLY block to ensure proper deallocation of resources.
	 * @param conn
	 * @param stmt
	 * @param rs
	 */
	public static void closeJDBCResources(Connection conn, Statement stmt, ResultSet rs)
	{
		closeJDBCResultSet(rs);
		closeJDBCResources(conn, stmt);
	}
	/**
	 * Safely closes a Connection object
	 * @param conn
	 */
	public static void closeJDBCResources(Connection conn){
		try
		{
			if(conn != null)
			{
				conn.close();
				conn = null;
			}
		}
		catch(SQLException e){ /*IGNORE*/ }
	}
	
	
	/**
	 * Runs a generic statement
	 * @param statement
	 * @throws SQLException 
	 */
	public void runStatement(String statement) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		try
		{
			conn = setUpConnection();		
			stmt = conn.prepareStatement(statement);		
			stmt.executeUpdate();
			conn.commit();
		}
		finally
		{
			closeJDBCResources(conn, stmt);
		}
	}
	
	
	/**
	 * Runs a QUERY, converts the result set to an array of strings and then returns the array
	 * @param query
	 * @return
	 * @throws SQLException 
	 */
	public ArrayList<String> runQuery_getListOfStrings(String query) throws SQLException{
		ArrayList<String> res = new ArrayList<>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			conn = setUpConnection();		
			stmt = conn.createStatement();		
			rs = stmt.executeQuery(query);	
			while(rs.next())
			{			
				res.add(rs.getString(1));
			}
			conn.commit();
		}
		finally
		{
			closeJDBCResources(conn, stmt, rs);
		}
				
		return res;		
	}
	
	/**
	 * Runs a query and returns a single string
	 * @param query
	 * @return
	 * @throws SQLException
	 */
	public String runQuery_getSingleString(String query) throws SQLException
	{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String result = "";
		try
		{
			conn = setUpConnection();			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);			
			rs.next();
			result = rs.getString(1);
			conn.commit();
		}
		finally
		{
			closeJDBCResources(conn, stmt, rs);
		}
		return result;
	}
	
		
	/**
	 * Sets up a connection to the embedded Derby DB.
	 * Syntax for using the connection:<br/>
	 * <code>
	 *  stmt = conn.createStatement();     <br/>
	 *	rs = stmt.executeQuery(query);     <br/>
	 *  while(rs.next()){                  <br/>	
	 *	    result = rs.getString(1);      <br/>
	 *      //do something with resultset  <br/>
	 *  }                                  <br/>
	 *	conn.commit();                     <br/>
	 * </code>
	 * @return
	 * @throws SQLException 
	 */
	public Connection setUpConnection() throws SQLException{
		loadDriver();
		Connection conn = DriverManager.getConnection(protocol + dbName
                + ";create=false", new Properties());
		conn.setAutoCommit(false);
		return conn;
	}
	
	
	/**
	 * Loads the derby driver.
	 */
	private void loadDriver() {
        /*
         *  The JDBC driver is loaded by loading its class.
         *  If you are using JDBC 4.0 (Java SE 6) or newer, JDBC drivers may
         *  be automatically loaded, making this code optional.
         *
         *  In an embedded environment, this will also start up the Derby
         *  engine (though not any databases), since it is not already
         *  running. In a client environment, the Derby engine is being run
         *  by the network server framework.
         *
         *  In an embedded environment, any static Derby system properties
         *  must be set before loading the driver to take effect.
         */
        try {
            Class.forName(driver).newInstance();
            System.out.println("Loaded the appropriate driver");
        } catch (ClassNotFoundException cnfe) {
            System.err.println("\nUnable to load the JDBC driver " + driver);
            System.err.println("Please check your CLASSPATH.");
            cnfe.printStackTrace(System.err);
        } catch (InstantiationException ie) {
            System.err.println(
                        "\nUnable to instantiate the JDBC driver " + driver);
            ie.printStackTrace(System.err);
        } catch (IllegalAccessException iae) {
            System.err.println(
                        "\nNot allowed to access the JDBC driver " + driver);
            iae.printStackTrace(System.err);
        }
    }

	/**
	 * Shuts down the Derby DB
	 */
	public void shutDownDB(){
		try
        {
            // the shutdown=true attribute shuts down Derby
			DriverManager.getConnection("jdbc:derby:" + dbName + ";shutdown=true");

            // To shut down a specific database only, but keep the
            // engine running (for example for connecting to other
            // databases), specify a database in the connection URL:
            //DriverManager.getConnection("jdbc:derby:" + dbName + ";shutdown=true");
        }
        catch (SQLException se)
        {
            if (( (se.getErrorCode() == 45000)
                    && ("08006".equals(se.getSQLState()) ))) {
                // we got the expected exception
                System.out.println("Derby shut down normally");
                // Note that for single database shutdown, the expected
                // SQL state is "08006", and the error code is 45000.
            } else {
                // if the error code or SQLState is different, we have
                // an unexpected exception (shutdown failed)
                System.err.println("Derby did not shut down normally");
                printSQLException(se);
            }
        }
    }
		
	/**
     * Prints details of an SQLException chain to <code>System.err</code>.
     * Details included are SQL State, Error code, Exception message.
     *
     * @param e the SQLException from which to print details.
     */
    public static void printSQLException(SQLException e)
    {
        // Unwraps the entire exception chain to unveil the real cause of the
        // Exception.
        while (e != null)
        {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            // for stack traces, refer to derby.log or uncomment this:
            //e.printStackTrace(System.err);
            e = e.getNextException();
        }
    }   
	

}
