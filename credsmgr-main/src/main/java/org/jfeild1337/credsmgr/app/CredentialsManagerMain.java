package org.jfeild1337.credsmgr.app;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.codec.binary.Base64;

import org.jfeild1337.configmanager.ConfigManager;
import org.jfeild1337.credsmgr.crypto.CryptoUtils;
import org.jfeild1337.credsmgr.db.CredentialsDBManager;
import org.jfeild1337.credsmgr.ui.CredentialsManagerMainUIHandler;
import org.jfeild1337.credsmgr.ui.helpers.ColorGenerator;
import org.jfeild1337.credsmgr.ui.helpers.CredsUtils;

/**
 * Provides control flow for the entire application
 *
 * @author Julian
 *
 */
public class CredentialsManagerMain {

    //constants
    private final String CONFIG_FILE = "data/config/config.cfg";
    private final String COLOR_FILE = "data/config/color.cfg";
    private final String LOG_CONFIG_FILE = "data/config/logger.cfg";

    private final String INITIALIZED_FILE = "initial_setup.isf";
    private final String COMMENT_DELIM = "#";

    private ImageIcon img_success = null; //new ImageIcon(getClass().getClassLoader().getResource("img/check_green.gif"));

    //config file params
    private final String DATABASE_NAME = "DATABASE_NAME";
    private String mDBName;
    private final String DATABASE_LOCATION = "DATABASE_LOCATION";
    private String mDBLocation;
    private String mDBInitFile;

    private final String LOOK_AND_FEEL = "LOOK_AND_FEEL";
    private final String NIMBUS = "NIMBUS";
    private final String SYSTEM = "SYSTEM";

    //member vars
    private CredentialsManagerMainUIHandler mMainUI;
    private CredentialsDBManager mDBManager;
    private String mSecretKey; //master password. Will be base64-encoded
    private ConfigManager mCfgMgr;
    private ConfigManager mColorConfigManager;

    /**
     * Constructor
     */
    public CredentialsManagerMain() {
        img_success = CredsUtils.getResourceImageAsIcon("check_green.gif"); 
        //get custom database locations
        try {
            mCfgMgr = new ConfigManager(CONFIG_FILE, "=", COMMENT_DELIM);
            setUpConfigValues();
        } catch (IOException e) {
            CredsUtils.showErrorPopup("Cannot open config file: " + e.getMessage(), "Error");
            System.exit(255);
        }
        //get any custom colors
        try {
            mColorConfigManager = new ConfigManager(COLOR_FILE, "=", COMMENT_DELIM);
        } catch (IOException e) {
            //we really don't care if the color file could be read or not..not something we should quit for
            CredsUtils.showErrorPopup("Cannot open color config file: " + e.getMessage(), "Error");
            mColorConfigManager = null;
        }
        //do everything
        init();
    }

    /**
     * Sets up the config values
     */
    private void setUpConfigValues() {
        mCfgMgr.checkConfigSettingString(DATABASE_LOCATION, CredentialsDBManager.DB_DIR);
        mCfgMgr.checkConfigSettingString(DATABASE_NAME, CredentialsDBManager.DB_NAME);

        //now set the corresponding member vars:
        mDBLocation = mCfgMgr.getConfigSettingString(DATABASE_LOCATION);
        mDBName = mCfgMgr.getConfigSettingString(DATABASE_NAME);
        mDBInitFile = mDBLocation + "/" + INITIALIZED_FILE;

    }

    /**
     * Initializes everything
     */
    private void init() {
        setUpColors();
        mDBManager = new CredentialsDBManager(mDBLocation, mDBName);
        //Create database if necessary 
        if (!checkIfInitialSetupDone()) {
            showFirstTimeSetupNotification();
            runSetupDatabaseWizard();
            showSetupSuccess();
        }
        try {
            userLogin();
        } catch (Exception ex) {
            System.out.println("Fatal error - exiting: " + ex.getMessage());
            System.exit(255);
        }
        showMainUI();
    }

    private void showSetupSuccess() {
        String text = "Setup Complete!<br>"
                + "Now you will need to enter the password you<br>"
                + "just created to log in to the app.";
        CredsUtils.showPopup("Setup Complete", text, img_success);
    }

    private void showFirstTimeSetupNotification() {
        String text = "This is the first time you're running the app,<br>"
                + "so we need to set up a Master Password. You will<br>"
                + "be asked to create and then confirm your new password<br>"
                + "on the next two screens.<br>"
                + "<b>NOTE: Please make sure you memorize this password,<br>"
                + "because it will literally be the 'key' to all your stored<br>"
                + "passwords and, if you forget it, there is nothing that can be<br>"
                + "done to recover it.</b>";
        CredsUtils.showPopup("First Time Setup", text, null);
    }

    /**
     * Checks if the initial setup has been done.
     *
     * @return true if setup has been done, false otherwise
     */
    private boolean checkIfInitialSetupDone() {
        File initialSetpDataFile = new File(mDBInitFile);
        if (initialSetpDataFile.exists()) {
            return true;
        }
        return false;
    }

