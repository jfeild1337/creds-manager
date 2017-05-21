package org.jfeild1337.credsmgr.ui.helpers;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;

import org.jfeild1337.credsmgr.ui.layouts.HorizontalLayout;
import org.jfeild1337.credsmgr.ui.layouts.VerticalLayout;

/**
 * CLASS for a popup confirmation dialog. Simply displays a message
 * and has an OK and a CANCEL button. An additional button can be shown
 * as well (the idea is something like SAVE, CANCEL, CONTINUE WITHOUT SAVING. 
 * 
 * @author Julian
 *
 */
public class ConfirmationDialog extends JFrame{
	
	private final int WINDOW_HEIGHT = 135;
	private final int WINDOW_WIDTH = 300;
	private final int PADDING = 10;
	private final int TXT_FIELD_WIDTH = 15;
	
	private String msg;
	private JLabel lblPrompt;	
	private JButton btnOK;
	private JButton btnCancel;
	private JButton btnContinue;
	private VerticalLayout lytMain;
	private HorizontalLayout lytBtns;
	
	/**
	 * Constructor. All fields are optional EXCEPT showThirdChoice; this MUST be set 
	 * 
	 * @param title window title
	 * @param txtPrompt message to be displayed
	 * @param width window width
	 * @param height window height
	 * @param icon path to image to be used as an icon
	 * @param showThirdChoice set TRUE to display 3rd button
	 */
	public ConfirmationDialog(String title, String txtPrompt, Integer width, Integer height, String icon, boolean showThirdChoice){
		super(title);	
		if(icon != null){
			try{
				ImageIcon img = new ImageIcon(icon);
				setIconImage(img.getImage());
			}catch(Exception e){ /*ignore it, we don't care*/ }
		}
		if(width != null){
			if(height != null){
				setSize(new Dimension(width, height));
			}else{
				setSize(new Dimension(width, WINDOW_HEIGHT));
			}
		}
		else{
			if(height != null){
				setSize(new Dimension(WINDOW_WIDTH, height));
			}
			else{
				setSize(new Dimension(WINDOW_WIDTH , WINDOW_HEIGHT));
			}
		}
		msg = (txtPrompt == null ) ? "Are you sure?" : txtPrompt;
		init(showThirdChoice);
	}
	
	/**
	 * Sets up the window.
	 */
	public void init(boolean showThirdChoice){
		setUpNimbusLookAndFeel();
		lytMain = new VerticalLayout();
		setContentPane(lytMain);		
		lytMain.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
		
		HorizontalLayout lytPrompt = new HorizontalLayout();
		lblPrompt = new JLabel(msg);
		lytPrompt.add(lblPrompt);
		lytPrompt.addExpandingSeparator();
		
		lytBtns = new HorizontalLayout();		
		btnOK = new JButton("OK");
		btnCancel = new JButton("Cancel");
		btnContinue = new JButton("Continue");
		lytBtns.add(Box.createHorizontalGlue());
		lytBtns.add(btnOK);
		lytBtns.add(btnCancel);
		if(showThirdChoice){
			lytBtns.add(btnContinue);
		}
		lytMain.add(lytBtns);
		
	}
	
	
	
	
	
	
	
	public JButton getBtnOK() {
		return btnOK;
	}
	public JButton getBtnCancel() {
		return btnCancel;
	}
	public JButton getBtnContinue() {
		return btnContinue;
	}

	/**
	 * Sets up a NIMBUS look and feel
	 */
	private void setUpNimbusLookAndFeel(){
//		 UIManager.put("nimbusBase", ColorGenerator.getBelizeHole());
//		 UIManager.put("nimbusBlueGrey", ColorGenerator.getBelizeHole());
//		 UIManager.put("control", ColorGenerator.getSilver());

		 try{
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
	 private void setUpSystemLookAndFeel(){
		 try {
				UIManager.setLookAndFeel(
						UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException
					| IllegalAccessException | UnsupportedLookAndFeelException e) {
				System.out.println(e.getClass() + "; " + e.getMessage());
			}
	 }
	

}
