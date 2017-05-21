package org.jfeild1337.credsmgr.ui;

/**
 * This is the main UI for the user to select a domain and view/edit the stored
 * username/password and other misc info. The logic for this UI is handled in
 * the CredentialsManagerMainUILogic class, both for the sake of readability and
 * because the UI layout in this class is mainly managed through the
 * WindowBuilder.
 *
 * NOTE: The functionality to edit the domain names not possible with the
 * current setup, so the buttoin is hidden.
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import org.jfeild1337.credsmgr.ui.helpers.CredsUtils;
import org.jfeild1337.thirdpartyutils.SortedComboBoxModel;
import org.jfeild1337.thirdpartyutils.IgnoreCaseStringComparator;

public class CredentialsManagerMainUI extends javax.swing.JFrame {

    private JPanel mLytMain;
    private JPanel mLytMainBtns;
    private JComboBox<String> mCmbBoxDomainSelector;
    private JScrollPane mTextViewScrollPane;
    private JTextPane mTextViewerPane;

    //--------------------------------------------------------------
    // CONSTANTS
    //--------------------------------------------------------------
    public static final int MAX_HORIZ_DIM = 5760;
    public static final int MAX_VERT_DIM = 2560;
    public static final int MAX_HEIGHT_BTN_LRG = 45;
    public static final int MAX_HEIGHT_BTN_SM = 35;
    private static final int UN_PWD_SECTION_HGT = 150;
    private static final int WINDOW_WIDTH = 506;
    private static final int WINDOW_HEIGHT = 500;
    private static final String TITLE = "Credentials Manager v" + CredsUtils.STR_VERSION_MAJOR + "." + CredsUtils.STR_VERSION_MINOR;
    private JLabel lblUsername;
    private JTextField textFieldUsername;
    private JLabel lblPassword;
    private JTextField textFieldPassword;
    private JLabel lblSelectDomain;
    private JButton btnAddNewDomain;

    private JButton btnDeleteDomain;
    private JMenuBar menuTop;
    private JMenu mMenuItemFileActions;
    private JLabel lblOtherInfo;
    private Component spcDomainSelectorAndButtons;
    private JPanel lytSaveResetBtns;
    private JButton btnSave;
    private JButton btnReset;
    private Component spcSaveResetBtns;
    private Component spcAddEditDomains;
    private JButton btnCopyPassword;
    private JButton btnCopyUsername;
    private JButton btnCopyOtherInfo;
    private JMenuItem mntmImportFromFile;
    private JMenuItem mntmExportDatabase;
    private JMenu mnHelp;
    private JMenuItem mntmHelpAbout;
    private JButton btnEditDomain;
    private Component spcNewDeleteDomain;
    private JLabel lblSaved;
    private Component horizontalGlue_1;
    private Component horizontalGlue_2;
    private JLabel lblDomain;
    private JTextField textFieldDomainEdit;
    private JButton btnCopyDomainToClipboard;
    private JMenuItem mntmChangeMasterPassword;

    private JPanel lytGridLblAndTxtFld;
    private Component spcOtherInfoAndSaveBtn;

    /**
     * Launch the application. For testing the UI.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    CredentialsManagerMainUI frame = new CredentialsManagerMainUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     *
     * @throws IOException
     */
    public CredentialsManagerMainUI() {
        setMinimumSize(new Dimension(510, 500));

        setIconImage(CredsUtils.getResourceImageAsIcon(CredsUtils.ICON_LOCK_OPEN_SMALL_FNAME).getImage());

        setTitle(TITLE);
        //setUpNimbusLookAndFeel();
        //setUpSystemLookAndFeel();

        setBackground(new Color(220, 220, 220));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 546, 539);

        menuTop = new JMenuBar();
        menuTop.setBackground(SystemColor.menu);
        menuTop.setForeground(SystemColor.activeCaptionBorder);
        setJMenuBar(menuTop);

        mMenuItemFileActions = new JMenu("File");
        mMenuItemFileActions.setMnemonic('F');
        menuTop.add(mMenuItemFileActions);

        mntmImportFromFile = new JMenuItem("Import From File");
        mntmImportFromFile.setToolTipText("Import info from file (file must be formatted properly, see user guide for details)");
        mMenuItemFileActions.add(mntmImportFromFile);

        mntmExportDatabase = new JMenuItem("Export Database To File");
        mntmExportDatabase.setToolTipText("Dump all database contents into plain-text file which is also readable by this application");
        mMenuItemFileActions.add(mntmExportDatabase);

        mntmChangeMasterPassword = new JMenuItem("Change Master Password");
        mMenuItemFileActions.add(mntmChangeMasterPassword);

        mnHelp = new JMenu("Help");
        menuTop.add(mnHelp);

        mntmHelpAbout = new JMenuItem("About");
        mnHelp.add(mntmHelpAbout);
        mLytMain = new JPanel();
        mLytMain.setBackground(UIManager.getColor("Button.background"));
        mLytMain.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(mLytMain);
        mLytMain.setLayout(new BoxLayout(mLytMain, BoxLayout.Y_AXIS));

        mLytMainBtns = new JPanel();
        mLytMainBtns.setBorder(null);
        mLytMain.add(mLytMainBtns);
        mLytMainBtns.setLayout(new BoxLayout(mLytMainBtns, BoxLayout.X_AXIS));

        lblSelectDomain = new JLabel(" ");
        mLytMainBtns.add(lblSelectDomain);

        //custom code to make the combo box sortable...
        SortedComboBoxModel<String> model = new SortedComboBoxModel<String>(new IgnoreCaseStringComparator());
        mCmbBoxDomainSelector = new JComboBox<String>(model);
        mCmbBoxDomainSelector.setMaximumSize(new Dimension(MAX_HORIZ_DIM, MAX_HEIGHT_BTN_LRG));
        mLytMainBtns.add(mCmbBoxDomainSelector);
        mCmbBoxDomainSelector.setToolTipText("Select a Domain");

        spcDomainSelectorAndButtons = Box.createHorizontalStrut(20);
        spcDomainSelectorAndButtons.setPreferredSize(new Dimension(5, 0));
        spcDomainSelectorAndButtons.setMinimumSize(new Dimension(1, 0));
        spcDomainSelectorAndButtons.setMaximumSize(new Dimension(10, 0));
        mLytMainBtns.add(spcDomainSelectorAndButtons);

        spcAddEditDomains = Box.createHorizontalStrut(20);
        spcAddEditDomains.setPreferredSize(new Dimension(3, 0));
        spcAddEditDomains.setMinimumSize(new Dimension(3, 0));
        spcAddEditDomains.setMaximumSize(new Dimension(3, 0));
        mLytMainBtns.add(spcAddEditDomains);

        btnAddNewDomain = new JButton("New");
        btnAddNewDomain.setToolTipText("Create new domain");
        mLytMainBtns.add(btnAddNewDomain);

        spcNewDeleteDomain = Box.createHorizontalStrut(20);
        spcNewDeleteDomain.setPreferredSize(new Dimension(3, 0));
        spcNewDeleteDomain.setMinimumSize(new Dimension(3, 0));
        spcNewDeleteDomain.setMaximumSize(new Dimension(3, 0));
        mLytMainBtns.add(spcNewDeleteDomain);

        btnDeleteDomain = new JButton("Delete");
        btnDeleteDomain.setToolTipText("Delete selected domain");
        mLytMainBtns.add(btnDeleteDomain);

        //space between main buttons and text pane
        Component btnAndTextPaneSpace = Box.createVerticalStrut(20);
        mLytMain.add(btnAndTextPaneSpace);

        lytGridLblAndTxtFld = new JPanel();
        lytGridLblAndTxtFld.setMaximumSize(new Dimension(32767, 275));
        mLytMain.add(lytGridLblAndTxtFld);
        GridBagLayout gbl_lytGridLblAndTxtFld = new GridBagLayout();
        gbl_lytGridLblAndTxtFld.columnWidths = new int[]{0, 0, 0};
        gbl_lytGridLblAndTxtFld.rowHeights = new int[]{0, 0, 0, 0, 0};
        gbl_lytGridLblAndTxtFld.columnWeights = new double[]{0.0, 1.0, 0.0};
        gbl_lytGridLblAndTxtFld.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        lytGridLblAndTxtFld.setLayout(gbl_lytGridLblAndTxtFld);

        lblDomain = new JLabel("DOMAIN:");
        lblDomain.setToolTipText("Edit the domain name (this will change the name in the drop-down)");
        GridBagConstraints gbc_lblDomain = new GridBagConstraints();
        gbc_lblDomain.anchor = GridBagConstraints.WEST;
        gbc_lblDomain.insets = new Insets(0, 0, 5, 5);
        gbc_lblDomain.gridx = 0;
        gbc_lblDomain.gridy = 0;
        lytGridLblAndTxtFld.add(lblDomain, gbc_lblDomain);

        textFieldDomainEdit = new JTextField();
        GridBagConstraints gbc_textFieldDomainEdit = new GridBagConstraints();
        gbc_textFieldDomainEdit.fill = GridBagConstraints.HORIZONTAL;
        gbc_textFieldDomainEdit.insets = new Insets(0, 0, 5, 5);
        gbc_textFieldDomainEdit.gridx = 1;
        gbc_textFieldDomainEdit.gridy = 0;
        lytGridLblAndTxtFld.add(textFieldDomainEdit, gbc_textFieldDomainEdit);
        textFieldDomainEdit.setColumns(10);

        btnCopyDomainToClipboard = new JButton("");
        GridBagConstraints gbc_btnCopyDomainToClipboard = new GridBagConstraints();
        gbc_btnCopyDomainToClipboard.insets = new Insets(0, 0, 5, 0);
        gbc_btnCopyDomainToClipboard.gridx = 2;
        gbc_btnCopyDomainToClipboard.gridy = 0;
        lytGridLblAndTxtFld.add(btnCopyDomainToClipboard, gbc_btnCopyDomainToClipboard);
        btnCopyDomainToClipboard.setToolTipText("Copy domain name to clipboard");
        btnCopyDomainToClipboard.setBorder(new EmptyBorder(2, 2, 2, 2));
        btnCopyDomainToClipboard.setIcon(CredsUtils.getResourceImageAsIcon(CredsUtils.ICON_CLIPBOARD_FNAME));
        btnCopyDomainToClipboard.setContentAreaFilled(false);

        lblUsername = new JLabel("USERNAME:");
        GridBagConstraints gbc_lblUsername = new GridBagConstraints();
        gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
        gbc_lblUsername.gridx = 0;
        gbc_lblUsername.gridy = 1;
        lytGridLblAndTxtFld.add(lblUsername, gbc_lblUsername);

        textFieldUsername = new JTextField();
        GridBagConstraints gbc_textFieldUsername = new GridBagConstraints();
        gbc_textFieldUsername.fill = GridBagConstraints.HORIZONTAL;
        gbc_textFieldUsername.insets = new Insets(0, 0, 5, 5);
        gbc_textFieldUsername.gridx = 1;
        gbc_textFieldUsername.gridy = 1;
        lytGridLblAndTxtFld.add(textFieldUsername, gbc_textFieldUsername);
        textFieldUsername.setColumns(10);

        btnCopyUsername = new JButton("");
        GridBagConstraints gbc_btnCopyUsername = new GridBagConstraints();
        gbc_btnCopyUsername.insets = new Insets(0, 0, 5, 0);
        gbc_btnCopyUsername.gridx = 2;
        gbc_btnCopyUsername.gridy = 1;
        lytGridLblAndTxtFld.add(btnCopyUsername, gbc_btnCopyUsername);
        btnCopyUsername.setToolTipText("Copy username to clipboard");
        btnCopyUsername.setBorder(new EmptyBorder(2, 2, 2, 2));
        btnCopyUsername.setIcon(CredsUtils.getResourceImageAsIcon(CredsUtils.ICON_CLIPBOARD_FNAME));
        btnCopyUsername.setContentAreaFilled(false);

        lblPassword = new JLabel("PASSWORD:");
        GridBagConstraints gbc_lblPassword = new GridBagConstraints();
        gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
        gbc_lblPassword.gridx = 0;
        gbc_lblPassword.gridy = 2;
        lytGridLblAndTxtFld.add(lblPassword, gbc_lblPassword);
        lblPassword.setMinimumSize(new Dimension(15, 14));

        textFieldPassword = new JTextField();
        GridBagConstraints gbc_textFieldPassword = new GridBagConstraints();
        gbc_textFieldPassword.fill = GridBagConstraints.HORIZONTAL;
        gbc_textFieldPassword.insets = new Insets(0, 0, 5, 5);
        gbc_textFieldPassword.gridx = 1;
        gbc_textFieldPassword.gridy = 2;
        lytGridLblAndTxtFld.add(textFieldPassword, gbc_textFieldPassword);
        textFieldPassword.setColumns(10);

        btnCopyPassword = new JButton("");
        GridBagConstraints gbc_btnCopyPassword = new GridBagConstraints();
        gbc_btnCopyPassword.insets = new Insets(0, 0, 5, 0);
        gbc_btnCopyPassword.gridx = 2;
        gbc_btnCopyPassword.gridy = 2;
        lytGridLblAndTxtFld.add(btnCopyPassword, gbc_btnCopyPassword);
        btnCopyPassword.setToolTipText("Copy password to clipboard");
        btnCopyPassword.setBorder(new EmptyBorder(2, 2, 2, 2));
        btnCopyPassword.setIcon(CredsUtils.getResourceImageAsIcon(CredsUtils.ICON_CLIPBOARD_FNAME));
        btnCopyPassword.setContentAreaFilled(false);

        lblOtherInfo = new JLabel("OTHER INFO");
        GridBagConstraints gbc_lblOtherInfo = new GridBagConstraints();
        gbc_lblOtherInfo.insets = new Insets(0, 0, 0, 5);
        gbc_lblOtherInfo.gridx = 0;
        gbc_lblOtherInfo.gridy = 3;
        lytGridLblAndTxtFld.add(lblOtherInfo, gbc_lblOtherInfo);

        btnCopyOtherInfo = new JButton("");
        GridBagConstraints gbc_btnCopyOtherInfo = new GridBagConstraints();
        gbc_btnCopyOtherInfo.gridx = 2;
        gbc_btnCopyOtherInfo.gridy = 3;
        lytGridLblAndTxtFld.add(btnCopyOtherInfo, gbc_btnCopyOtherInfo);
        btnCopyOtherInfo.setToolTipText("Copy \"Other Info\" to clipboard");
        btnCopyOtherInfo.setBorder(new EmptyBorder(2, 2, 2, 2));
        btnCopyOtherInfo.setIcon(CredsUtils.getResourceImageAsIcon(CredsUtils.ICON_CLIPBOARD_FNAME));
        btnCopyOtherInfo.setContentAreaFilled(false);

        mTextViewScrollPane = new JScrollPane();
        mLytMain.add(mTextViewScrollPane);
        mTextViewScrollPane.setPreferredSize(new Dimension(514, 283));

        mTextViewerPane = new JTextPane();
        mTextViewerPane.setSelectionColor(new Color(70, 130, 180));
        mTextViewScrollPane.setViewportView(mTextViewerPane);

        spcOtherInfoAndSaveBtn = Box.createVerticalStrut(20);
        spcOtherInfoAndSaveBtn.setPreferredSize(new Dimension(0, 5));
        spcOtherInfoAndSaveBtn.setMinimumSize(new Dimension(0, 5));
        spcOtherInfoAndSaveBtn.setMaximumSize(new Dimension(0, 5));
        mLytMain.add(spcOtherInfoAndSaveBtn);

        lytSaveResetBtns = new JPanel();
        lytSaveResetBtns.setOpaque(false);
        mLytMain.add(lytSaveResetBtns);
        lytSaveResetBtns.setLayout(new BoxLayout(lytSaveResetBtns, BoxLayout.X_AXIS));
        lytSaveResetBtns.setMaximumSize(new Dimension(MAX_HORIZ_DIM, UN_PWD_SECTION_HGT));

        lblSaved = new JLabel("SAVED");
        lblSaved.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblSaved.setForeground(new Color(60, 179, 113));
        lytSaveResetBtns.add(lblSaved);
        lblSaved.setVisible(false);

        horizontalGlue_1 = Box.createHorizontalGlue();
        lytSaveResetBtns.add(horizontalGlue_1);

        btnSave = new JButton("Save");
        lytSaveResetBtns.add(btnSave);

        spcSaveResetBtns = Box.createHorizontalStrut(20);
        spcSaveResetBtns.setPreferredSize(new Dimension(5, 0));
        spcSaveResetBtns.setMinimumSize(new Dimension(5, 0));
        spcSaveResetBtns.setMaximumSize(new Dimension(5, 0));
        lytSaveResetBtns.add(spcSaveResetBtns);

        btnReset = new JButton("Reset");
        lytSaveResetBtns.add(btnReset);

        horizontalGlue_2 = Box.createHorizontalGlue();
        lytSaveResetBtns.add(horizontalGlue_2);
    }
    
    //AUTO_GENERATED GETTERS AND SETTERS
    public JMenuItem getMntmHelpAbout() {
        return mntmHelpAbout;
    }

    public JButton getBtnSave() {
        return btnSave;
    }

    public void setBtnSave(JButton btnSave) {
        this.btnSave = btnSave;
    }

    public JButton getBtnReset() {
        return btnReset;
    }

    public void setBtnReset(JButton btnReset) {
        this.btnReset = btnReset;
    }

    public JTextPane getmTextViewerPane() {
        return mTextViewerPane;
    }

    public void setmTextViewerPane(JTextPane mTextViewerPane) {
        this.mTextViewerPane = mTextViewerPane;
    }

    public JButton getBtnCopyPassword() {
        return btnCopyPassword;
    }

    public JButton getBtnCopyUsername() {
        return btnCopyUsername;
    }

    public JButton getBtnCopyOtherInfo() {
        return btnCopyOtherInfo;
    }

    public JComboBox getmCmbBoxDomainSelector() {
        return mCmbBoxDomainSelector;
    }

    public JTextField getTextFieldUsername() {
        return textFieldUsername;
    }

    public JTextField getTextFieldPassword() {
        return textFieldPassword;
    }

    public JButton getBtnAddNewDomain() {
        return btnAddNewDomain;
    }

    public JButton getBtnDeleteDomain() {
        return btnDeleteDomain;
    }

    public JMenuItem getMntmImportFromFile() {
        return mntmImportFromFile;
    }

    public JMenuItem getMntmExportDatabase() {
        return mntmExportDatabase;
    }

    public JButton getBtnEditDomain() {
        return btnEditDomain;
    }

    public JLabel getLblSaved() {
        return lblSaved;
    }

    public JTextField getTextFieldDomainEdit() {
        return textFieldDomainEdit;
    }

    public JButton getBtnCopyDomainToClipboard() {
        return btnCopyDomainToClipboard;
    }

    public JMenuItem getMntmChangeMasterPassword() {
        return mntmChangeMasterPassword;
    }

    //---------------------------------------------------------------------
    // HELPERS
    //---------------------------------------------------------------------
    /**
     * Shows or hides the "SAVED" label
     *
     * @param isVisible
     */
    public void setSavedLabelVisible(boolean isVisible) {
        lblSaved.setVisible(isVisible);
    }

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