    /**
     * Prompts user to create master password. Creates the database and
     * populates default values. Does not check if database already exists.
     * Assumes mDBManager has been initialized.
     */
    private void runSetupDatabaseWizard() {
        String password = CredsUtils.createMasterPassword(true);
        try {
            mDBManager.createDatabase();
            //Store new master password
            byte[] masterSalt = CryptoUtils.generateRandomSalt(32);
            String masterSaltStr = CryptoUtils.byteArrayToString(masterSalt);
            String hashedMasterPwd = CryptoUtils.basicOneWayHash(password, masterSaltStr);
            mDBManager.connectToDB();
            mDBManager.insertMasterKey(hashedMasterPwd, masterSaltStr);
            mDBManager.disconnectFromDB();
            try {
                createFlagFile();
            } catch (Exception ex2) {
            }
        } catch (SQLException sqlEx) {
            System.out.println("Error creating database: " + sqlEx.getMessage());
            System.exit(255);
        }

    }

    /**
     * Prompts user for password. Keeps propmting until user quits or gets it
     * right. Sets mSecretKEy to base-64 encoded password upon successful
     * authentication
     *
     * @throws Exception
     * @throws SQLException
     */
    private void userLogin() throws Exception {
        String userPwd = "PASS1";
        String[] masterKeyData = null;
        try {
            mDBManager.connectToDB();
            masterKeyData = mDBManager.getMasterKey();
            mDBManager.disconnectFromDB();
            //System.out.println("FROM DATABASE: MASTERKEY = " + masterKeyData[0] );
        } catch (SQLException e) {
            throw new Exception("SQL Error @ Login - cannot connect to database: " + e.getMessage());
        }
        String realMasterKey = masterKeyData[0];
        String salt = masterKeyData[1];

        boolean doPasswordsMatch = false;

        JPanel passwdPanel = new JPanel();
        passwdPanel.setMinimumSize(new Dimension(CredsUtils.PASSWORD_PROMPT_SIZE));
        JLabel label = new JLabel("Enter password:");
        JPasswordField pass = new JPasswordField(CredsUtils.PASSWD_FIELD_LEN);
        pass.setMinimumSize(CredsUtils.PASSWORD_LABEL_SIZE);
        passwdPanel.add(label);
        passwdPanel.add(pass);
        String[] options = new String[]{"OK", "Cancel"};
        do {
            pass.setText("");
            int option = JOptionPane.showOptionDialog(null, passwdPanel, "Login", JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (option == 0) // pressing OK button
            {
                char[] password = pass.getPassword();
                userPwd = new String(password);
                //System.out.println("DEBUG - Your password is: " + userPwd);
            } else {
                System.exit(99);
            }
            String userPwdHash = CryptoUtils.basicOneWayHash(userPwd, salt);
            //System.out.println("USER PASSWORD HASH = " + userPwdHash);
            doPasswordsMatch = userPwdHash.equals(realMasterKey);
        } while (!doPasswordsMatch);
        mSecretKey = Base64.encodeBase64String(userPwd.getBytes());
        //System.out.println("LOGIN: PASSWORD BASE 64: " + mSecretKey);
    }

    /**
     * Sets up and displays the main UI
     */
    private void showMainUI() {
        mMainUI = new CredentialsManagerMainUIHandler(mDBManager, mSecretKey);
    }

    /**
     * Creates the flag file that tells if the database has been set up properly
     *
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    private void createFlagFile() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(mDBInitFile, "UTF-8");
        Date now = new Date();
        writer.println("***********DO NOT DELETE THIS FILE***************");
        writer.println("* IF you delete this file, you will not be able to access your data anymore");
        writer.println("[INFO] INITIAL SETUP BEGUN " + now.toString());
        writer.close();
    }

    /**
     * Sets up custom color configs
     */
    private void setUpColors() {
        ArrayList<String> looknfeels = new ArrayList<>();
        looknfeels.add(NIMBUS);
        looknfeels.add(SYSTEM);
        if (mColorConfigManager != null) {
            //just blindly set UI manager settings with stuff the user has inputted. If there are errors we just ignore them
            for (String key : mColorConfigManager.getAllKeys()) {
                if (!key.equals(LOOK_AND_FEEL)) {   //we DO NOT want to add this one...                    
                    try {
                        UIManager.put(key, new Color(Integer.parseInt(mColorConfigManager.getConfigSettingString(key), 16)));
                    } catch (Exception e) {
                        System.out.println("ERROR ADDING COLOR KEY; reason: " + e.getMessage());
                    }
                }
            }

            //set up look and feel last
            mColorConfigManager.checkConfigSettingString(LOOK_AND_FEEL, NIMBUS, looknfeels);
            String looknfeel = mColorConfigManager.getConfigSettingString(LOOK_AND_FEEL);
            switch (looknfeel) {
                case NIMBUS:
                    setUpNimbusLookAndFeel();
                    break;
                case SYSTEM:
                    setUpSystemLookAndFeel();
                    break;
            }
        } else {
            //default is nimbus
            setUpNimbusLookAndFeel();
        }
    }

    /**
     * Sets up the NIMBUS look and feel
     */
    private void setUpNimbusLookAndFeel() {
        //UIManager.put("nimbusBase", ColorGenerator.getBelizeHole());
        //UIManager.put("nimbusBlueGrey", ColorGenerator.getBelizeHole());
        //UIManager.put("control", ColorGenerator.getSilver());		
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
