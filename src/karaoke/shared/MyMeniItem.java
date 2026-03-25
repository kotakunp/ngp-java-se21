package karaoke.shared;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

public class MyMeniItem extends JMenuItem {
	private static final long serialVersionUID = 1L;

	public MyMeniItem(String name, int key, String command, ActionListener action) {
		super(name, key);
		setActionCommand(command);
		addActionListener(action);
	}
	
	public MyMeniItem(String name, ImageIcon icon,  int key, String command) {
		super(name, icon);
		setActionCommand(command);
	}
	
	public MyMeniItem(String name, int key, String command) {
		super(name);
		setActionCommand(command);
	}
	
	public MyMeniItem(String name, ImageIcon icon,  int key, String command, ActionListener action) {
		super(name, icon);
		setActionCommand(command);
		addActionListener(action);
	}

}
