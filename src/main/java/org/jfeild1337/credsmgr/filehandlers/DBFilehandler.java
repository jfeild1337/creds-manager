package org.jfeild1337.credsmgr.filehandlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import org.jfeild1337.credsmgr.db.DomainEntity;
import org.jfeild1337.credsmgr.misc.TempDomainEntity;
import org.jfeild1337.credsmgr.ui.helpers.CredsUtils;

/**
 *
 * @author Julian
 *
 */
public class DBFilehandler {

    private File mSrcFile;
    private String mMasterKey;
    public static final String NEW_DOMAIN_DELIM = "----------------------------------------";

    public DBFilehandler(String masterKey, File srcFile) {
        mMasterKey = masterKey;
        mSrcFile = srcFile;
    }

//	/**
//	 * Parses mSrcFile and creates DomainEntities from the contents. Returns the Entities in an
//	 * Array
//	 * @return
//	 * @throws DataFileFormatException 
//	 * @throws FileNotFoundException 
//	 */
//	public DomainEntity[] getDomainEntitiesFromfile() throws FileNotFoundException, DataFileFormatException
//	{		
//		ArrayList<TempDomainEntity> tempEntities = parseFile();
//		DomainEntity[] listDomainEntities = new DomainEntity[tempEntities.size()];
//		 
//		for(int i = 0; i < tempEntities.size(); i++)
//		{
//			TempDomainEntity ent = tempEntities.get(i);
//			DomainEntity domainEnt = DomainEntity.createNewDomainEntityFromComponents(
//					mMasterKey, ent.getDomainName(), ent.getUserName(), ent.getPassword(), ent.getOtherInfo());
//			listDomainEntities[i] = domainEnt;
//		}
//		return listDomainEntities;
//	}
    /**
     * Parses mSrcFile and creates DomainEntities from the contents. Returns the
     * Entities in an Array
     *
     * @return
     * @throws DataFileFormatException
     * @throws FileNotFoundException
     */
    public ArrayList<DomainEntity> getDomainEntitiesFromfile() throws FileNotFoundException, DataFileFormatException {
        ArrayList<TempDomainEntity> tempEntities = parseFile();
        ArrayList<DomainEntity> listDomainEntities = new ArrayList<>();

        for (int i = 0; i < tempEntities.size(); i++) {
            TempDomainEntity ent = tempEntities.get(i);
            DomainEntity domainEnt = DomainEntity.createNewDomainEntityFromComponents(
                    mMasterKey, ent.getDomainName(), ent.getUserName(), ent.getPassword(), ent.getOtherInfo());
            listDomainEntities.add(domainEnt);
        }
        return listDomainEntities;
    }

    /**
     * Takes the list of entities, then decrypts their contents and stores them
     * to a plain-text file that can be read in by this application.<br><b>
     * MIGHT I REMIND YOU THAT THIS IS A TERRIBLE THING TO DO???</b><br>
     *
     * @param outfile the file to which to write the plaintext entities
     * @param entities Collection of DomainEntities
     */
    public static void storeDomainEntitiesToFile(File outfile, Collection<DomainEntity> entities) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), "utf-8"));

            writer.write(DBFilehandler.NEW_DOMAIN_DELIM);
            writer.newLine();
            for (DomainEntity entity : entities) {
                writer.write(entity.getDecryptedDomainName());
                writer.newLine();
                writer.write(entity.getDecryptedUsername());
                writer.newLine();
                writer.write(entity.getDecryptedPassword());
                writer.newLine();
                writer.write(entity.getDecryptedOtherInfo());
                writer.newLine();
                writer.write(DBFilehandler.NEW_DOMAIN_DELIM);
                writer.newLine();
            }
            CredsUtils.showPopup("File Export Complete", 
                    "File export complete! Remember to delete the file when you are finished with it.", 
                    CredsUtils.getResourceImageAsIcon(CredsUtils.ICON_SUCCESS_FNAME));
        } catch (IOException ex) {
            CredsUtils.showErrorPopup("Error opening file: " + ex.getMessage(), "File IO Error");
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {/*ignore*/
            }
        }

    }

    /**
     * Parses a file and generates a list of TempDomainEntities from its
     * contents. FILE MUST BE IN THE FORMAT:<br>
     * -----------------------------------------<br>
     * domain name<br>
     * username<br>
     * password<br>
     * other info<br>
     * that can span several lines<br>
     * <br>
     * Note: this is at best "beta" functionality, and isn't very robust
     *
     * @return ArrayList of TempDomainEntities from the file contents. Call
     * DomainEntity.createNewDomainEntityFromComponents() to create encrypted
     * database objects from the TempDomainEntities
     * @throws FileNotFoundException
     * @throws DataFileFormatException
     */
    private ArrayList<TempDomainEntity> parseFile() throws FileNotFoundException, DataFileFormatException {
        boolean hasError = false;
        ArrayList<TempDomainEntity> listTempDomainEntities = new ArrayList<>();
        Scanner scanner = new Scanner(mSrcFile);
        try {
            String newDelim = "";

            //need a special case for the first line since it's the new domain delimeter
            String line = "";
            do {
                line = scanner.nextLine().trim();
            } while (line.isEmpty() && scanner.hasNextLine());
            //if we quit because we ran out of lines, exit the loop
            if (!scanner.hasNextLine()) {
                throw new DataFileFormatException("Invalid File...no data could be parsed");
            }
            //first line with content will be the delimeter
            newDelim = line.trim();

            //and now, read through the file and gather the info...
            while (scanner.hasNextLine()) {
                String domainName = "";
                String username = "";
                String password = "";
                String otherinfo = "";

                //domain name
                do {
                    domainName = scanner.nextLine().trim();
                } while (domainName.isEmpty() && scanner.hasNextLine()); //skip blank lines

                //check that we've got another line, if not then quit:
                if (!scanner.hasNextLine()) {
                    break;
                }
                do {
                    username = scanner.nextLine().trim();
                } while (username.isEmpty() && scanner.hasNextLine()); //skip blank lines	            

                //check that we've got another line, if not then quit:
                if (!scanner.hasNextLine()) {
                    break;
                }
                do {
                    password = scanner.nextLine().trim();
                } while (password.isEmpty() && scanner.hasNextLine()); //skip blank lines

                //get the "other info". This could be one line, or several lines, or could be empty.
                if (!scanner.hasNextLine()) {
                    break;
                }
                do {
                    line = scanner.nextLine();
                    if (!line.startsWith(newDelim)) {
                        otherinfo += line.trim() + "\n";
                    }
                } while (scanner.hasNextLine() && !line.startsWith(newDelim));
                if (otherinfo.isEmpty()) {
                    otherinfo = "None";
                }
                TempDomainEntity ent = new TempDomainEntity(domainName, username, password, otherinfo.trim());
                listTempDomainEntities.add(ent);
            }
        } finally {
            scanner.close();
        }
        return listTempDomainEntities;
    }

    public static void main(String[] args) throws FileNotFoundException, DataFileFormatException {
        File file = new File("support/Examples/SampleInput.txt");
        DBFilehandler hnd = new DBFilehandler("Derpity derpt derp", file);
        ArrayList<TempDomainEntity> tempEntities = hnd.parseFile();
        for (TempDomainEntity ent : tempEntities) {
            System.out.println("DOMAIN = " + ent.getDomainName());
            System.out.println("USER = " + ent.getUserName());
            System.out.println("PASSWORD = " + ent.getPassword());
            System.out.println("OTHER INFO = " + ent.getOtherInfo());
            System.out.println("-----------------------------------");
        }

    }

}
