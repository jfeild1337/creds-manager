package org.jfeild1337.credsmgr.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jfeild1337.credsmgr.crypto.CryptoUtils;
import org.jfeild1337.credsmgr.db.CredentialsDBManager;
import org.jfeild1337.credsmgr.db.DomainEntity;
import org.jfeild1337.credsmgr.db.DBManager;
import org.jfeild1337.credsmgr.filehandlers.DBFilehandler;
import org.jfeild1337.credsmgr.filehandlers.DataFileFormatException;
import org.jfeild1337.credsmgr.ui.helpers.ConfirmationDialog;
import org.jfeild1337.credsmgr.ui.helpers.CredsUtils;
import org.jfeild1337.credsmgr.ui.helpers.EnterStringPopup;
import org.jfeild1337.credsmgr.ui.helpers.TextPopup;

import org.apache.commons.codec.binary.Base64;
import org.jdesktop.swingx.autocomplete.*;

/**
 * Implements all the logic for the CredentialsManagerMainUI class. This could
 * have been baked into either CredentialsManagerMainUI or
 * CredentialsManagerMain but it's being done in here to keep these 3 classes as
 * readable as possible.<br/>
 * This class requires the Base64-encoded master password obtained from the
 * password prompt, as well as the main CredentialsDBManager object.
 *
 * @author Julian
 *
 */
public class CredentialsManagerMainUIHandler {
    //constants

    private final String MSG_ERROR_DUPLICATE_DOMAIN = "**Error: Domain name already exists in the database**";
    private final String CONFIRM_DELETE_MSG = "This will permanently delete the selected Domain and all of its credentials.<br>Are you sure?";
    private final String CONFIRM_SWITCH_AND_DISCARD_CHANGES = "You have unsaved changes; switching domains will discard them.<br>Are you sure?";
    private ImageIcon ICON_WARN = CredsUtils.getResourceImageAsIcon("warning-icon.png"); //new ImageIcon(getClass().getResource("/warning-icon.png"));
    private ImageIcon ICON_INFO = CredsUtils.getResourceImageAsIcon("info.png"); //new ImageIcon(getClass().getResource("/info.png"));
    private ImageIcon IMG_SUCCESS = CredsUtils.getResourceImageAsIcon("check_green.gif"); //new ImageIcon(getClass().getResource("/check_green.gif"));

    //member vars
    private CredentialsManagerMainUI mMainUI;
    private CredentialsDBManager mDBManager;
    private String mMasterKey; //base64 encoded master password
    private ArrayList<DomainEntity> mListDomainEntities;
    private HashMap<String, DomainEntity> mMapDomainNameToEntity = new HashMap<>();
    private DomainEntity mSelectedDomainEntity = null;

    /**
     *
     * @param dbManager - CredentialsDBManager setup in CredentialsManagerMain
     */
    public CredentialsManagerMainUIHandler(CredentialsDBManager dbManager, String masterkey) {
        mDBManager = dbManager;
        mMasterKey = masterkey;
        mMainUI = new CredentialsManagerMainUI();
        try {
            mDBManager.connectToDB();
        } catch (SQLException e) {
            //System.out.println("Main UI - Error connecting to DB: " + e.getMessage());
            CredsUtils.showErrorPopup("Error connecting to Database", "Database Error");
        }
        try {
            initData();
        } catch (SQLException e) {
            //System.out.println("Main UI - Error fetching database records: " + e.getMessage());
            CredsUtils.showErrorPopup("Error fetching database records", "Database Error");
        }
        setupListeners();
        initUI();
    }

    /**
     * Initializes database data etc
     *
     * @throws SQLException
     */
    private void initData() throws SQLException {
        mListDomainEntities = mDBManager.getAllDomainEntities(mMasterKey);
        mSelectedDomainEntity = (mListDomainEntities.isEmpty() ? null : mListDomainEntities.get(0));
        for (DomainEntity entity : mListDomainEntities) {
            mMapDomainNameToEntity.put(entity.getDecryptedDomainName(), entity);
            mMainUI.getmCmbBoxDomainSelector().addItem(entity.getDecryptedDomainName());
        }
        if (mSelectedDomainEntity != null) {
            selectDomain(mSelectedDomainEntity.getDecryptedDomainName(), true);
        }

    }

