package org.jfeild1337.credsmgr.ui.layouts;

import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class HorizontalLayout extends JPanel{
	
	private static final long serialVersionUID = 12799L;
	
	/**
	 * Default constructor. Creates a JPanel with a Box layout arranged horizontally. 
	 */
	public HorizontalLayout(){
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}

	/**
	 * Creates a horizontal strut in the width (pixels) described by <code>amount</code>
	 * Similar to addSpace() but is probably safer to use.
	 * @param amount
	 */
	public void addStrut(int amount){
		add(Box.createHorizontalStrut(amount));
	}
	
	/**
	 * Adds a horizontal space with the specified amount.
	 * @param amount
	 */
	public void addSpace(int amount){
		add(Box.createRigidArea(new Dimension(amount, 0)));	
	}
	
	/**
	 * Adds "Glue" to keep components spaced or to fill up empty space
	 * and keep compnents pushed out to the sides.
	 */
	public void addExpandingSeparator(){
		add(Box.createHorizontalGlue());
	}
	
	/**
	 * Adds a separator line
	 */
	public void addSeparatorLine(){
		this.add(new JSeparator(SwingConstants.VERTICAL));
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
