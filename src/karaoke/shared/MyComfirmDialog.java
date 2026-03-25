package karaoke.shared;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MyComfirmDialog  {
	private String header;
	private String message;
	private int input;
	public MyComfirmDialog(JFrame frame, String header, String message, int i) {
		if(i == 0) {
			ImageIcon icon = new ImageIcon("image/question.png");
			setInput(JOptionPane.showConfirmDialog(frame, message, header,
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon));
		}else  if(i == 1) {
			ImageIcon icon = new ImageIcon("image/question.png");
			JOptionPane.showMessageDialog(frame, message, header, JOptionPane.OK_OPTION, icon);
		}else  if(i == 2) {
			ImageIcon icon = new ImageIcon("image/question.png");
			setInput(JOptionPane.showConfirmDialog(frame, message, header,
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon));
		}
		
	}
	
	public int getInput() {
		return input;
	}
	public void setInput(int input) {
		this.input = input;
	}
	
	

}
