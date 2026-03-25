package karaoke.shared;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

public class MyLabel extends JLabel {
	private static final long serialVersionUID = 1L;

	private Font font;
	public MyLabel(String name, int loaction) {
		super(name, loaction);
		font = new  Font("Times New Roamn", Font.PLAIN, 15);
		setFont(font);
		setForeground(Color.white);
	}
	public MyLabel() {
		font = new  Font("Times New Roamn", Font.PLAIN, 15);
		setFont(font);
		setForeground(Color.lightGray);
	}
	public MyLabel(int i) {
		if(i == 1) {
			font = new  Font("Times New Roamn", Font.BOLD, 14);
			setFont(font);
			setForeground(Color.red);
		}else if(i ==2) {
			font = new  Font("Times New Roamn", Font.BOLD, 14);
			setFont(font);
			setForeground(Color.black);
		}
		
	}
	
	public MyLabel(String name, int loaction, Color color, int size) {
		super(name, loaction);
		font = new  Font("Times New Roamn", Font.PLAIN, size);
		setFont(font);
		setForeground(color);
	}
	


}
