package karaoke.shared;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class myTextfield extends JTextField {
	private static final long serialVersionUID = 1L;

	private int idx = 110;
	private Color color;
	private int line_idx = 0;

	public myTextfield(String ug, int idx,int line_idx, Color color) {
		this.idx = idx;
		this.color = color;
		this.line_idx = line_idx;
		setText(ug);
		setBorder(BorderFactory.createEmptyBorder());
		setOpaque(false);
		setFont(new Font("", Font.BOLD, 18)); //Mongolian Baiti
		Cursor cursor = getCursor();
		setCaretColor(Color.white);
		setForeground(color);
		setEditable(false);
	}

	public myTextfield(String ug) {
		setText(ug);
		setFont(new Font("", Font.BOLD, 18)); //Mongolian Baiti
		setBorder(BorderFactory.createEmptyBorder());
		setOpaque(false);
		setForeground(Color.white);
		setCaretColor(Color.white);
		
	}
	public myTextfield() {
	}
	
	public myTextfield(boolean end) {
		setBorder(BorderFactory.createEmptyBorder());
		setOpaque(false);
		setEditable(false);
	}
	
	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getLine_idx() {
		return line_idx;
	}

	public void setLine_idx(int line_idx) {
		this.line_idx = line_idx;
	}
	
	public String getTexts() {
		if(getText().length() > 0)
			return getText();
		else
			return " ";
	}

}
