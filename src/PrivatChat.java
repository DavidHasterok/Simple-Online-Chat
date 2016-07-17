import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Toolkit;
import java.awt.TextArea;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.*;
import java.util.Date;

public class PrivatChat extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
    private int privateChatIndex;
    private MainWindow mw;
    private TextArea textArea = new TextArea();
	/**
	 * Launch the application.
	 */
	public void open() {
				try {
					this.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
	}
	
			


	/**
	 * Create the frame.
	 */
	public PrivatChat() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    textArea.setEditable(false);
		textArea.setBackground(Color.WHITE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 442, 270);
		contentPane.add(panel);
		panel.setLayout(null);
		
		getTextArea().setBounds(10, 10, 422, 223);
		panel.add(getTextArea());
		
		textField = new JTextField();
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendPrivateMessage();
			}
		});
		textField.setBounds(10, 239, 224, 20);
		panel.add(textField);
		textField.setColumns(10);
		
		
		/////////////////////////////////
		// klicken auf button chat
		//
		////////////////////////////////
		JButton btnChat = new JButton("Chat");
		btnChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
             sendPrivateMessage();
			}
		});
		btnChat.setBounds(244, 238, 89, 23);
		panel.add(btnChat);
		
		// //////////////////////////////////////////////////////
		// /
		// / privatChat-fenster close
		// /
		// //////////////////////////////////////////////////////		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnClose.setBounds(343, 238, 89, 23);
		panel.add(btnClose);
	}

	public void sendPrivateMessage(){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String t = sdf.format(new Date());
	String text = textField.getText();
	while(!(text.equals("")) && text.contains("  ")){
		text = text.replace("  ", " ");
	}
	if(text.startsWith(" ")){text = text.substring(1);}
	
	if(!(text.equals(""))){
	text = mw.getUserName() + " [" + t + "]: " + text + "\n";
	
	if(mw.isJoinError() == true){JOptionPane.showMessageDialog(null, "Du bist gebannt.", "Fehler...", JOptionPane.OK_CANCEL_OPTION);}else{
	mw.getOut().println(5);
	mw.getOut().println(privateChatIndex);
	mw.getOut().println(text);
	}
	}
	textField.setText("");
	}
	
	public void setPrivateChatIndex(int privateChatIndex) {
		this.privateChatIndex = privateChatIndex;
	}

	public int getPrivateChatIndex() {
		return privateChatIndex;
	}

	public void setMw(MainWindow mw) {
		this.mw = mw;
	}

	public MainWindow getMw() {
		return mw;
	}

	public void setTextArea(TextArea textArea) {
		this.textArea = textArea;
	}

	public TextArea getTextArea() {
		return textArea;
	}
}
