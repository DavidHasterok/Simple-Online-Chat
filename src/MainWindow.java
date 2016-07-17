import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.TextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import java.awt.List;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JRadioButton;
import javax.swing.JPasswordField;
import java.awt.Font;
import java.awt.Color;
import java.awt.Toolkit;

import javax.swing.border.TitledBorder;

class MainWindow extends JFrame {
	private ClientThread ct = new ClientThread(this);
	private PrintWriter out;
	private List listRooms = new List();
	private TextArea textArea = new TextArea("", 30, 100, TextArea.SCROLLBARS_VERTICAL_ONLY);
	private List listUsers = new List();
	private JLabel raumNameLabel = new JLabel("Du bist im Raum \"Default\"");
	private JLabel labelConnection = new JLabel("Du bist verbunden mit Server");
	private JPanel contentPane;
	private JTextField textFieldName;
	private JTextField textFieldChat;
	private JPasswordField passwordField;
	private JPasswordField passwordField2;
	private boolean loginError;
	private String receivedMessage;
	private int numberOfRooms = -1;
	private int roomIndex = 1;
	private boolean joinError = false;
	private String text;
	private String serverName;
	private String userName;
	private Vector<PrivatChat> privatChats = new Vector<PrivatChat>(64);
	private boolean isConnected = false;
	/**
	 * Launch the application.
	 */

	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	@SuppressWarnings("deprecation")
	MainWindow() {
		setIconImage(Toolkit.getDefaultToolkit().getImage("src/Icons/Icons/chat.png"));
		setResizable(false);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				setSize(460, 230);

			}
		});
		setTitle("Client");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 730, 1018);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		final JPanel panelChat1 = new JPanel();
		panelChat1.setBounds(10, 339, 774, 600);
		contentPane.add(panelChat1);
		panelChat1.setLayout(null);

		JPanel panelStatus = new JPanel();
		panelStatus.setBorder(new TitledBorder(null, "Status", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelStatus.setBounds(547, 11, 217, 578);
		panelChat1.add(panelStatus);
		panelStatus.setLayout(null);

		JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane_1.setFont(new Font("Dialog", Font.PLAIN, 11));
		tabbedPane_1.setBounds(10, 21, 197, 512);
		tabbedPane_1.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		panelStatus.add(tabbedPane_1);

		JPanel panel = new JPanel();
		tabbedPane_1.addTab("Benutzer", null, panel, null);
		panel.setLayout(null);
		
		///////////////////////////////////////////////////////
		// doppelklick auf usernamen um privatchat zu starten//
		//                                                   //
		///////////////////////////////////////////////////////
		listUsers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String privateChatUserName = listUsers.getSelectedItem();
				
				if(privateChatUserName.equals(userName)){}else{
				out.println(4); //4. Methode privatChat starten
				out.println(privateChatUserName);
				}
			}
		});

		getListUsers().setMultipleSelections(false);
		getListUsers().setBounds(10, 10, 172, 464);
		panel.add(getListUsers());

		JPanel panel_1 = new JPanel();
		tabbedPane_1.addTab("R\u00E4ume", null, panel_1, null);
		panel_1.setLayout(null);

		getListRooms().setMultipleSelections(false);
		getListRooms().setBounds(10, 10, 172, 464);
		panel_1.add(getListRooms());

		ImageIcon icon4 = new ImageIcon("src/Icons/cancel.png");
		JButton btnNewButton_1 = new JButton("Ende", icon4);
		btnNewButton_1.setFont(new Font("Dialog", Font.PLAIN, 11));
		btnNewButton_1.setBounds(83, 544, 124, 23);
		panelStatus.add(btnNewButton_1);

		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				while(privatChats.size()>0){
					privatChats.elementAt(0).dispose();
					privatChats.remove(0);
				}
				dispose();
			}
		});

		JPanel panelChat2 = new JPanel();
		panelChat2.setBorder(new TitledBorder(null, "Chat", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelChat2.setBounds(10, 94, 527, 495);
		panelChat1.add(panelChat2);
		panelChat2.setLayout(null);

		textFieldChat = new JTextField();
		textFieldChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if ((text = textFieldChat.getText()).isEmpty()) {
				} else {
					sendChatMessage(); // Bei Klick auf Senden -----> rufe
										// Sende-Methode auf
				}
			}
		});
		textFieldChat.setBounds(10, 462, 372, 20);
		panelChat2.add(textFieldChat);
		textFieldChat.setColumns(10);

		// //////////////////////////////////////////////////////////////
		// /
		// / Button SENDEN
		// /
		// //////////////////////////////////////////////////////////////

		ImageIcon icon3 = new ImageIcon("src/Icons/send.png");
		JButton btnNewButton = new JButton("Senden", icon3);
		btnNewButton.setFont(new Font("Dialog", Font.PLAIN, 11));
		btnNewButton.setBounds(392, 460, 125, 23);
		panelChat2.add(btnNewButton);
		getTextArea().setBackground(Color.WHITE);
		getTextArea().setEditable(false);

		getTextArea().setBounds(10, 41, 507, 415);
		panelChat2.add(getTextArea());

		JPanel panelInformation = new JPanel();
		panelInformation.setBorder(new TitledBorder(null, "Information", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelInformation.setBounds(10, 11, 527, 72);
		panelChat1.add(panelInformation);
		panelInformation.setLayout(null);

		JLabel lblNewLabel_1 = new JLabel("Status:");
		lblNewLabel_1.setFont(new Font("Dialog", Font.BOLD, 11));
		lblNewLabel_1.setBounds(10, 47, 100, 14);
		panelInformation.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Verbindung:");
		lblNewLabel_2.setFont(new Font("Dialog", Font.BOLD, 11));
		lblNewLabel_2.setBounds(10, 22, 100, 14);
		panelInformation.add(lblNewLabel_2);
		getLabelConnection().setFont(new Font("Dialog", Font.PLAIN, 11));

		getLabelConnection().setBounds(120, 22, 397, 14);
		panelInformation.add(getLabelConnection());
		getRaumNameLabel().setFont(new Font("Dialog", Font.PLAIN, 11));

		getRaumNameLabel().setBounds(120, 47, 397, 14);
		panelInformation.add(getRaumNameLabel());
		btnNewButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if ((text = textFieldChat.getText()).isEmpty()) {
				} else {
					sendChatMessage(); // Bei Klick auf Senden -----> rufe
										// Sende-Methode auf
				}
			}
		});

		final JPanel panelLogin = new JPanel();
		panelLogin.setBorder(new TitledBorder(null, "Login", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelLogin.setBounds(10, 11, 432, 155);
		contentPane.add(panelLogin);
		panelLogin.setLayout(null);

		textFieldName = new JTextField();
		textFieldName.setBounds(152, 22, 100, 20);
		panelLogin.add(textFieldName);
		textFieldName.setColumns(10);

		// //////////////////////////////////////////////////////////////
		// /
		// / MenuBar + MenuItems
		// /
		// //////////////////////////////////////////////////////////////

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("Menu");
		menuBar.add(mnNewMenu);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		mnNewMenu.add(mntmExit);

		final JMenu mnOptionen = new JMenu("Optionen");
		menuBar.add(mnOptionen);

		JMenu mnNewMenu_3 = new JMenu("Help");
		mnNewMenu_3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JOptionPane.showMessageDialog(null, "... ist etwas Schreckliches -.-", "Hilflosigkeit ...", JOptionPane.OK_CANCEL_OPTION);
			}
		});
		mnOptionen.add(mnNewMenu_3);

		// //////////////////////////////////////////////////////////////
		// /
		// / ButtonGrp + RadioButtons
		// /
		// //////////////////////////////////////////////////////////////

		ButtonGroup buttgrp = new ButtonGroup();
		final JRadioButton rdbtnReg = new JRadioButton("Registrierung");
		rdbtnReg.setFont(new Font("Dialog", Font.PLAIN, 11));
		rdbtnReg.setBounds(248, 69, 100, 24);
		panelLogin.add(rdbtnReg);

		final JRadioButton rdbtnAnm = new JRadioButton("Anmeldung");
		rdbtnAnm.setFont(new Font("Dialog", Font.PLAIN, 11));
		rdbtnAnm.setBounds(10, 124, 100, 24);
		panelLogin.add(rdbtnAnm);
		buttgrp.add(rdbtnAnm);
		buttgrp.add(rdbtnReg);

		rdbtnReg.setSelected(true);
		rdbtnAnm.setBounds(82, 111, 121, 24);
		rdbtnReg.setBounds(204, 111, 121, 24);
		panelLogin.add(rdbtnReg);
		panelLogin.add(rdbtnAnm);

		passwordField = new JPasswordField();
		passwordField.setBounds(152, 53, 100, 20);
		panelLogin.add(passwordField);

		passwordField2 = new JPasswordField();
		passwordField2.setBounds(152, 84, 100, 20);
		panelLogin.add(passwordField2);

		// //////////////////////////////////////////////////////////////
		// /
		// / Button ABBRUCH
		// /
		// //////////////////////////////////////////////////////////////

		ImageIcon icon2 = new ImageIcon("src/Icons/cancel.png");
		JButton btnAbbruch = new JButton("Abbruch", icon2);
		btnAbbruch.setFont(new Font("Dialog", Font.PLAIN, 11));
		btnAbbruch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnAbbruch.setBounds(307, 81, 115, 23);
		panelLogin.add(btnAbbruch);

		JLabel labelUsername = new JLabel("Benutzername:");
		labelUsername.setFont(new Font("Dialog", Font.PLAIN, 11));
		labelUsername.setBounds(10, 25, 94, 14);
		panelLogin.add(labelUsername);

		JLabel labelPassword1 = new JLabel("Passwort:");
		labelPassword1.setFont(new Font("Dialog", Font.PLAIN, 11));
		labelPassword1.setBounds(10, 56, 79, 14);
		panelLogin.add(labelPassword1);

		final JLabel labelPassword2 = new JLabel("Passwort Wdh:");
		labelPassword2.setFont(new Font("Dialog", Font.PLAIN, 11));
		labelPassword2.setBounds(10, 90, 94, 14);
		panelLogin.add(labelPassword2);

		// //////////////////////////////////////////////////////////////
		// /
		// / Button LOS GEHTS
		// /
		// //////////////////////////////////////////////////////////////

		ImageIcon icon1 = new ImageIcon("src/Icons/ok.png");
		JButton btnTalk = new JButton("Los gehts", icon1);
		btnTalk.setFont(new Font("Dialog", Font.PLAIN, 11));
		btnTalk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isConnected == false){
				ct.connect();
				isConnected = true;}
				setUserName(textFieldName.getText());
				String password = passwordField.getText();
				String password2 = passwordField2.getText();
				boolean answer = false; // entscheidet darueber, ob Benutzer
										// registrieren oder anmelden will
				boolean wrongPass = false;

				// /////////////////////////////////
				// User will sich registrieren
				// /////////////////////////////////

				if (rdbtnReg.isSelected() == true) { // REGISTRIEREN ist
														// markiert
					if (userName.equals("") | password.equals("")) // duefen
																									// nicht
																									// leer
																									// sein
					{
						JOptionPane.showMessageDialog(null, "Benutzername und Passwort d\u00FCrfen nicht leer sein!", "Fehler ...",
								JOptionPane.OK_CANCEL_OPTION);
						wrongPass = true;
					}else{ if (userName.contains(" ")){JOptionPane.showMessageDialog(null, "Der Benutzername darf keine Leerzeichen enthalten.", "Fehler ...",
							JOptionPane.OK_CANCEL_OPTION);
					         wrongPass = true;}
						else {
					}
						if (!(passwordField2.getText().equals(passwordField.getText()))) // pass1
																							// +
																							// pass2
																							// stimmen
																							// nicht
																							// ueberein
						{
							passwordField.setBackground(Color.red);
							passwordField2.setBackground(Color.red);
							JOptionPane
									.showMessageDialog(null, "Passwoerter stimmen nicht \u00FCberein!", "Fehler ...", JOptionPane.OK_CANCEL_OPTION);
							passwordField.setBackground(Color.white);
							passwordField2.setBackground(Color.white);
							passwordField.setText("");
							passwordField2.setText("");
							wrongPass = true;
						} else // pass1 + pass2 stimmen ueberein
						{
							answer = false;

						}
					}
				}

				// /////////////////////////////////
				// User will sich anmelden
				// /////////////////////////////////

				else // if (rdbtnAnm.isSelected() == true)
				{
					if (textFieldName.getText().equals("") | passwordField.getText().equals("")) // Username
																									// oder
																									// Passwort
																									// leer
					{
						JOptionPane.showMessageDialog(null, "Benutzername und Passwort d\u00FCrfen nicht leer sein!", "Fehler",
								JOptionPane.OK_CANCEL_OPTION);
						wrongPass = true;
					} else // Username und Passwort nicht leer
					{
						answer = true;
					}
				} // Ende else if
				try {
					setOut(new PrintWriter(ct.getS().getOutputStream(), true));
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (NullPointerException e2) {
					JOptionPane.showMessageDialog(null, "Keine Verbindung zum Server", "Fehler", JOptionPane.OK_CANCEL_OPTION);
					System.exit(0);
				}

				if (wrongPass == false) { // wenn bei der Eingabe alles passt
					getOut().println(1);      //erste Fkt. login
					getOut().println(answer); // ...Senden der drei Werte
					getOut().println(getUserName());
					getOut().println(password);	
					try {
					    out = new PrintWriter(ct.getS().getOutputStream(), true);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					loginError = ct.getIn().nextBoolean();
					while((setReceivedMessage(ct.getIn().nextLine())).isEmpty()){}; // Einlesen der
														// Server-Antwort
					if (loginError == true) {
						JOptionPane.showMessageDialog(null, getReceivedMessage(), "Fehler.", JOptionPane.OK_CANCEL_OPTION); // Wenn
																														// loginError
																														// --->
																														// Einblenden
																														// der
																														// Fehlermeldung
					} else {
						while ((setServerName(ct.getIn().nextLine())).isEmpty()) {
						}
						;
						getLabelConnection().setText("Du bist verbunden mit dem Server " + "\"" + getServerName() + "\"");
						JOptionPane.showMessageDialog(null, getReceivedMessage(), "Erfolgreich.", JOptionPane.OK_CANCEL_OPTION);
						panelLogin.setVisible(false); // LoginFenster ausblenden
						setSize(800, 660); // neue JFrameGr\F6\DFe setzen
						panelChat1.setVisible(true);
						panelChat1.setBounds(5, 5, 775, 600); // PanelChat ausrichten
						mnOptionen.setEnabled(true);
						// Raumnamen einlesen, Raumliste aufbauen:
						setnumberOfRooms(ct.getIn().nextInt()); // Raum Anzahl einlesen ----> ServerThread 302
						out.println(true);
						String roomName;
						for (int k = 0; k < getnumberOfRooms(); k++) {
							while ((roomName = ct.getIn().nextLine()).isEmpty()) {
							}
							listRooms.add(roomName);
						}
						out.println(true); // ServerThread 281
						// Benutzernamen einlesen, Benutzerliste aufbauen
						ct.getIn().nextBoolean();

						int usersInRoom = ct.getIn().nextInt();
						String listUserName;
						for (int l = 0; l < usersInRoom; l++) {
							while ((listUserName = ct.getIn().nextLine()).isEmpty()) {
							}
							listUsers.add(listUserName);
						}
						listUsers.add(getUserName());
						out.println(true);
						// Label setzen
						getRaumNameLabel().setText("Du bist als " + getUserName() + " im Raum " + "\"" + getListRooms().getItem(0) + "\".");
						out.println(true);
						ct.start();

					}
				}
			}
		}); // Ende Button Losgehts

		btnTalk.setBounds(307, 52, 115, 23);
		panelLogin.add(btnTalk);

		// //////////////////////////////////////////////////////////////
		// /
		// / PasswordField2 ein-/ausblenden via RadioButton
		// /
		// //////////////////////////////////////////////////////////////

		rdbtnAnm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				passwordField2.setVisible(false);
				labelPassword2.setVisible(false);
			}
		});

		rdbtnReg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				passwordField2.setVisible(true);
				labelPassword2.setVisible(true);
			}
		});

		// ///////////////////////////////////////////////////////////////
		// /
		// / Doppelklick auf Raum in der Liste, um Raum zu wechseln
		// /
		// //////////////////////////////////////////////////////////////

		getListRooms().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			if(getListRooms().getSelectedIndex() == -1){}
			else{
				if (getListRooms().getSelectedIndex() + 1 == getRoomIndex()) {
					// Bei Doppelklick auf den Raum, in dem man gerade ist,
					// passiert gar nichts.
				} else { if(joinError == true){}else{
					// Ansonsten Raumwechsel
					out.println(2); // 2. Fkt join Room
					out.println(true); 
				}
					}
				}
			}
				
		});
	}

	void sendChatMessage() {
		text = textFieldChat.getText();
		while(!(text.equals("")) && text.contains("  ")){
			text = text.replace("  ", " ");
			System.out.println(text);
		}
		if(text.startsWith(" ")){text = text.substring(1);}
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String t = sdf.format(new Date());
		if(text.equals("")){}else{
		text = this.getUserName() + " [" + t + "]: " + text + "\n";
		out.println(3); // 3. fkt. chatmessage senden
		out.println(text);}
		textFieldChat.setText("");// sende Inhalt des text feldes, dann leere
	}

	public List getListRooms() {
		return listRooms;
	}

	public void setListRooms(List listRooms) {
		this.listRooms = listRooms;
	}

	public JLabel getLabelConnection() {
		return labelConnection;
	}

	public void setLabelConnection(JLabel labelConnection) {
		this.labelConnection = labelConnection;
	}

	public List getListUsers() {
		return listUsers;
	}

	public void setListUsers(List listUsers) {
		this.listUsers = listUsers;
	}

	public JLabel getRaumNameLabel() {
		return raumNameLabel;
	}

	public void setRaumNameLabel(JLabel raumNameLabel) {
		this.raumNameLabel = raumNameLabel;
	}

	public TextArea getTextArea() {
		return textArea;
	}

	public void setTextArea(TextArea textArea) {
		this.textArea = textArea;
	}

	public String getServerName() {
		return serverName;
	}

	public String setServerName(String serverName) {
		this.serverName = serverName;
		return serverName;
	}

	public PrintWriter getOut() {
		return out;
	}

	public void setOut(PrintWriter out) {
		this.out = out;
	}

	public void setJoinError(boolean joinError) {
		this.joinError = joinError;
	}

	public boolean isJoinError() {
		return joinError;
	}

	public String setReceivedMessage(String receivedMessage) {
		this.receivedMessage = receivedMessage;
		return receivedMessage;
	}

	public String getReceivedMessage() {
		return receivedMessage;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public void setnumberOfRooms(int numberOfRooms) {
		this.numberOfRooms = numberOfRooms;
	}

	public int getnumberOfRooms() {
		return numberOfRooms;
	}

	public void setRoomIndex(int roomIndex) {
		this.roomIndex = roomIndex;
	}

	public int getRoomIndex() {
		return roomIndex;
	}

	public void setPrivatChats(Vector<PrivatChat> privatChats) {
		this.privatChats = privatChats;
	}

	public Vector<PrivatChat> getPrivatChats() {
		return privatChats;
	}
}