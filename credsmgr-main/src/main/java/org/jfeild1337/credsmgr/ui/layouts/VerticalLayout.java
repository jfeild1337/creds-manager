package org.jfeild1337.credsmgr.ui.layouts;

import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class VerticalLayout extends JPanel {

	private static final long serialVersionUID = 76251L;
	
	/**
	 * Default constructor. Creates a JPanel with a Box layout arranged vertically. 
	 */
	public VerticalLayout(){
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));		
	}
	
	/**
	 * Creates a vertical strut in the height (pixels) described by <code>amount</code>
	 * Similar to addSpace() but is probably safer to use.
	 * @param amount
	 */
	public void addStrut(int amount){
		add(Box.createVerticalStrut(amount));
	}
	
	/**
	 * Adds a vertical space with the specified amount.
	 * @param amount
	 */
	public void addSpace(int amount){
		add(Box.createRigidArea(new Dimension(0, amount)));	
	}
	
	/**
	 * Adds "Glue" to keep components spaced or to fill up empty space
	 * and keep components pushed out to the sides.
	 */
	public void addExpandingSeparator(){
		add(Box.createVerticalGlue());
	}
	
	/**
	 * Adds a separator line
	 */
	public void addSeparatorLine(){
		this.add(new JSeparator(SwingConstants.HORIZONTAL));
	}
	
	/**
	 * Adds a separator line with the specified amount of padding
	 * on either side
	 * 
	 * @param padding amount of padding to add before and after the line
	 */
	public void addDividerWithPadding(int padding){
		addSpace(padding);
		addSeparatorLine();
		addSpace(padding);
		
	}
}
