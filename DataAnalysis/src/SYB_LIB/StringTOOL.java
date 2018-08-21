package SYB_LIB;

import java.awt.FontMetrics;
import java.awt.Graphics;

public class StringTOOL {
	
	public static int getStringWidth(Graphics g, String input) {
		// TODO Auto-generated method stub
		
		FontMetrics fontMetrics = g.getFontMetrics();
		
		
		return fontMetrics.stringWidth(input);
	}
	
	public static int getStringHight(Graphics g, String input) {
		// TODO Auto-generated method stub
		
		FontMetrics fontMetrics = g.getFontMetrics();
		
		return (int) fontMetrics.getStringBounds("input", g).getHeight();
	}
	


}
