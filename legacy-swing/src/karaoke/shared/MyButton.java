package karaoke.shared;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.Border;

public class MyButton extends JButton {
	private static final long serialVersionUID = 1L;

	public MyButton() {
		
	}

	public MyButton(String name) {
		super(name);
		setActionCommand(name);
		setFont(new Font("Times New Roman", Font.PLAIN, 25)); 
		setForeground(Color.white); 
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder());
	}
	
	public MyButton(String command, String imageDir, String name) {
		super( new ImageIcon(imageDir));
		setToolTipText(name);
		setActionCommand(command);
		setVerticalTextPosition(JButton.BOTTOM);
		setHorizontalTextPosition(JButton.CENTER);
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder());
	}
	
	public MyButton(String name, String action) {
		super(name);
		setActionCommand(action);
		setFont(new Font("Times New Roman", Font.PLAIN, 25)); 
//		setForeground(Color.white); 
//		setOpaque(false);
//		setBorder(BorderFactory.createEmptyBorder());
	}



}
