package org.jfeild1337.credsmgr.ui.helpers;

import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import org.jfeild1337.credsmgr.ui.layouts.HorizontalLayout;
import org.jfeild1337.credsmgr.ui.layouts.VerticalLayout;

/**
 * GetNamePopup
 *
 * Simple window that has a text field and confirm and cancel buttons. What the
 * buttons will do is handled in whatever class instanciates this one.
 * 
 */
public class GetNamePopup extends JFrame {

    private static final long serialVersionUID = 55645L;

    private final int WINDOW_HEIGHT = 135;
    private final int WINDOW_WIDTH = 300;
    private final int PADDING = 10;
    private final int TXT_FIELD_WIDTH = 15;

    private String prompt;
    private JLabel lblPrompt;
    private JTextArea valueField;
    private JButton btnOK;
    private JButton btnCancel;

    public GetNamePopup() {
        super();
        prompt = "Enter a Value:";
        init();
    }

    public GetNamePopup(String title) {
        super(title);
        setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        prompt = "Enter a Value:";
        init();
    }

    public GetNamePopup(String title, int width, int height) {
        super(title);
        setSize(new Dimension(width, height));
        prompt = "Enter a Value:";
        init();
    }

    /**
     * Flexible constructor that allows you to set the parameters you want. You
     * can set as many or as few as you want-just set the unwanted ones to NULL
     *
     * @param title - the title for the window
     * @param txtPrompt - the text prompt
     * @param width - the window width
     * @param height - the window height
     * @param icon - path to the icon to use
     */
    public GetNamePopup(String title, String txtPrompt, Integer width, Integer height, String icon) {
        super(title);
        if (icon != null) {
            try {
                ImageIcon img = new ImageIcon(icon);
                setIconImage(img.getImage());
            } catch (Exception e) {
                /*ignore it, we don't care*/ }
        }
        if (width != null) {
            if (height != null) {
                setSize(new Dimension(width, height));
            } else {
                setSize(new Dimension(width, WINDOW_HEIGHT));
            }
        } else {
            if (height != null) {
                setSize(new Dimension(WINDOW_WIDTH, height));
            } else {
                setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
            }
        }
        prompt = (txtPrompt == null) ? "Enter a value:" : txtPrompt;
        init();
    }

    /**
     * Sets up the UI
     */
    public void init() {
        setUpNimbusLookAndFeel();
        VerticalLayout lytMain = new VerticalLayout();
        setContentPane(lytMain);
        lytMain.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));

        HorizontalLayout lytPrompt = new HorizontalLayout();
        lblPrompt = new JLabel(prompt);
        lytPrompt.add(lblPrompt);
        lytPrompt.addExpandingSeparator();

        valueField = new JTextArea(2, TXT_FIELD_WIDTH);
        lytMain.add(lytPrompt);
        lytMain.addSpace(5);
        lytMain.add(valueField);
        lytMain.addSpace(5);

        HorizontalLayout lytBtns = new HorizontalLayout();
        btnOK = new JButton("OK");
        btnCancel = new JButton("Cancel");
        lytBtns.add(Box.createHorizontalGlue());
        lytBtns.add(btnOK);
        lytBtns.add(btnCancel);
        lytMain.add(lytBtns);
    }

    /**
     * Returns the OK button so a listener can be assigned.
     *
     * @return the "OK" button
     */
    public JButton getBtnOK() {
        return btnOK;
    }

    /**
     * Returns the CANCEL button so a listener can be assigned.
     *
     * @return the "CANCEL" button
     */
    public JButton getBtnCancel() {
        return btnCancel;
    }

    /**
     * Gets the text field
     *
     * @return
     */
    public JTextArea getValueField() {
        return valueField;
    }

    /**
     * Returns the text contained in the text field.
     *
     * @return the text contained in the text field
     */
    public String getTextValue() {
        return valueField.getText();
    }

    /**
     * Sets up a NIMBUS look and feel
     */
    private void setUpNimbusLookAndFeel() {
//		 UIManager.put("nimbusBase", ColorGenerator.getBelizeHole());
//		 UIManager.put("nimbusBlueGrey", ColorGenerator.getBelizeHole());
//		 UIManager.put("control", ColorGenerator.getSilver());

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
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.out.println(e.getClass() + "; " + e.getMessage());
        }
    }

}
