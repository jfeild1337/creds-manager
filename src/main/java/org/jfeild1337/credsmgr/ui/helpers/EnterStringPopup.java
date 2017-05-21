package org.jfeild1337.credsmgr.ui.helpers;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.JTextField;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Dimension;
import java.awt.BorderLayout;

public class EnterStringPopup extends JPanel {
	private JTextField txtFieldEnterValue;
	private JLabel lblErrorText;
	private JLabel lblPrompt;
	
	/**
	 * Create the panel.
	 */
	public EnterStringPopup(String strPrompt, String strErrorText) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel panel_1 = new JPanel();
		add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
		
		Component horizontalStrut_3 = Box.createHorizontalStrut(20);
		horizontalStrut_3.setPreferredSize(new Dimension(4, 0));
		horizontalStrut_3.setMinimumSize(new Dimension(4, 0));
		horizontalStrut_3.setMaximumSize(new Dimension(4, 0));
		panel_1.add(horizontalStrut_3);
		
		lblErrorText = new JLabel(strErrorText);
		panel_1.add(lblErrorText);
		lblErrorText.setMaximumSize(new Dimension(5760, 14));
		lblErrorText.setForeground(Color.RED);
		//not visible by default
		lblErrorText.setVisible(false);
		
		Component horizontalStrut_4 = Box.createHorizontalStrut(20);
		horizontalStrut_4.setPreferredSize(new Dimension(4, 0));
		horizontalStrut_4.setMinimumSize(new Dimension(4, 0));
		horizontalStrut_4.setMaximumSize(new Dimension(4, 0));
		panel_1.add(horizontalStrut_4);
		
		Component verticalStrut = Box.createVerticalStrut(20);
		verticalStrut.setMaximumSize(new Dimension(0, 5));
		verticalStrut.setMinimumSize(new Dimension(0, 5));
		verticalStrut.setPreferredSize(new Dimension(0, 5));
		add(verticalStrut);
		
		JPanel panel = new JPanel();
		panel.setMinimumSize(new Dimension(24, 10));
		add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		horizontalStrut_1.setPreferredSize(new Dimension(4, 0));
		horizontalStrut_1.setMinimumSize(new Dimension(4, 0));
		horizontalStrut_1.setMaximumSize(new Dimension(4, 0));
		panel.add(horizontalStrut_1);
		
		lblPrompt = new JLabel(strPrompt);
		panel.add(lblPrompt);
		lblPrompt.setMaximumSize(new Dimension(5760, 14));
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		horizontalStrut.setPreferredSize(new Dimension(5, 0));
		horizontalStrut.setMinimumSize(new Dimension(5, 0));
		horizontalStrut.setMaximumSize(new Dimension(5, 0));
		panel.add(horizontalStrut);
		
		txtFieldEnterValue = new JTextField();
		txtFieldEnterValue.setMinimumSize(new Dimension(60, 22));
		panel.add(txtFieldEnterValue);
		txtFieldEnterValue.setMaximumSize(new Dimension(2147483647, 22));
		txtFieldEnterValue.setColumns(10);
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		horizontalStrut_2.setPreferredSize(new Dimension(4, 0));
		horizontalStrut_2.setMinimumSize(new Dimension(4, 0));
		horizontalStrut_2.setMaximumSize(new Dimension(4, 0));
		panel.add(horizontalStrut_2);

	}

	public JTextField getTxtFieldEnterValue() {
		return txtFieldEnterValue;
	}

	public void setTxtFieldEnterValue(JTextField txtFieldEnterValue) {
		this.txtFieldEnterValue = txtFieldEnterValue;
	}

	public JLabel getLblErrorText() {
		return lblErrorText;
	}

	public void setLblErrorText(JLabel lblErrorText) {
		this.lblErrorText = lblErrorText;
	}

	public JLabel getLblPrompt() {
		return lblPrompt;
	}

	public void setLblPrompt(JLabel lblPrompt) {
		this.lblPrompt = lblPrompt;
	}

}
