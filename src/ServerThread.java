//Schnittstelle zwischen Client und Server, sendet und empfaengt

import java.io.*;
import java.net.*;
import java.util.*;

class ServerThread extends Thread {
	private Socket s;
	private ServerWindow serverWindow;
	private Scanner scan;
	private PrintWriter pw;
	private String username = "Guest";
	private String receivedName;
	private String receivedPassword;
	private String message;
	private boolean receivedAnswer;
	private boolean loginError = true;
	private boolean joinError;
	private String text;
	private int roomNumber = -1;

	ServerThread(ServerWindow serverWindow, Socket s) throws IOException {
		setDaemon(true);
		this.setServerWindow(serverWindow);
		this.s = s; // socket fuer text
		pw = new PrintWriter(s.getOutputStream(), true);
		setScan(new Scanner(s.getInputStream()));
	}

	public void run() {
	try {
			while (true) { // text = nextLine, wenn nicht leer dann
			int methodIndex = getScan().nextInt();
				switch(methodIndex){
				case 1 : {login(); break;}
				case 2 : {joinRoom(); break;}
				case 3 : {sendChatMessage(); break;}
				case 4 : {privateChatStart(); break;}
				case 5 : {sendPrivateMessage(); break;}
				default : break;
				}
				
	
			}
		} catch (NoSuchElementException e) {
			getServerWindow().getTextAreaServer().append("User " + getUsername() + " hat sich abgemeldet\n");
			try {
				getServerWindow().getServerLogFile().writeBytes("User " + getUsername() + " hat sich abgemeldet\n");
			} catch (IOException e1) {
			}
			int j = 0;
			if(joinError == false && loginError == false){
			while (!(getUsername().equals(getServerWindow().getListAllUsers().getItem(j)))) {
				j++;
			}
			getServerWindow().getListAllUsers().remove(j);
			}
			j = 0;
			if(getRoomNumber() != -1 && !(username.equals("Guest"))){
			getServerWindow().broadcast(getRoomNumber(), 1);
			getServerWindow().broadcast(getRoomNumber(), "User " + getUsername() + " hat sich abgemeldet\n");
			userListChange(false, getRoomNumber(), getUsername());
			while (!(getServerWindow().getConnections().elementAt(getRoomNumber()).getUsersInRoom().elementAt(j).getUsername()).equals(getUsername())) {
				j++;
			}
			getServerWindow().getConnections().elementAt(getRoomNumber()).getUsersInRoom().remove(j);
			}
			for(int i = 0; i<getServerWindow().getPrivateChat().size();i++){
				if(getServerWindow().getPrivateChat().elementAt(i)[0].username.equals(username)){
					 getServerWindow().getPrivateChat().elementAt(i)[1].send(10);
			         getServerWindow().getPrivateChat().elementAt(i)[1].send(i);
			         getServerWindow().getPrivateChat().elementAt(i)[1].send("Dein Partner hat sich abgemeldet");
				}else{
				if(getServerWindow().getPrivateChat().elementAt(i)[1].username.equals(username)){
					 getServerWindow().getPrivateChat().elementAt(i)[0].send(10);
			         getServerWindow().getPrivateChat().elementAt(i)[0].send(i);
			         getServerWindow().getPrivateChat().elementAt(i)[0].send("Dein Partner hat sich abgemeldet");	
				}
				}
			}
			
			
			getServerWindow().getTabbedPane().setTitleAt(1, "Users (" + String.valueOf(getServerWindow().getListAllUsers().getItemCount()) + ")");
		}
		;
	}
	
	void sendPrivateMessage(){
	 int privateChatIndex = getScan().nextInt();
	 String text;
	 while((text = getScan().nextLine()).isEmpty()){};
	 
		if (privateChatIndex != -1) {
            getServerWindow().getPrivateChat().elementAt(privateChatIndex)[0].send(10);
            getServerWindow().getPrivateChat().elementAt(privateChatIndex)[0].send(privateChatIndex);
            getServerWindow().getPrivateChat().elementAt(privateChatIndex)[0].send(text);
            getServerWindow().getPrivateChat().elementAt(privateChatIndex)[1].send(10);
            getServerWindow().getPrivateChat().elementAt(privateChatIndex)[1].send(privateChatIndex);
            getServerWindow().getPrivateChat().elementAt(privateChatIndex)[1].send(text);
		}
	}

