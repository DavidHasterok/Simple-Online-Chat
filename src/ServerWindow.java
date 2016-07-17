//das ist die neueste version. 14:45 am 13.2.2013

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.TextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import java.awt.List;
import java.awt.Color;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Label;
import java.awt.Toolkit;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ServerWindow extends JFrame {

	private Listener listener;
	private ServerSocket server;
	private Vector<Room> connections;
	private Vector<ServerThread[]> privateChat;
	private RandomAccessFile datafile;
	private RandomAccessFile bannedUserFile;
	private RandomAccessFile serverLogFile;
	private RandomAccessFile serverUserFile;
	private TextArea textAreaServer = new TextArea("", 30, 100, TextArea.SCROLLBARS_VERTICAL_ONLY);
	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private List listAllUsers = new List();
	private List listRoom = new List();
	private List listAllBans = new List();
	private Room defaultRoom = new Room("Default");
	private JButton btnClose;
	private JComboBox comboBox;
	private JButton btnExec;
	private JTextField textFieldExecute = new JTextField();
	private JPanel contentPane;
	private JTextField textField;
	private SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss z");
	private String serverTime = sdf2.format(new Date());
	private String serverName = "BestAmmeServer 2013";
	private JLabel labelServerName = new JLabel("Server \"" + getServerName() + "\" ist online");
	private Label labelRoomID = new Label("");
	private Label labelUserNumbers = new Label("");
	private JPasswordField passwordField;
	private JTextField textFieldName2;

	/**
	 * Launch the application.
	 */

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerWindow frame = new ServerWindow();
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
	ServerWindow() {
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				setSize(355, 120);
			}
		});
		setIconImage(Toolkit.getDefaultToolkit().getImage("src/Icons/Icons/server.png"));
		
		try {
			serverUserFile = new RandomAccessFile("src/admin.rtf", "rw");// File in dem die Usernames + Passwoerter gespeichert werden
			setDatafile(new RandomAccessFile("src/chatuser.rtf", "rw"));// File in dem die Usernames + Passwoerter gespeichert werden
			setBannedUserFile(new RandomAccessFile("src/bannedUser.rtf", "rw"));// File in dem gebannten User gespeichert werden
			setServerLogFile(new RandomAccessFile("src/serverlog.rtf", "rw")); // Log-File
          try {
			serverLogFile.seek(serverLogFile.length());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
			
			
		} catch (FileNotFoundException e) {
		}
		;

		try {
			getBannedUserFile().seek(0);
			while (getBannedUserFile().getFilePointer() != getBannedUserFile().length()) {
				String z = getBannedUserFile().readLine();
				if(!(z.contains(" "))) {
					listAllBans.add(z);
				}
			}
		} catch (IOException e1) {
		}
		;

		
		
		
		setConnections(new Vector<Room>(64));
		setPrivateChat(new Vector<ServerThread[]>(64));
		addRoom(defaultRoom);
		setTitle("Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 711, 514);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(10, 152, 684, 562);
		contentPane.add(panel);
		panel.setLayout(null);

		try { // Die ersten zwei Zeilen der Log-Datei
			getServerLogFile().writeBytes("-----SERVER-LOG-FILE vom " + serverTime + "----\n\n");
			getServerLogFile().writeBytes("Server wurde gestartet.\n");
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		refreshRoomList();

		JPanel panelServerlog = new JPanel();
		panelServerlog.setBorder(new TitledBorder(null, "Serverlog", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelServerlog.setBounds(10, 11, 377, 529);
		panel.add(panelServerlog);
		panelServerlog.setLayout(null);

		ImageIcon icon1 = new ImageIcon("src/Icons/send.png");
		JButton btnSend = new JButton("Senden", icon1);
		btnSend.setFont(new Font("Dialog", Font.PLAIN, 11));
		btnSend.setBounds(10, 495, 124, 23);
		panelServerlog.add(btnSend);

		ImageIcon icon2 = new ImageIcon("src/Icons/cancel.png");
		btnClose = new JButton("Schliessen", icon2);
		btnClose.setFont(new Font("Dialog", Font.PLAIN, 11));
		btnClose.setBounds(239, 495, 124, 23);
		panelServerlog.add(btnClose);

		textField = new JTextField();
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendServerMessage();
			}
		});
		textField.setBounds(10, 464, 353, 20);
		panelServerlog.add(textField);
		textField.setColumns(10);

		labelServerName.setFont(new Font("Dialog", Font.PLAIN, 11));
		labelServerName.setBounds(10, 23, 357, 14);
		panelServerlog.add(labelServerName);
		getTextAreaServer().setBackground(Color.WHITE);
		getTextAreaServer().setEditable(false);

		getTextAreaServer().setBounds(10, 43, 353, 415);
		panelServerlog.add(getTextAreaServer());

		JPanel panelAufgaben = new JPanel();
		panelAufgaben.setBorder(new TitledBorder(null, "Aufgaben", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelAufgaben.setBounds(397, 421, 277, 119);
		panel.add(panelAufgaben);
		panelAufgaben.setLayout(null);

		// //////////////////////////////////////////////////////////////
		// /
		// / Button AUSFUEHREN (0=Warnen/1=Kicken/2=Bannen/3=Entbannen/5=Raum hinzufuegen/
		// / 6=Raumnamen aendern/7= Raum loeschen/9=Servernamen setzen)
		// //////////////////////////////////////////////////////////////

		ImageIcon icon3 = new ImageIcon("src/Icons/exec.png");
		btnExec = new JButton("Ausf\u00FChren", icon3);
		btnExec.setFont(new Font("Dialog", Font.PLAIN, 11));
		btnExec.setBounds(142, 85, 125, 23);
		panelAufgaben.add(btnExec);
		textFieldExecute.setBounds(10, 55, 257, 20);
		panelAufgaben.add(textFieldExecute);
		textFieldExecute.setColumns(10);

		comboBox = new JComboBox();
		comboBox.setFont(new Font("Dialog", Font.PLAIN, 11));
		comboBox.setBounds(10, 24, 257, 20);
		panelAufgaben.add(comboBox);
		comboBox.setModel(new DefaultComboBoxModel(new String[] { "Benutzer verwarnen", "Benutzer kicken", "Benutzer bannen",
		"Benutzer entbannen", "----","Raum hinzuf\u00FCgen", "Raumnamen \u00E4ndern", "Raum l\u00F6schen", "-----", "Servernamen setzen" }));

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(null, "Status", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_4.setBounds(397, 11, 277, 320);
		panel.add(panel_4);
		panel_4.setLayout(null);
		getTabbedPane().setFont(new Font("Dialog", Font.PLAIN, 10));

		getTabbedPane().setBounds(10, 21, 257, 288);
		panel_4.add(getTabbedPane());

		JPanel panel_1 = new JPanel();

		getTabbedPane().addTab("R\u00E4ume (" + String.valueOf(listRoom.getItemCount()) + ")", null, panel_1, null);
		panel_1.setLayout(null);
		listRoom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(listRoom.getSelectedIndex() != -1){
				labelRoomID.setText(String.valueOf(listRoom.getSelectedIndex()));
				labelUserNumbers.setText(String.valueOf(getConnections().elementAt(listRoom.getSelectedIndex()).getUsersInRoom().size()));
				}
				}
		});

		listRoom.setBounds(10, 10, 232, 240);
		panel_1.add(listRoom);

		JPanel panel_2 = new JPanel();
		getTabbedPane().addTab("Benutzer (" + getListAllUsers().getItemCount() + ")", null, panel_2, null);
		panel_2.setLayout(null);

		getListAllUsers().setBounds(10, 10, 232, 240);
		panel_2.add(getListAllUsers());

		JPanel panel_3 = new JPanel();
		getTabbedPane().addTab("Gebannt (" + (listAllBans.getItemCount()) + ")", null, panel_3, null);
		panel_3.setLayout(null);

		listAllBans.setBounds(10, 10, 232, 240);
		panel_3.add(listAllBans);

		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(null, "Informationen", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_5.setBounds(397, 342, 277, 68);
		panel.add(panel_5);
		panel_5.setLayout(null);

		JLabel lblNewLabel_1 = new JLabel("Mitglieder: ");
		lblNewLabel_1.setFont(new Font("Dialog", Font.PLAIN, 11));
		lblNewLabel_1.setBounds(10, 46, 72, 14);
		panel_5.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Nummer:");
		lblNewLabel_2.setFont(new Font("Dialog", Font.PLAIN, 11));
		lblNewLabel_2.setBounds(10, 21, 62, 14);
		panel_5.add(lblNewLabel_2);

		labelRoomID.setBounds(88, 21, 62, 14);
		panel_5.add(labelRoomID);

		labelUserNumbers.setBounds(88, 46, 62, 14);
		panel_5.add(labelUserNumbers);
		
		final JPanel panelLogin = new JPanel();
		panelLogin.setBorder(new TitledBorder(null, "Admin-Login", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelLogin.setBounds(10, 11, 329, 75);
		contentPane.add(panelLogin);
		panelLogin.setLayout(null);
		
		JLabel lblBenutzer = new JLabel("Benutzer:");
		lblBenutzer.setBounds(10, 23, 70, 14);
		panelLogin.add(lblBenutzer);
		
		JLabel lblPasswort = new JLabel("Passwort:");
		lblPasswort.setBounds(10, 48, 70, 14);
		panelLogin.add(lblPasswort);
		
		passwordField = new JPasswordField();
		passwordField.setEchoChar('*');
		passwordField.setBounds(100, 45, 100, 20);
		panelLogin.add(passwordField);
		
		JButton btnAnmelden = new JButton("Anmelden");
		btnAnmelden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String userName = textFieldName2.getText();
				String password = passwordField.getText();
													// registrieren oder anmelden will
				boolean wrongPass = false;

			if (userName.equals("") | password.equals(""))	// duefen nicht leer sein
					{
						JOptionPane.showMessageDialog(null, "Benutzername und Passwort d\u00FCrfen nicht leer sein!", "Fehler ...",
								JOptionPane.OK_CANCEL_OPTION);
						wrongPass = true;
					}else{ if (userName.contains(" ")){JOptionPane.showMessageDialog(null, "Der Benutzername darf keine Leerzeichen enthalten.", "Fehler ...",
							JOptionPane.OK_CANCEL_OPTION);
					         wrongPass = true;}else{
					try {
						serverUserFile.seek(0);
						if(serverUserFile.readLine().equalsIgnoreCase(userName)){
							if(serverUserFile.readLine().equals(password)){
								JOptionPane.showMessageDialog(null, "Anmeldung erfolgreich!", "Erfolg...",
										JOptionPane.OK_CANCEL_OPTION);
								setBounds(5, 5, 710, 580);
								//setSize(500, 600);
								panelLogin.setVisible(false);
								setServer(new ServerSocket(1234)); // erstellt serversocket
								getListener().start(); // startet listener
							}
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}	
				}
					}
			}
		});
		btnAnmelden.setBounds(210, 19, 110, 23);
		panelLogin.add(btnAnmelden);
		
		JButton btnRegistrieren = new JButton("Schliessen");
		btnRegistrieren.setBounds(210, 44, 110, 23);
		panelLogin.add(btnRegistrieren);
		
		btnRegistrieren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			dispose();	
			}
		});
		
		textFieldName2 = new JTextField();
		textFieldName2.setBounds(100, 20, 100, 20);
		panelLogin.add(textFieldName2);
		textFieldName2.setColumns(10);
		btnExec.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (comboBox.getSelectedIndex() == 0) { // User Verwarnen

					if (getListAllUsers().getSelectedIndex() != -1) {
						String warnedUser = getListAllUsers().getSelectedItem();
						String warnung = textFieldExecute.getText();
					
							while(!(warnung.equals("")) && warnung.contains("  ")){
							warnung = warnung.replace("  ", " ");
							}
							if(warnung.startsWith(" ")){warnung = warnung.substring(1);}
							
							if(warnung.equals("")){}else{
							warnung = "SERVER-WARNUNG: " + warnung;
						getTextAreaServer().append(warnung + "\n" + warnedUser + " wurde verwarnt." + "\n");
						try {
							serverLogFile.writeBytes(warnung + "\n" + warnedUser + " wurde verwarnt." + "\n");
						} catch (IOException e2) {
							e2.printStackTrace();
						}
						for (int i = 0; i < getConnections().size(); i++) {
							for (int j = 0; j < getConnections().elementAt(i).getUsersInRoom().size(); j++) {
								if (getConnections().elementAt(i).getUsersInRoom().elementAt(j).getUsername().equals(warnedUser)) {
									getConnections().elementAt(i).getUsersInRoom().elementAt(j).send(1);
									getConnections().elementAt(i).getUsersInRoom().elementAt(j).send(warnung);
									try {
										getConnections().elementAt(i).getUsersInRoom().elementAt(j).sleep(10);
									} catch (InterruptedException e1) {
										e1.printStackTrace();
									}
									broadcast(i, 1);
									broadcast(i, warnedUser + " wurde verwarnt.");
								}
							}
							}
						}
						textFieldExecute.setText("");

					}

				}

				if (comboBox.getSelectedIndex() == 1) { // User kicken

					if (getListAllUsers().getSelectedIndex() != -1) {
						String kickedUser = getListAllUsers().getSelectedItem();

						for (int i = 0; i < getConnections().size(); i++) {
							for (int j = 0; j < getConnections().elementAt(i).getUsersInRoom().size(); j++) {
								if (getConnections().elementAt(i).getUsersInRoom().elementAt(j).getUsername().equals(kickedUser)) {
                                    getTextAreaServer().append(kickedUser + " wurde aus dem Raum "+ listRoom.getItem(i) + " gekickt.");
                                    try {
										serverLogFile.writeBytes(kickedUser + " wurde aus dem Raum "+ listRoom.getItem(i) + " gekickt.");
									} catch (IOException e2) {
										e2.printStackTrace();
									}
									getConnections().elementAt(i).getUsersInRoom().elementAt(j).send(1);
									getConnections().elementAt(i).getUsersInRoom().elementAt(j).send("Du wurdest aus dem Raum gekickt.");
									try {
										getConnections().elementAt(i).getUsersInRoom().elementAt(j).sleep(10);
									} catch (InterruptedException e1) {
										e1.printStackTrace();
									}
									getConnections().elementAt(i).getUsersInRoom().elementAt(j).setRoomNumber(-1);
									getConnections().elementAt(i).getUsersInRoom().remove(j);
									break;
								}
							}
						for(int k = 0; k < getConnections().elementAt(i).getUsersInRoom().size(); k++){
							getConnections().elementAt(i).getUsersInRoom().elementAt(k).send(1);
							getConnections().elementAt(i).getUsersInRoom().elementAt(k).send(kickedUser + " wurde aus dem Raum gekickt.");
							try {
								getConnections().elementAt(i).getUsersInRoom().elementAt(k).sleep(10);
							} catch (InterruptedException e1) {
							e1.printStackTrace();
							}
							getConnections().elementAt(i).getUsersInRoom().elementAt(k).send(7);
							getConnections().elementAt(i).getUsersInRoom().elementAt(k).send(kickedUser);
						}
						}
					}
				}

				if (comboBox.getSelectedIndex() == 2) { // User bannen

					if (getListAllUsers().getSelectedIndex() != -1) {
						String bannedUser = getListAllUsers().getSelectedItem();
						String fileCheck = "";
						try {
							while (getBannedUserFile().getFilePointer() != getBannedUserFile().length()
									&& !((fileCheck = getBannedUserFile().readLine()).equalsIgnoreCase(bannedUser))) {
								;
							}
							if (fileCheck.equals(bannedUser)) {
							} else {
								getBannedUserFile().writeBytes(bannedUser+ "\n");
								for (int i = 0; i < getConnections().size(); i++) {
									for (int j = 0; j < getConnections().elementAt(i).getUsersInRoom().size(); j++) {
										if (getConnections().elementAt(i).getUsersInRoom().elementAt(j).getUsername().equals(bannedUser)) {
											getConnections().elementAt(i).getUsersInRoom().elementAt(j).send(1);
											getConnections().elementAt(i).getUsersInRoom().elementAt(j)
													.send("Du wurdest aus dem Chat gebannt. Einloggen nicht mehr m\u00F6glich.");
											getConnections().elementAt(i).getUsersInRoom().elementAt(j).setRoomNumber(-1);
											getConnections().elementAt(i).getUsersInRoom().elementAt(j).setJoinError(true);
											try {
												getConnections().elementAt(i).getUsersInRoom().elementAt(j).sleep(10);
											} catch (InterruptedException e1) {
												e1.printStackTrace();
											}
											getConnections().elementAt(i).getUsersInRoom().elementAt(j).send(11);
											getConnections().elementAt(i).getUsersInRoom().remove(j);
											getListAllUsers().remove(bannedUser);
                                   

											
											getTabbedPane().setTitleAt(1, "Benutzer (" + String.valueOf(getListAllUsers().getItemCount()) + ")");
										break;
										}
									}
									for(int k=0; k<getConnections().elementAt(i).getUsersInRoom().size(); k++){
										getConnections().elementAt(i).getUsersInRoom().elementAt(k).send(1);
										getConnections().elementAt(i).getUsersInRoom().elementAt(k).send("User " + bannedUser + " wurde gebannt.");
										try {
											getConnections().elementAt(i).getUsersInRoom().elementAt(k).sleep(10);
										} catch (InterruptedException e1) {
											e1.printStackTrace();
										}
										getConnections().elementAt(i).getUsersInRoom().elementAt(k).send(7);
										getConnections().elementAt(i).getUsersInRoom().elementAt(k).send(bannedUser);
										try {
											getConnections().elementAt(i).getUsersInRoom().elementAt(k).sleep(10);
										} catch (InterruptedException e1) {
											e1.printStackTrace();
										}
									}
								}
							}
							getBannedUserFile().seek(0);

						} catch (IOException e1) {
						}

					}
					listAllBans.removeAll();
					try {
						while (getBannedUserFile().getFilePointer() != getBannedUserFile().length()) {
							listAllBans.add(getBannedUserFile().readLine());
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					getTabbedPane().setTitleAt(2, "Gebannt (" + String.valueOf(listAllBans.getItemCount()) + ")");
				}
				
				
				if (comboBox.getSelectedIndex() == 3) // Benutzer entbannen
				{
					
					if (listAllBans.getSelectedIndex() != -1) {
					getTextAreaServer().append(listAllBans.getSelectedItem() + " wurde entbannt.");
					try {
						getServerLogFile().writeBytes(listAllBans.getSelectedItem() + " wurde entbannt.");
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					String userUnbanned = listAllBans.getSelectedItem();
					for (int i = 0; i < listAllBans.getItemCount(); i++) { // Benutzer aus der Liste AllBans entfernen
						if (listAllBans.getItem(i).equals(userUnbanned)) {
							listAllBans.remove(i);
						}
					}
					getTabbedPane().setTitleAt(2, "Gebannt (" + String.valueOf(listAllBans.getItemCount()) + ")"); // Listen-Titel aktualisieren
					try {
						getBannedUserFile().seek(0);
						String s = getBannedUserFile().readLine();
						while ((getBannedUserFile().getFilePointer() != getBannedUserFile().length()) && !(s.equalsIgnoreCase(userUnbanned))) {
							s = getBannedUserFile().readLine(); // Datei durchlaufen bis zum gesuchten Benutzername
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						int n = userUnbanned.length();
						getBannedUserFile().seek(getBannedUserFile().getFilePointer() - (n + 1));
						for (int i = 0; i < n; i++) {
							getBannedUserFile().writeBytes(" "); // Zeile im BannedUserFile mit Leerzeichen �berschreiben
						}
						getBannedUserFile().writeBytes("\n");
						// In der Datei ist jetzt eine leere Zeile, diese wird nun entfernt:
						int j = 0;
						Vector<String> bannedUser = new Vector<String>(); // Zwischenspeicher-Vector
						getBannedUserFile().seek(0);
						while ((getBannedUserFile().getFilePointer() != getBannedUserFile().length())) {
							// Datei durchlaufen und alle gebannten User im Vector ablegen, die leere Zeile wird �bersprungen
							String s = getBannedUserFile().readLine();
							if (!(s.contains(" "))) {
								bannedUser.add(j, s);
								j++;
							}
						}
						getBannedUserFile().setLength(0); // BannedUser-Datei leeren
						// Aus dem Vector die BannedUser-Datei neu aufbauen ----> keine leere Zeile mehr
						for (int k = 0; k < bannedUser.size(); k++) {
							if (!(bannedUser.elementAt(k) == null)) {
								getBannedUserFile().writeBytes(bannedUser.elementAt(k) + "\n");
							}
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}}
				}

				if (comboBox.getSelectedIndex() == 5) // Raum hinzufuegen
				{
					if (!(textFieldExecute.getText().equals(""))) {
						String roomName = textFieldExecute.getText();
						while(!(roomName.equals("")) && roomName.contains("  ")){
							roomName = roomName.replace("  ", " ");}
						if(roomName.startsWith(" ")){roomName = roomName.substring(1);}
						
					
					if(roomName.equals("")){}else{
						Room newRoom = new Room(roomName);
						if (exists(newRoom) == true) {
							getTextAreaServer().append("Ein Raum mit diesem Namen existiert schon.\n");

						} else {
							addRoom(newRoom);
							getTextAreaServer().append("Neuer Raum [" + roomName + "] wurde angelegt.\n");
							try {
								getServerLogFile().writeBytes("Neuer Raum [" + roomName + "] wurde angelegt.\n");
							} catch (IOException e2) {
								e2.printStackTrace();
							}
				
     						for (int i = 0; i < getConnections().size(); i++) {
								for (int j = 0; j < getConnections().elementAt(i).getUsersInRoom().size(); j++) {
									getConnections().elementAt(i).getUsersInRoom().elementAt(j).send(2);
									getConnections().elementAt(i).getUsersInRoom().elementAt(j).send(roomName);
							    	getConnections().elementAt(i).getUsersInRoom().elementAt(j).send("Neuer Raum [" + roomName + "] wurde angelegt.\n");
								}
							}
						}
				
					}
						}
						textFieldExecute.setText(""); // TextFeld wieder leeren
						getTabbedPane().setTitleAt(0, "R\u00E4ume (" + String.valueOf(listRoom.getItemCount()) + ")");
					}

				if (comboBox.getSelectedIndex() == 6) // Raumnamen aendern
				{
					if (listRoom.getSelectedIndex() != -1 && !(textFieldExecute.getText().equals(""))) {
						String raumname = textFieldExecute.getText();
						while(!(raumname.equals("")) && raumname.contains("  ")){
							raumname = raumname.replace("  ", " ");
						}
						if(raumname.startsWith(" ")){raumname = raumname.substring(1);}
						
						if(raumname.equals("")){}else{
						String temp = getConnections().elementAt(listRoom.getSelectedIndex()).getRoomName();
						
						if(raumname.equals(temp)){}else{
						getConnections().elementAt(listRoom.getSelectedIndex()).setRoomName(raumname);

						for (int i = 0; i < getConnections().size(); i++) {
							for (int j = 0; j < getConnections().elementAt(i).getUsersInRoom().size(); j++) {
								getConnections().elementAt(i).getUsersInRoom().elementAt(j).send(3);
								getConnections().elementAt(i).getUsersInRoom().elementAt(j).sendRoomListChange(temp, raumname);
								getConnections().elementAt(i).getUsersInRoom().elementAt(j).send("Der Raum [" + temp + "] wurde in [" + raumname + "] umbenannt.");
							}
						}
                       getTextAreaServer().append("Raum [" + temp + "] wurde in [" + raumname + "] umbenannt.\n");
						try {
							getServerLogFile().writeBytes("Raum [" + temp + "] wurde in [" + raumname + "] umbenannt.\n");
						} catch (IOException e2) {
							e2.printStackTrace();
						}
						}
						}
						
						refreshRoomList();
					}
					textFieldExecute.setText("");
				}
				
				if (comboBox.getSelectedIndex() == 7) // Raum loeschen
				{

					if (listRoom.getSelectedIndex() != -1) {
						if(listRoom.getItemCount()>1){							
						for (int i = 0; i < getConnections().size(); i++) {
							for (int j = 0; j < getConnections().elementAt(i).getUsersInRoom().size(); j++) {
								getConnections().elementAt(i).getUsersInRoom().elementAt(j).send(4);
								getConnections().elementAt(i).getUsersInRoom().elementAt(j)
								.sendRoomListChange(getConnections().elementAt(listRoom.getSelectedIndex()).getRoomName());								
								try {
									getConnections().elementAt(i).getUsersInRoom().elementAt(j).sleep(10);
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}
							}
						}
						for (int j = 0; j < getConnections().elementAt(listRoom.getSelectedIndex()).getUsersInRoom().size(); j++) {
							getConnections().elementAt(listRoom.getSelectedIndex()).getUsersInRoom().elementAt(j).send("Dieser Raum wird jetzt geschlossen. Bitte w\u00E4hle einen anderen Raum.");
							try {
								getConnections().elementAt(listRoom.getSelectedIndex()).getUsersInRoom().elementAt(j).sleep(10);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
							getConnections().elementAt(listRoom.getSelectedIndex()).getUsersInRoom().elementAt(j).setRoomNumber(-1);
						}
						(getConnections().elementAt(listRoom.getSelectedIndex())).getUsersInRoom().removeAllElements();
						for (int i = (listRoom.getSelectedIndex() + 1); i < getConnections().size(); i++) {
							for (int j = 0; j < getConnections().elementAt(i).getUsersInRoom().size(); j++) {
								getConnections().elementAt(i).getUsersInRoom().elementAt(j).setRoomNumber(
										getConnections().elementAt(i).getUsersInRoom().elementAt(j).getRoomNumber() - 1);
							}
						}
						getConnections().remove(listRoom.getSelectedIndex());
						refreshRoomList();
						getTabbedPane().setTitleAt(0, "R\u00E4ume (" + String.valueOf(listRoom.getItemCount()) + ")");

					}
					}
				}
				if (comboBox.getSelectedIndex() == 9) // Servernamen setzen
				{
					if (!(textFieldExecute.getText().isEmpty())) {
						String newServerName = textFieldExecute.getText();
						while(!(newServerName.equals("")) && newServerName.contains("  ")){
							newServerName = newServerName.replace("  ", " ");
						}
						if(newServerName.startsWith(" ")){newServerName = newServerName.substring(1);}
						
						if(newServerName.equals("")){}else{
						setServerName(textFieldExecute.getText());
						labelServerName.setText("Server \"" + getServerName() + "\" ist online");

						for (int i = 0; i < getConnections().size(); i++) {
							for (int j = 0; j < getConnections().elementAt(i).getUsersInRoom().size(); j++) {
								getConnections().elementAt(i).getUsersInRoom().elementAt(j).send(5);
								getConnections().elementAt(i).getUsersInRoom().elementAt(j).serverNameChange(getServerName());
							}
							}
						}
					}
				}
			}
		});

		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}

		});
		btnSend.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent e) {
				sendServerMessage();
			}
		});

		try {
			setListener(new Listener(this)); // erstellt listener

		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	synchronized void broadcast(int room, String message) { //Senden an alle Clients in einem Raum
		for (int i = 0; i < getConnections().elementAt(room).getUsersInRoom().size(); i++) {
			(getConnections().elementAt(room).getUsersInRoom().elementAt(i)).send(message);
		}
	}

	synchronized void broadcast(int room, int methodIndex) { 
		for (int i = 0; i < getConnections().elementAt(room).getUsersInRoom().size(); i++) {
			(getConnections().elementAt(room).getUsersInRoom().elementAt(i)).send(methodIndex);

		}
	}
	
	synchronized void broadcastToAll(String message) { // Methode zum Senden an
														// ALLE User im gesamten
														// Chat
		for (int i = 0; i < getConnections().size(); i++) {
			for (int j = 0; j < getConnections().elementAt(i).getUsersInRoom().size(); j++) {
				(getConnections().elementAt(i).getUsersInRoom().elementAt(j)).send(message);
			}
		}
	}

	synchronized void broadcastToAll(int methodIndex) { // Methode zum Senden an alle User
		for (int i = 0; i < getConnections().size(); i++) {
			for (int j = 0; j < getConnections().elementAt(i).getUsersInRoom().size(); j++) {
				(getConnections().elementAt(i).getUsersInRoom().elementAt(j)).send(methodIndex);
			}
		}
	}

	boolean exists(Room room) { // Test, ob es einen �bergebenen Raum schon gibt
		boolean e = false;
		for (int i = 0; i < getConnections().size(); i++) {
			if (room.getRoomName().equals(getConnections().elementAt(i).getRoomName())) {
				e = true;
				break;
			}
		}
		return e;
	}

	void addRoom(Room room) { // Fuege neuen Raum hinzu
		if (exists(room) == false) {
			this.getConnections().add(room);
            refreshRoomList();		
		}
	}

	void refreshRoomList() { // Aktualisiere Raum-Liste
		listRoom.removeAll();
		for (int r = 0; r < getConnections().size(); r++) {
			listRoom.add(getConnections().elementAt(r).getRoomName());
		}
	}

	void sendServerMessage() { // Sende Server-Message an alle Clients im
								// gesamten Chat
		String text = textField.getText();
		while(!(text.equals("")) && text.contains("  ")){
			text = text.replace("  ", " ");
		}
		if(text.startsWith(" ")){text = text.substring(1);}
		if (!(text.equals(""))) {
			
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			String t = sdf.format(new Date());
			text = "SERVER-MESSAGE: "  + text + "\n";
			broadcastToAll(1);
			broadcastToAll(text);
			getTextAreaServer().append(text);
			try {
				getServerLogFile().writeBytes(text);
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			textField.setText("");
		}

	}

	public Listener getListener() {
		return listener;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public Vector<Room> getConnections() {
		return connections;
	}

	public void setConnections(Vector<Room> connections) {
		this.connections = connections;
	}

	public List getListAllUsers() {
		return listAllUsers;
	}

	public void setListAllUsers(List listAllUsers) {
		this.listAllUsers = listAllUsers;
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	public void setTabbedPane(JTabbedPane tabbedPane) {
		this.tabbedPane = tabbedPane;
	}

	public ServerSocket getServer() {
		return server;
	}

	public void setServer(ServerSocket server) {
		this.server = server;
	}


	public RandomAccessFile getServerLogFile() {
		return serverLogFile;
	}

	public void setServerLogFile(RandomAccessFile serverLogFile) {
		this.serverLogFile = serverLogFile;
	}

	public RandomAccessFile getBannedUserFile() {
		return bannedUserFile;
	}

	public void setBannedUserFile(RandomAccessFile bannedUserFile) {
		this.bannedUserFile = bannedUserFile;
	}

	public RandomAccessFile getDatafile() {
		return datafile;
	}

	public void setDatafile(RandomAccessFile datafile) {
		this.datafile = datafile;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public TextArea getTextAreaServer() {
		return textAreaServer;
	}

	public void setTextAreaServer(TextArea textAreaServer) {
		this.textAreaServer = textAreaServer;
	}

	public void setPrivateChat(Vector<ServerThread[]> privateChat) {
		this.privateChat = privateChat;
	}

	public Vector<ServerThread[]> getPrivateChat() {
		return privateChat;
	}
}