    /**
     * Assigns listeners to the components
     */
    private void setupListeners() {
        //select new domain
        addSelectedDomainListener();

        //add the listeners to the text fields to set isEdited() on the selected domain
        addGenericTextFieldListener(mMainUI.getTextFieldDomainEdit());
        addGenericTextFieldListener(mMainUI.getTextFieldUsername());
        addGenericTextFieldListener(mMainUI.getTextFieldPassword());
        addGenericTextPaneListener(mMainUI.getmTextViewerPane());

        //setup button listeners
        setupActionButtonListeners();

        setupClipboardButtonListeners();

        setupMenuListeners();
    }

    /**
     * Adds listener to the combobox
     */
    private void addSelectedDomainListener() {
        mMainUI.getmCmbBoxDomainSelector().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //this works, but registers after the domain has been switched. Therefore the selector shows the new value,
                //but the selected entity is the old one. Then if we try to set the selected entity back to the correct one,
                //we will get the prompt again, and this looks like it will lead to infinite recursion
                /*if(mSelectedDomainEntity != null && mSelectedDomainEntity.isEdited()==true)
                    {
                            TextPopup warnPopup = new TextPopup(CONFIRM_SWITCH_AND_DISCARD_CHANGES);					
                            String[] options = new String[]{"Discard", "Go Back"};		
                            int option = JOptionPane.showOptionDialog(null, warnPopup, "Unsaved Data", JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,  null, options, options[0]);
                            if(option == 1) //User wants to go back
                            {
                                    return;
                            }		
                    }*/
                //TODO..if the user changed the domain name, it will still be selectable 
                JComboBox<String> cmb = (JComboBox<String>) e.getSource();
                String selecteDomainName = (String) cmb.getSelectedItem();
                selectDomain(selecteDomainName, false);
                //every time we select a different domain, the text fields get written to, so the isEdited flag gets set to true -_-
                setSelectedDomainEdited(false);
            }
        });
        //add auto-complete to dropdown
        AutoCompleteDecorator.decorate(mMainUI.getmCmbBoxDomainSelector());
    }

    /**
     * Sets the globally selected DomainEntity to the one corresponding to the
     * specified name. (Note: the name is the DECRYPTED name)
     *
     * @param domainName domain name to select
     * @param forceComboBoxUpdate set to TRUE to set combo box selected item to
     * be the name of the selected domain
     */
    private void selectDomain(String domainName, boolean forceComboBoxUpdate) {
        if (domainName == null) {
            mSelectedDomainEntity = null;
            mMainUI.getmCmbBoxDomainSelector().setSelectedItem(null);
            mMainUI.getTextFieldDomainEdit().setText("");
            mMainUI.getTextFieldUsername().setText("");
            mMainUI.getTextFieldPassword().setText("");
            mMainUI.getmTextViewerPane().setText("");
        } else {
            mSelectedDomainEntity = mMapDomainNameToEntity.get(domainName);
            //update text fields accordingly
            mMainUI.getTextFieldDomainEdit().setText(mSelectedDomainEntity.getDecryptedDomainName());
            mMainUI.getTextFieldUsername().setText(mSelectedDomainEntity.getDecryptedUsername());
            mMainUI.getTextFieldPassword().setText(mSelectedDomainEntity.getDecryptedPassword());
            mMainUI.getmTextViewerPane().setText(mSelectedDomainEntity.getDecryptedOtherInfo());
            //reason we have this is because this step is redundant for the combobox listener
            if (forceComboBoxUpdate) {
                mMainUI.getmCmbBoxDomainSelector().setSelectedItem(mSelectedDomainEntity.getDecryptedDomainName());
            }
        }

    }

    /**
     * Adds a listener to set isEdited() on the selected domain when the text
     * field is edited
     *
     * @param txtField
     */
    private void addGenericTextFieldListener(JTextField txtField) {
        txtField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent arg0) {
                //tracks if the LAST edit was undone, so not much use				
            }

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                setSelectedDomainEdited(true);
            }

            @Override
            public void changedUpdate(DocumentEvent arg0) {
                setSelectedDomainEdited(true);
            }
        });
    }

    /**
     * Adds a listener to set isEdited() on the selected domain when the text
     * pane is edited
     *
     * @param txtField
     */
    private void addGenericTextPaneListener(JTextPane txtField) {
        txtField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent arg0) {
                //tracks if the LAST edit was undone, so not much use				
            }

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                setSelectedDomainEdited(true);
                mMainUI.setSavedLabelVisible(false);
            }

            @Override
            public void changedUpdate(DocumentEvent arg0) {
                setSelectedDomainEdited(true);
                mMainUI.setSavedLabelVisible(false);
            }
        });
    }

    /**
     * Simply checks that the selected domain entity is not null, and if it's
     * not, sets isEdited() to true
     */
    private synchronized void setSelectedDomainEdited(boolean isEdited) {
        if (mSelectedDomainEntity == null) {
            return;
        }
        mSelectedDomainEntity.setIsEdited(isEdited);
    }

    /**
     * Assigns listeners to the ADD, DELETE, and EDIT domain buttons as well as
     * SAVE and CANCEL
     */
    private void setupActionButtonListeners() {
        mMainUI.getBtnAddNewDomain().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                addNewDomain();
            }
        });
        mMainUI.getBtnDeleteDomain().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (mSelectedDomainEntity != null) {
                    deleteDomain(mSelectedDomainEntity);
                }
            }
        });
        mMainUI.getBtnSave().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                //if the domain name has been changed, we need to keep track of the old name so
                //we can update the drop down accordingly!
                String oldSelectedDomainName = (String) mMainUI.getmCmbBoxDomainSelector().getSelectedItem();
                updateDomain();
                String newDomainName = mSelectedDomainEntity.getDecryptedDomainName();
                if (!oldSelectedDomainName.equals(newDomainName)) {
                    mMapDomainNameToEntity.remove(oldSelectedDomainName);
                    mMapDomainNameToEntity.put(newDomainName, mSelectedDomainEntity);
                    mMainUI.getmCmbBoxDomainSelector().addItem(newDomainName);
                    mMainUI.getmCmbBoxDomainSelector().removeItem(oldSelectedDomainName);
                    mMainUI.getmCmbBoxDomainSelector().setSelectedItem(newDomainName);
                }
            }
        });
        mMainUI.getBtnReset().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //just reset the text fields back to their original values
                if (mSelectedDomainEntity != null) {
                    resetFields(true);
                }
            }
        });

    }

    /**
     * Resets the fields to their original values (last saved value).
     */
    private void resetFields(boolean bForceUpdateComboBoxChange) {
        mMainUI.getmCmbBoxDomainSelector().setSelectedItem(mSelectedDomainEntity.getDecryptedDomainName());
    }

    /**
     * NOT USED-TODO REMOVE Resets a change to the domain name (if the change
     * has not been saved)
     */
    private void resetDomainName() {
        //if the domain name has been changed, we have to fix the drop down
        String dropdownDomainName = (String) mMainUI.getmCmbBoxDomainSelector().getSelectedItem();
        if (!mSelectedDomainEntity.getDecryptedDomainName().equals(dropdownDomainName)) {
            //re-add the domain name
            mMainUI.getmCmbBoxDomainSelector().removeItem(mSelectedDomainEntity.getDecryptedDomainName());
            //and remove the new one
            mMainUI.getmCmbBoxDomainSelector().removeItem(dropdownDomainName);
        }
        mMainUI.getmCmbBoxDomainSelector().setSelectedItem(mSelectedDomainEntity.getDecryptedDomainName());
    }

    /**
     * Spawns a popup to request a new domain name, either for renaming the
     * currently selected domain or for creating a new domain
     *
     * @param isNewDomain - TRUE if this is for creating a new domain
     * @return
     */
    private String getNewDomainNameFromUser(boolean isNewDomain) {
        String sWindowTitle = (isNewDomain) ? "Add New Domain" : "Rename Domain";

        EnterStringPopup domainPopup = new EnterStringPopup("Enter Domain Name:", "ERROR - Name cannot be null and cannot already exist");
        String newDomainName = null;

        String[] options = new String[]{"OK", "Cancel"};
        int option = JOptionPane.showOptionDialog(null, domainPopup, sWindowTitle, JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (option == 0) // pressing OK button
        {
            newDomainName = domainPopup.getTxtFieldEnterValue().getText().trim();
            //ok we've got the new name, so create default creds for it	if it doesn't already exist:
            if (doesDomainAlreadyExist(newDomainName)) {
                CredsUtils.showErrorPopup("Cannot add " + newDomainName + "; domain already exists", "DUPLICATE NAME ERROR");
                newDomainName = null;
            }
        }
        return newDomainName;
    }

    /**
     * Creates a new domain with default values for username/password/other
     * info.
     */
    private void addNewDomain() {
        String newDomainName = getNewDomainNameFromUser(true);
        if (newDomainName == null) {
            return;
        } else {
            DomainEntity newDomainEntity = DomainEntity.createNewDomainEntityFromDomainName(mMasterKey, newDomainName);
            //TODO create different methods for adding domains
            ArrayList<DomainEntity> tempList = new ArrayList<>(1);
            tempList.add(newDomainEntity);
            ArrayList<DomainEntity> updatedDomainEntities = mDBManager.createNewDomains(tempList);
            //we've only sent one, so we only expect one back
            if (updatedDomainEntities == null || updatedDomainEntities.isEmpty()) {
                CredsUtils.showErrorPopup("Database error adding new domain", "ERROR");
            } else {
                DomainEntity newDomainEntity2 = updatedDomainEntities.get(0);
                newDomainEntity2.setIsNew(false);
                mMapDomainNameToEntity.put(newDomainName, newDomainEntity2);
                ((JComboBox<String>) mMainUI.getmCmbBoxDomainSelector()).addItem(newDomainName);
                ((JComboBox<String>) mMainUI.getmCmbBoxDomainSelector()).setSelectedItem(newDomainName); //this will call the selectDomain() method				
            }
        }
    }

    /**
     * Deletes the domain and corresponding info defined by the specified
     * DomainEntity object
     *
     * @param domainToDelete
     */
    private void deleteDomain(DomainEntity domainToDelete) {
        int option = CredsUtils.showPopup("Confirm Delete", CONFIRM_DELETE_MSG, ICON_WARN);
        if (option == 0) {
            int status = mDBManager.deleteDomain(domainToDelete);
            if (status != 1) {
                CredsUtils.showErrorPopup("Error - cannot delete domain", "Error");
            } else {
                mMapDomainNameToEntity.remove(domainToDelete.getDecryptedDomainName());
                mMainUI.getmCmbBoxDomainSelector().removeItem(domainToDelete.getDecryptedDomainName());
                if (mMapDomainNameToEntity.isEmpty()) {
                    selectDomain(null, true);
                } else {
                    String firstDomain = (String) mMainUI.getmCmbBoxDomainSelector().getItemAt(0);
                    selectDomain(firstDomain, true);
                }
            }
        }
    }

    /**
     * Updates the selected domain's username/password/other info to the values
     * entered in the text fields
     */
    private void updateDomain() {
        mSelectedDomainEntity.setmDomainName((String) mMainUI.getmCmbBoxDomainSelector().getSelectedItem());
        mSelectedDomainEntity.setmDomainName(mMainUI.getTextFieldDomainEdit().getText());
        mSelectedDomainEntity.setmUsername(mMainUI.getTextFieldUsername().getText());
        mSelectedDomainEntity.setmPassword(mMainUI.getTextFieldPassword().getText());
        mSelectedDomainEntity.setOtherInfo(mMainUI.getmTextViewerPane().getText());
        int status = mDBManager.updateDomainInfo(new DomainEntity[]{mSelectedDomainEntity}, false);
        if (status != 1) {
            CredsUtils.showErrorPopup("Error saving changes", "Error");
        } else {
            mSelectedDomainEntity.setIsEdited(false);
            mMainUI.setSavedLabelVisible(true);
        }
    }

    /**
     * Causes the clipboard buttons to copy the contents of their respective
     * fields to the clipboard!
     */
    private void setupClipboardButtonListeners() {
        mMainUI.getBtnCopyDomainToClipboard().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                clpbrd.setContents(new StringSelection(mMainUI.getTextFieldDomainEdit().getText()), null);

            }
        });
        mMainUI.getBtnCopyUsername().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                clpbrd.setContents(new StringSelection(mMainUI.getTextFieldUsername().getText()), null);
            }
        });
        mMainUI.getBtnCopyPassword().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                clpbrd.setContents(new StringSelection(mMainUI.getTextFieldPassword().getText()), null);
            }
        });
        mMainUI.getBtnCopyOtherInfo().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                clpbrd.setContents(new StringSelection(mMainUI.getmTextViewerPane().getText()), null);
            }
        });
    }

    /**
     * Sets up the listeners for the UI
     */
    private void initUI() {
        mMainUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mMainUI.addWindowListener(
                new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                //System.out.println("Disconnecting from Database...");
                mDBManager.disconnectFromDB();
                //System.out.println("Shutting down Database...");
                mDBManager.shutDownDB();
                //System.out.println("Destroying UI...");
                mMainUI.dispose();
            }
        }
        );
        //setUpSystemLookAndFeel();
        mMainUI.pack();
        mMainUI.setVisible(true);
        mMainUI.setLocationRelativeTo(null);
    }

    /**
     * Adds listeners to the File Menu
     */
    private void setupMenuListeners() {
        mMainUI.getMntmChangeMasterPassword().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeMasterPassword();

            }
        });
        mMainUI.getMntmImportFromFile().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                addDomainsFromFile();
            }
        });
        mMainUI.getMntmExportDatabase().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                exportDatabaseToFile();
            }
        });
        mMainUI.getMntmHelpAbout().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                showHelpAbout();
            }
        });
    }

    /**
     * Shows the help > about (invoked from Help > About menu item)
     */
    private void showHelpAbout() {
        String title = "About Credentials Manager";
        String version = "Credentials Manager version "
                + CredsUtils.STR_VERSION_MAJOR + "." + CredsUtils.STR_VERSION_MINOR + "." + CredsUtils.STR_VERSION_BUILD
                + (CredsUtils.STR_BUILDER_TAG.isEmpty() ? "" : ("-" + CredsUtils.STR_BUILDER_TAG));
        String msg = version + "<br><br>2017 jfeild1337<br><br>"
                + "Application incorporates the following open-source libraries:<br>"
                + "<ul>"
                + "<li>Apache commons-io-2.4</li>"
                + "<li>Apache commons-codec-1.6</li>"
                + "<li>Swing Labs swingx-all-1.6.4</li>"
                + "<li>Apache derby</li>"
                + "</ul>";
        //CredsUtils.showPopup(title, msg, new ImageIcon(getClass().getResource("/lock_open.png")));
        CredsUtils.showPopup(title, msg, CredsUtils.getResourceImageAsIcon("lock_open.png"));
    }

    /**
     * Method that is invoked from the File -> Import menu
     */
    private void addDomainsFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        File infile = openDialog(fileChooser);
        if (infile == null) {
            return;
        }
        DBFilehandler fh = new DBFilehandler(mMasterKey, infile);
        try {
            ArrayList<DomainEntity> entitiesFromFile = fh.getDomainEntitiesFromfile();
            if (entitiesFromFile.size() == 0) {
                CredsUtils.showPopup("INFO", "Found 0 domain entries in file", ICON_WARN);
            } else {
                //check for duplicate domain names
                ArrayList<DomainEntity> filteredList = new ArrayList<>();
                ArrayList<String> duplicates = new ArrayList<>();
                for (DomainEntity ent : entitiesFromFile) {
                    if (doesDomainAlreadyExist(ent.getDecryptedDomainName())) {
                        duplicates.add(ent.getDecryptedDomainName());
                    } else {
                        filteredList.add(ent);
                    }
                }
                //try to add them to the database
                ArrayList<DomainEntity> newDatabaseEntities = mDBManager.createNewDomains(filteredList);
                //add the new entities to the GUI
                for (DomainEntity newEnt : newDatabaseEntities) {
                    DomainEntity entClone = newEnt.clone();
                    mMapDomainNameToEntity.put(entClone.getDecryptedDomainName(), entClone);
                    mMainUI.getmCmbBoxDomainSelector().addItem(entClone.getDecryptedDomainName());
                }
                //show the user our success or failure...
                int successfulEntities = newDatabaseEntities.size();
                int duplicateCount = duplicates.size();
                String msg = "Successfully added " + successfulEntities + " items to the database.";
                if (duplicateCount > 0) {
                    msg += "\n" + duplicateCount + " items were omitted since their domain names already exist in the database:\n";
                    for (String dName : duplicates) {
                        msg += dName + "\n";
                    }
                }
                CredsUtils.showPopup("IMPORT COMPLETE", msg, ICON_WARN);
            }
        } catch (FileNotFoundException e) {
            CredsUtils.showErrorPopup("Cannot access file; file does not exist or you do not have permission to read it", "ERROR");
        } catch (DataFileFormatException e2) {
            CredsUtils.showErrorPopup("File is not formatted properly; Error: " + e2.getMessage(), "FILE FORMAT ERROR");
        }
    }

    /**
     * Dumps database contents to plain-text file
     */
    private void exportDatabaseToFile() {
        //CredsUtils.showPopup("Oops!", "This feature is not implemented yet!", new ImageIcon(getClass().getResource("/warning-icon.png")));
        JFileChooser fileChooser = new JFileChooser();
        File infile = openDialog(fileChooser);
        if (infile == null) {
            return;
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(infile), "utf-8"));

            writer.write(DBFilehandler.NEW_DOMAIN_DELIM);
            writer.newLine();
            for (DomainEntity entity : mMapDomainNameToEntity.values()) {
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
            CredsUtils.showPopup("File Export Complete", "File export complete! Remember to delete the file when you are finished with it.", ICON_INFO);
        } catch (IOException ex) {
            CredsUtils.showErrorPopup("Error openeing file: " + ex.getMessage(), "File IO Error");
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {/*ignore*/
            }
        }

    }
    //====================================================================
    //============================HELPERS=================================
    //====================================================================

    /**
     * Returns true if the specified domain name exists. We have to do a
     * decrypted compare since in the database it's possible to have to
     * identical domains since we store the encrypted values and each one gets a
     * unique salt...
     *
     * @param newDomainName
     * @return
     */
    private boolean doesDomainAlreadyExist(String newDomainName) {
        return mMapDomainNameToEntity.containsKey(newDomainName);
    }

    private File openDialog(JFileChooser fileChooser) {
        int openChoice = fileChooser.showOpenDialog(mMainUI);

        if (openChoice == JFileChooser.APPROVE_OPTION) {
            File openFile = fileChooser.getSelectedFile();
            return openFile;
        }
        return null;
    }

    /**
     * Changes the master password and re-encrypts all the data
     */
    private void changeMasterPassword() {
        String password = CredsUtils.createMasterPassword(false);

        if (password == null) {
            return;
        }
        byte[] masterSalt = CryptoUtils.generateRandomSalt(32);
        String masterSaltStr = CryptoUtils.byteArrayToString(masterSalt);
        String hashedMasterPwd = CryptoUtils.basicOneWayHash(password, masterSaltStr);

        if (!mDBManager.updateMasterKey(hashedMasterPwd, masterSaltStr)) {
            CredsUtils.showErrorPopup("Error updating master password", "Error");
            return;
        }

        mMasterKey = Base64.encodeBase64String(password.getBytes());
        //System.out.println("NEW PASSWORD BASE 64: " + mMasterKey);
        //update all domains
        for (DomainEntity entity : mMapDomainNameToEntity.values()) {
            entity.setNewMasterKey(mMasterKey);
            int status = mDBManager.updateDomainInfo(new DomainEntity[]{entity}, true);
            if (status != 1) {
                CredsUtils.showErrorPopup("Error saving updating master password for " + entity.getDecryptedDomainName(), "Error");
            }
        }

    }

    //====================================================================
    //============================APPEARANCE STUFF========================
    //====================================================================
    /**
     * Sets up the NIMBUS look and feel
     */
    private void setUpNimbusLookAndFeel() {
        // UIManager.put("nimbusBase", ColorGenerator.getBelizeHole());
        // UIManager.put("nimbusBlueGrey", ColorGenerator.getBelizeHole());
        // UIManager.put("control", ColorGenerator.getSilver());

        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.out.println(e.getClass() + "; " + e.getMessage());
        }
    }

    /**
     * Sets up the SYSTEM look and feel
     */
    private void setUpSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.out.println(e.getClass() + "; " + e.getMessage());
        }
    }

}
