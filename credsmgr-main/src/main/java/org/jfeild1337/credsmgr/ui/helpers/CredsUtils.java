package org.jfeild1337.credsmgr.ui.helpers;

import java.awt.Dimension;
import java.text.MessageFormat;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;


public class CredsUtils {

    public static String STR_VERSION_MAJOR = "1";
    public static String STR_VERSION_MINOR = "1";
    public static String STR_VERSION_BUILD = "0";
    public static String STR_BUILDER_TAG = "BETA3";

    public static int PWD_BTN_OK = 0;
    public static int PWD_BTN_CANCEL = 1;
    public static int DEFAULT_SALT_LEN = 8;

    public static Dimension PASSWORD_LABEL_SIZE = new Dimension(100, 12);
    public static Dimension PASSWORD_PROMPT_SIZE = new Dimension(350, 150);
    public static final int PASSWD_FIELD_LEN = 20;
    public static final int PASSWD_MIN_LEN = 5;

    public static final String DEFAULT_USERNAME = "(Set Username)";
    public static final String DEFAULT_PASSWORD = "(Set Password)";
    public static final String DEFAULT_OTHER_INFO = "(Set Value)";

    public static final String IMG_RESRC_PATH = "img";
    
    /**
     * ENUM for domain fields
     *
     * @author Julian
     *
     */
    public enum DomainFields {
        DOMAIN_ID("DOMAIN_ID"),
        DOMAIN_NAME("DOMAIN_NAME"),
        USER_NAME("USERNAME"),
        PASSWORD("PASSWORD"),
        OTHER_INFO("OTHER_INFO"),
        SALT("SALT");

        private final String tblName;

        DomainFields(String name) {
            this.tblName = name;
        }

        public String getName() {
            return this.tblName;
        }
    }

    /**
     * Shows a popup to prompt for password.
     *
     * @return the password if the user clicked
     */
    public static String getPasswordPopup(String title, String promptLabel) {
        JPanel passwdPanel = new JPanel();
        JLabel label = new JLabel(promptLabel);
        JPasswordField pass = new JPasswordField();
        passwdPanel.add(label);
        passwdPanel.add(pass);

        String[] options = new String[]{"OK", "Cancel"};
        int option = JOptionPane.showOptionDialog(null, passwdPanel, title,
                JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);

        return null;
    }

    /**
     * Shows an error popup with the given title and content
     * 
     * @param msg
     * @param title 
     */
    public static void showErrorPopup(String msg, String title) {        
        JOptionPane.showMessageDialog(null, msg, title, JOptionPane.ERROR_MESSAGE, getResourceImageAsIcon("icon-error.png"));
    }

    /**
     * Returns the specified image file as an ImageIcon. 
     * 
     * @param imageFileName - image file name, including extension (i.e., 
     *        check_green.gif). The image file must be
     *        present in the resources/img directory of the source project
     * @return ImageIcon of the specified image file
     */
    public static ImageIcon getResourceImageAsIcon(String imageFileName)
    {
        String resourcePath = MessageFormat.format("{0}/{1}", IMG_RESRC_PATH, imageFileName);
        return new ImageIcon(CredsUtils.class.getClassLoader().getResource(resourcePath));
    }
    
    /**
     * Prompts the user to enter a master password, and returns the plain-text
     * password
     *
     * @return
     */
    public static String createMasterPassword(boolean exitOnCancel) {
        String password1 = "PASS1";
        String password2 = "PASS2";
        boolean doPasswordsMatch = false;

        //use a built-in prompt for the password
        JPanel passwdPanel = new JPanel();
        JLabel label = new JLabel("Enter password: (min 5 characters)");
        JPasswordField pass = new JPasswordField(PASSWD_FIELD_LEN);
        pass.setMinimumSize(PASSWORD_LABEL_SIZE);
        passwdPanel.add(label);
        passwdPanel.add(pass);
        passwdPanel.setMinimumSize(new Dimension(PASSWORD_PROMPT_SIZE));
        while (!doPasswordsMatch) {
            String[] options = new String[]{"OK", "Cancel"};
            int option = JOptionPane.showOptionDialog(null, passwdPanel, "Create New Password", JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (option == 0) // pressing OK button
            {
                char[] password = pass.getPassword();
                password1 = new String(password);
                System.out.println("DEBUG - Your password is: " + password1);
                if (password1.length() < PASSWD_MIN_LEN) {
                    showErrorPopup("Error: password must be at least " + PASSWD_MIN_LEN + " charcters.", "Password Too Short");
                    continue;
                }
            } else {
                if (exitOnCancel) {
                    System.exit(99);
                } else {
                    return null;
                }
            }
            //prompt to confirm password:
            pass.setText("");
            int option2 = JOptionPane.showOptionDialog(null, passwdPanel, "Confirm New Password", JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (option2 == 0) // pressing OK button
            {
                char[] password = pass.getPassword();
                password2 = new String(password);
                System.out.println("DEBUG - Confirm password is: " + password2);
            } else {
                if (exitOnCancel) {
                    System.exit(99);
                } else {
                    return null;
                }
            }
            doPasswordsMatch = password1.equals(password2);
        }
        return password1;
    }

    /**
     * displays a simple popup with the given text and an OK option. Text can be
     * HTML-formatted
     *
     * @param msg
     */
    public static int showPopup(String title, String msg, Icon icon) {
        TextPopup panel = new TextPopup(msg);
        String[] options = new String[]{"OK"};
        int option = JOptionPane.showOptionDialog(null, panel, title, JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, icon, options, options[0]);
        return option;
    }

    public static void main(String[] args) {        
        System.out.println(DomainFields.DOMAIN_ID.getName());
        //createNewDomainEntityFromDomainName("TESTING", "TEST NAME");
    }

}
