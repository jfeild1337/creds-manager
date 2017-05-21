package org.jfeild1337.credsmgr.ui.helpers;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

public class TextPopup extends JPanel {

	/**
	 * Create the panel.
	 */
	public TextPopup(String text) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JLabel lblContent = new JLabel("<html>" + text);
		add(lblContent);

	}

}
