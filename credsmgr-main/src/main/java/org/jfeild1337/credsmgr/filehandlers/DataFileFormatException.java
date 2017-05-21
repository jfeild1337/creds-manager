package org.jfeild1337.credsmgr.filehandlers;

/**
 * Exception to indicate that the data file was not in the expected format
 * @author Julian
 *
 */
public class DataFileFormatException extends Exception {
	
	private static String defaultMessage = "Error - file is not formatted properly. Please check the file format and try again.";
	
	public DataFileFormatException(String message)
	{
		super(message);
	}
	
	public DataFileFormatException()
	{		
		super(defaultMessage);
	}

}