	void privateChatStart(){
		String	privateChatUserName;
		while((privateChatUserName = scan.nextLine()).isEmpty()){};
		for(int i = 0; i<serverWindow.getConnections().size(); i++){
			for(int j = 0; j<serverWindow.getConnections().elementAt(i).getUsersInRoom().size(); j++){
				if(privateChatUserName.equals(serverWindow.getConnections().elementAt(i).getUsersInRoom().elementAt(j).username)){
					serverWindow.getPrivateChat().add(new ServerThread[2]);
					int privateChatIndex = serverWindow.getPrivateChat().size() - 1;
					serverWindow.getPrivateChat().elementAt(privateChatIndex)[0] = this;
					serverWindow.getPrivateChat().elementAt(privateChatIndex)[1] = serverWindow.getConnections().elementAt(i).getUsersInRoom().elementAt(j);
					send(9);
					send(privateChatIndex);
					send(serverWindow.getConnections().elementAt(i).getUsersInRoom().elementAt(j).username);
					serverWindow.getConnections().elementAt(i).getUsersInRoom().elementAt(j).send(9);
					serverWindow.getConnections().elementAt(i).getUsersInRoom().elementAt(j).send(privateChatIndex);
					serverWindow.getConnections().elementAt(i).getUsersInRoom().elementAt(j).send(this.username);
					break;
				}
			}
		}
		
	}
	
