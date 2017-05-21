package org.jfeild1337.credsmgr.ui.helpers;

import java.awt.Color;

/**
 * 
 * Provides static methods that simply return colors for the various elements of the
 * UI.
 * 
 * @author Julian
 *
 */
public class ColorGenerator {
	
	/**
	 * Returns the color used by the main layout
	 * @return
	 */
	public static Color getMainLytColor(){
		return new Color(189, 195, 199);//(216, 219, 224);
	}
	
	/**
	 * Returns the color used by the text fields
	 * @return
	 */
	public static Color getTxtFieldColor(){
		return new Color(224, 228, 235);
	}
	
	/**
	 * Returns the color used by the text fields
	 * @return
	 */
	public static Color getMenuLytColor(){
		return new Color(224, 228, 235);
	}
	
	public static Color getEditedColor(){
		return rgb(241, 196, 15);
	}
	
	//----------------------------------------------------
	// STOCK COLORS OBTAINED FROM FLATUICOLORS.COM
	//----------------------------------------------------
	private static Color turquoise = rgb(26, 188, 156);
	private static Color greenSea = rgb(22, 160, 133);
	private static Color sunFlower = rgb(241, 196, 15);
	private static Color orange = rgb(243, 156, 18);
	private static Color emerald = rgb(46, 204, 113);
	private static Color nephritis = rgb(39, 174, 96);
	private static Color carrot = rgb(230, 126, 34);
	private static Color pumpkin = rgb(211, 84, 0);
	private static Color peteRiver = rgb(52, 152, 219);
	private static Color belizeHole = rgb(41, 128, 185);
	private static Color alizarin = rgb(231, 76, 60);
	private static Color pomegranate = rgb(192, 57, 43);
	private static Color amethyst = rgb(155, 89, 182);
	private static Color wisteria = rgb(142, 68, 173);
	
	private static Color clouds = rgb(236, 240, 241);
	private static Color silver = rgb(189, 195, 199);
	private static Color wetAsphault = rgb(52, 73, 94);
	private static Color midnightBlue = rgb(44, 62, 80);
	private static Color concrete = rgb(149, 165, 166);
	private static Color asbestos = rgb(127, 140, 141);
	
	
	public static Color getTurquoise() {
		return rgb(26, 188, 156);
	}

	public static Color getGreenSea() {
		return rgb(22, 160, 133);
	}

	public static Color getSunFlower() {
		return rgb(241, 196, 15);
	}

	public static Color getOrange() {
		return rgb(243, 156, 18);
	}

	public static Color getEmerald() {
		return rgb(46, 204, 113);
	}

	public static Color getNephritis() {
		return rgb(39, 174, 96);
	}

	public static Color getCarrot() {
		return rgb(230, 126, 34);
	}

	public static Color getPumpkin() {
		return rgb(211, 84, 0);
	}

	public static Color getPeteRiver() {
		return rgb(52, 152, 219);
	}

	public static Color getBelizeHole() {
		return rgb(41, 128, 185);
	}

	public static Color getAlizarin() {
		return rgb(231, 76, 60);
	}

	public static Color getPomegranate() {
		return rgb(192, 57, 43);
	}

	public static Color getAmethyst() {
		return rgb(155, 89, 182);
	}

	public static Color getWisteria() {
		return rgb(142, 68, 173);
	}

	public static Color getClouds() {
		return rgb(236, 240, 241);
	}

	public static Color getSilver() {
		return rgb(189, 195, 199);
	}

	public static Color getWetAsphault() {
		return rgb(52, 73, 94);
	}

	public static Color getMidnightBlue() {
		return rgb(44, 62, 80);
	}

	public static Color getConcrete() {
		return rgb(149, 165, 166);
	}

	public static Color getAsbestos() {
		return rgb(127, 140, 141);
	}

	
	
	
	/**
	 * Returns a color object with the specified RGB.
	 * Flatuicolors.com pastes the RGB values as rgb(x, y,z)
	 * so a direct paste results in "new Colorrgb(x,y,z)".
	 * Hence this method...
	 */
	private static Color rgb(int r, int g, int b){
		return new Color(r, g, b);
	}

}