	void send(String message) { // text sende methode
		try {
			pw.println(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void send(int methodIndex) { // text sende methode
		try {
			pw.println(methodIndex);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void login() {
		setLoginError(true);
		boolean alreadyOnline=false;
		try {
			receivedAnswer = getScan().nextBoolean(); // entscheidet, ob Benutzer
		         								// registrieren oder
													// anmelden will
			while ((receivedName = getScan().nextLine()).isEmpty()) {
			}
			;
			receivedPassword = getScan().nextLine();
			if (receivedAnswer == false) { // Fuer empfangene Antwort FALSE,
											// Registrieren ausgewaehlt
				
				getServerWindow().getDatafile().seek(0);
				while ((getServerWindow().getDatafile().getFilePointer() != getServerWindow().getDatafile().length())
						&& !(getServerWindow().getDatafile().readLine().equalsIgnoreCase(receivedName))) { // Datei
																											// durchlaufen
																											// und
																											// mit
																											// Benutzername
																											// vergleichen
					getServerWindow().getDatafile().readLine();
				}
				if (getServerWindow().getDatafile().getFilePointer() == getServerWindow().getDatafile().length()) { // 1.
																													// Fall:
																													// Benutzer
																													// wurde
																													// in
																													// der
																													// Datei
																													// nicht
																													// gefunden
					setLoginError(false);
					getServerWindow().getDatafile().writeBytes(receivedName + "\n" + receivedPassword + "\n"); // Also
																												// neuen
																												// Benutzer
																												// in
																												// die
																												// Datei
																												// schreiben.
					this.setUsername(receivedName); //
					setMessage("Benutzer wurde angelegt");
					getServerWindow().getTextAreaServer().append("Neuer Benutzer (" + getUsername() + ") wurde angelegt.\n");
					try {
						getServerWindow().getServerLogFile().writeBytes("Neuer Benutzer (" + getUsername() + ") wurde angelegt.\n");
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}

				} else { // 2. Fall: Benutzername schon vorhanden
					setLoginError(true);
					setMessage("Benutzername schon vorhanden.");
				}
			} // Ende if

			else { // Fuer empfangene Antwort TRUE, Anmelden ausgewaehlt

				
				for(int i =0; i<serverWindow.getListAllUsers().getItemCount(); i++){
					if(serverWindow.getListAllUsers().getItem(i).equals(receivedName)){
						alreadyOnline = true;
						break;
					}else{alreadyOnline = false;}
				}
				
				if(alreadyOnline == true){setLoginError(true); message = "Dieser Benutzer ist schon eingeloggt.";}else{
				getServerWindow().getDatafile().seek(0);
				while ((getServerWindow().getDatafile().getFilePointer() != getServerWindow().getDatafile().length())
						&& !(getServerWindow().getDatafile().readLine().equalsIgnoreCase(receivedName))) {
					getServerWindow().getDatafile().readLine(); // File
																// durchlaufen
																// und nach dem
																// Username
																// suchen
				}

				if (getServerWindow().getDatafile().getFilePointer() == getServerWindow().getDatafile().length()) { // 1.
																													// Fall:
																													// Pointer
																													// am
																													// Ende,
																													// Name
																													// wurde
																													// nicht
																													// gefunden
					setLoginError(true);
					setMessage("Benutzername wurde nicht gefunden.");
				} else { // 2. Fall: Name wurde gefunden
					String password2 = getServerWindow().getDatafile().readLine();

						if (receivedPassword.equals(password2)) { // dann
																	// vergleiche
																	// Passwort
							this.setUsername(receivedName);
							setLoginError(false);
							setMessage("Erfolgreich angemeldet");

							getServerWindow().getBannedUserFile().seek(0);
							String name = "";
							while ((getServerWindow().getBannedUserFile().getFilePointer() != getServerWindow().getBannedUserFile().length())
									&& !((name = getServerWindow().getBannedUserFile().readLine()).equalsIgnoreCase(receivedName))) { // Datei
																																		// durchlaufen
																																		// und
																																		// mit
								// Benutzername vergleichen
							}
							if (getServerWindow().getBannedUserFile().getFilePointer() == getServerWindow().getBannedUserFile().length()
									&& !(name.equals(receivedName))) {
								setLoginError(false);
								setMessage("Erfolgreich angemeldet");
								getServerWindow().getTextAreaServer().append(getUsername() + " hat sich angemeldet." + "\n");
								getServerWindow().getServerLogFile().writeBytes(getUsername() + " hat sich angemeldet." + "\n");
							} else {
								setLoginError(true);
								setMessage("Du bist gebannt.");

							}

						}

						else { // Falsches Passwort
							setLoginError(true);
							setMessage("Falsches Passwort");

						}

					}

					getServerWindow().getDatafile().seek(0);// Ende else
				}
			} // Ende else
	
			pw.println(loginError);
			pw.println(message);
			if (loginError == false) {
				roomNumber = 0;
				pw.println(getServerWindow().getServerName());
				getServerWindow().getListAllUsers().add(username);
				getServerWindow().getTabbedPane().setTitleAt(1,
						"Benutzer (" + String.valueOf(getServerWindow().getListAllUsers().getItemCount()) + ")");
			    sendRoomList();
				scan.nextBoolean();
				sendUserList();
				scan.nextBoolean();
				userListChange(true, getRoomNumber(), username);
				getServerWindow().getConnections().elementAt(getRoomNumber()).getUsersInRoom().add(this);
				scan.nextBoolean();
				getServerWindow().broadcast(0, 1);
				getServerWindow().broadcast(0,
						getUsername() + " hat den Raum [" + getServerWindow().getConnections().elementAt(0).getRoomName() + "] betreten.");
				
			}
			else {
			}
		} catch (Exception e7) {
		}
	}

	void sendRoomList() {
		if (isJoinError() == false) {
			pw.println(this.getServerWindow().getConnections().size()); // Sende roomNumber
			boolean b = getScan().nextBoolean();
			if (b == true) {
				for (int i = 0; i < this.getServerWindow().getConnections().size(); i++) {
					pw.println(this.getServerWindow().getConnections().elementAt(i).getRoomName()); // alle Raumnamen senden
				}
			}
		}
	}

	void sendUserList() {
		pw.println(true);
		pw.println(getServerWindow().getConnections().elementAt(getRoomNumber()).getUsersInRoom().size());
		for (int i = 0; i < getServerWindow().getConnections().elementAt(getRoomNumber()).getUsersInRoom().size(); i++) {
			pw.println(getServerWindow().getConnections().elementAt(getRoomNumber()).getUsersInRoom().elementAt(i).getUsername());
		}
	}

	void userListChange(boolean b, int roomNumber, String userName) {
		if (b == true){
		serverWindow.broadcast(roomNumber,6);}
		else{serverWindow.broadcast(roomNumber,7);}
		serverWindow.broadcast(roomNumber,userName);
	}

	void joinRoom() {
		if (joinError == false) {
			if (getScan().nextBoolean() == true) { // MainWindow										
				int i = getRoomNumber();
				if (i != -1) {
					for (int j = 0; j < getServerWindow().getConnections().elementAt(i).getUsersInRoom().size(); j++) {
						if (getServerWindow().getConnections().elementAt(i).getUsersInRoom().elementAt(j).getUsername()
								.equals(getUsername())) {
							getServerWindow().getConnections().elementAt(i).getUsersInRoom().remove(j);
							userListChange(false, i, getUsername());
							break;
						}
					}
				}
			}
		}
		if (joinError == false) {
			try {
				getServerWindow().getBannedUserFile().seek(0);
				String name = "";
				while ((getServerWindow().getBannedUserFile().getFilePointer() != getServerWindow().getBannedUserFile().length())
						&& !((name = getServerWindow().getBannedUserFile().readLine()).equalsIgnoreCase(this.getUsername()))) { // Datei
																																// durchlaufen
																																// und
																																// mit
					// Benutzername vergleichen
				}
				if (getServerWindow().getBannedUserFile().getFilePointer() == getServerWindow().getBannedUserFile().length()
						&& !(name.equals(receivedName))) { 
					// 1. Fall: Pointer am Ende, Name wurde nicht gefunden
				} else {
					joinError = true;
					message = "Du bist gebannt.";
				}
			} catch (IOException e) {
			} catch (NullPointerException e2) {
			}
			;
			send(8); //8te methode changeRoom
			pw.println(isJoinError()); // mainWindow zeile 499
			System.out.println("methode 8 gestartet");
			if (joinError == true) {
				pw.println(getMessage()); //MainWindow 504
			} 
	
			if (joinError == false) {
				int i1 = getScan().nextInt();// MainWindow 510
				System.out.println(i1);
				userListChange(true, i1, getUsername());
				this.getServerWindow().getConnections().elementAt(i1).getUsersInRoom().add(this); // neuer ServerThread wird im Vector abgelegt
				this.setRoomNumber(i1);
				sendUserList();
				getScan().nextBoolean();
				getServerWindow().broadcast(i1, 1);
				getServerWindow().broadcast(i1,
						this.getUsername() + " hat den Raum [" + getServerWindow().getConnections().elementAt(i1).getRoomName() + "] betreten!\n");
				getServerWindow().getTextAreaServer().append(
						this.getUsername() + " hat den Raum [" + getServerWindow().getConnections().elementAt(i1).getRoomName() + "] betreten!\n");
				try {
					getServerWindow().getServerLogFile().writeBytes(
							this.getUsername() + " hat den Raum [" + getServerWindow().getConnections().elementAt(i1).getRoomName() + "] betreten!"
									+ "\n");
				} catch (IOException e) {
				}
				;
			}
		}
		// catch (NoSuchElementException e1)
		// {this.serverWindow.listAllUsers.remove(this.username);}
		// catch(IOException e) {};
	} // Ende joinRoom

	
	public void sendChatMessage(){
		text = getScan().nextLine();
		while ((text.isEmpty())) {
			text = getScan().nextLine();
		}
		getServerWindow().getTextAreaServer().append(text + "\n");
		try {
			getServerWindow().getServerLogFile().writeBytes(text + "\n");
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if (getRoomNumber() != -1) {
			getServerWindow().broadcast(getRoomNumber(), 1);
			getServerWindow().broadcast(getRoomNumber(), text);
		}
	}
	
	void sendRoomListChange( String s) { //raum hinzufügen/entfernen
		pw.println(s);

	}

	void sendRoomListChange(String sa, String sn) { //raumnamen ändern
		pw.println(sa);
		pw.println(sn);

	}

	void serverNameChange(String serverName) {
		pw.println(serverName);
	}
	
	public boolean isLoginError() {
		return loginError;
	}

	public void setLoginError(boolean loginError) {
		this.loginError = loginError;
	}

	public ServerWindow getServerWindow() {
		return serverWindow;
	}

	public void setServerWindow(ServerWindow serverWindow) {
		this.serverWindow = serverWindow;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isJoinError() {
		return joinError;
	}

	public void setJoinError(boolean joinError) {
		this.joinError = joinError;
	}

	public Scanner getScan() {
		return scan;
	}

	public void setScan(Scanner scan) {
		this.scan = scan;
	}

	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}

	public int getRoomNumber() {
		return roomNumber;
	}


}