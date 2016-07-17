import java.net.*;
import java.io.*;
import java.util.*;

import javax.swing.JOptionPane;

class ClientThread extends Thread {
	private Socket s;
	private MainWindow mw;
	private Scanner in;
	int i =0;
	private boolean nsee = false;
  	ClientThread(MainWindow mw) {

		setDaemon(true);
		this.mw = mw;
	}

	public void run() {

		while (true) { // solange wie run laeuft
	
			try{
			int methodIndex;		
			methodIndex = getIn().nextInt();
		switch(methodIndex){
		case 1:  {gotChatMessage(); break;}
		case 2 : {addRoom(); break;}
		case 3 : {changeRoomName(); break;}
		case 4 : {removeRoom(); break;}	
		case 5 : {changeServerName(); break;}
		case 6 : {addUser(); break;}
		case 7 : {removeUser(); break;}
		case 8 : {changeRoom(); break;}
		case 9 : {startPrivateChat(); break;}
		case 10 : {gotPrivateMessage(); break;}
		case 11 : {gotBanned(); break;}
		}
			}catch(NoSuchElementException e){
				if(nsee == false){
				JOptionPane.showMessageDialog(null, "Keine Verbindung zum Server.", "Fehler...", JOptionPane.OK_CANCEL_OPTION);
				mw.getListRooms().removeAll();
				mw.getListUsers().removeAll();
				nsee = true;
				}
				}
		}
	}

public void	startPrivateChat(){
		int privateChatIndex = getIn().nextInt();
		PrivatChat pchat = new PrivatChat();
		String chatWith;
		while((chatWith = getIn().nextLine()).isEmpty()){}
		pchat.setTitle("PrivatChat mit " + chatWith);
		pchat.setMw(this.mw);
		pchat.setPrivateChatIndex(privateChatIndex);
		this.mw.getPrivatChats().add(pchat);
		pchat.open();
	}

public void gotPrivateMessage(){
	int privateChatIndex = getIn().nextInt();
	String text;
	while((text = getIn().nextLine()).isEmpty()){};
	for(int i = 0; i< mw.getPrivatChats().size(); i++){
		if(mw.getPrivatChats().elementAt(i).getPrivateChatIndex() == privateChatIndex ){
			if(mw.getPrivatChats().elementAt(i).isVisible() == false){
				mw.getPrivatChats().elementAt(i).setVisible(true);
			}
			mw.getPrivatChats().elementAt(i).getTextArea().append(text + "\n");
			mw.getPrivatChats().elementAt(i).toFront();
		}
			
	}
}

public void gotBanned(){
	mw.getRaumNameLabel().setText("Du bist in keinem Raum.");
	mw.getListUsers().removeAll();
	mw.setJoinError(true);
	mw.setRoomIndex(-1);
	for(int i = 0; i<mw.getPrivatChats().size();i++){
		mw.getPrivatChats().elementAt(i).setPrivateChatIndex(-1);
	}
	
}

	public void changeRoom(){
		mw.setJoinError(in.nextBoolean()); // serverThread zeile// 216
		System.out.println(mw.isJoinError());
		if (mw.isJoinError() == true) {
			while ((mw.setReceivedMessage(in.nextLine())).isEmpty()) { //ServerThread 368
			}
			; // ServerThread Zeile 218
			JOptionPane.showMessageDialog(null, mw.getReceivedMessage(), "Fehler...", JOptionPane.OK_CANCEL_OPTION);
		} else {
			mw.getOut().println(mw.getListRooms().getSelectedIndex()); // ServerThread 372
			System.out.println(mw.getListRooms().getSelectedIndex());
			receiveUserList();
			mw.getOut().println(true);
			mw.getTextArea().setText("");
			mw.getRaumNameLabel().setText("Du bist als " + mw.getUserName() + " im Raum " + "\"" + mw.getListRooms().getSelectedItem() + "\".");
			mw.setRoomIndex(mw.getListRooms().getSelectedIndex() + 1);
		}
	}
	
	public void addUser(){
		String userName;
		while((userName = getIn().nextLine()).isEmpty()){}
		mw.getListUsers().add(userName);
	}
	
	public void removeUser(){
		String userName;
		while((userName = getIn().nextLine()).isEmpty()){}
		for(int i = 0; i < mw.getListUsers().getItemCount(); i++){
			if(userName.equals(mw.getListUsers().getItem(i))){mw.getListUsers().remove(i); break;}
		}
	}
	
	public void receiveUserList(){
		boolean a = in.nextBoolean();
		if (a == true) {
			mw.getListUsers().removeAll();
			int userNumber;
			userNumber = in.nextInt();
			String users;
			for (int j = 0; j < userNumber; j++) {
				while ((users = in.nextLine()).isEmpty()) {
				};
				mw.getListUsers().add(users);
			}
		}	
	}
	
	public void gotChatMessage(){
		String text;
		while((text = getIn().nextLine()).isEmpty()){};
		mw.getTextArea().append(text + "\n");
		if (text.equals("Du wurdest aus dem Raum gekickt.")) {
			mw.getRaumNameLabel().setText("Du bist in keinem Raum");
			mw.setRoomIndex(-1);
			mw.getListUsers().removeAll();
		}
	}
	
	void connect() {
		try {
			this.setS(new Socket("127.0.0.1", 1234));
				setIn(new Scanner(getS().getInputStream())); // Scanner
			} catch (IOException e) {
		    } catch (NoSuchElementException e) {
		    	if( i == 0){
			    	JOptionPane.showMessageDialog(null, "Keine Verbindung zum Server.", "Fehler...", JOptionPane.OK_CANCEL_OPTION);
			    	mw.getListUsers().removeAll();
			    	mw.getListRooms().removeAll();
			  }
			    i++;
		 }
	}
	
	void addRoom(){
		String roomName;
		String text;
		while((roomName = getIn().nextLine()).isEmpty()){}
		while((text = getIn().nextLine()).isEmpty()){};
		mw.getTextArea().append(text + "\n");
		mw.getListRooms().add(roomName);
	}
	
	void removeRoom(){

		String roomName;
		String text;
		while((roomName = getIn().nextLine()).isEmpty()){}
	    int i = 0;
		while(!(mw.getListRooms().getItem(i).equals(roomName))){
			i++;
		}
		if(mw.getRoomIndex()-1 == i){
		while((text = getIn().nextLine()).isEmpty()){};
		mw.getTextArea().append(text + "\n");
		mw.setRoomIndex(-1);
		}
		if(mw.getRoomIndex()-1 > i){
			mw.setRoomIndex(mw.getRoomIndex() - 1);
		}
		mw.getListRooms().remove(i);
		}
	void changeRoomName(){

		String roomNameOld;
		String roomNameNew;
		String text;
		while((roomNameOld = getIn().nextLine()).isEmpty()){}
		while((roomNameNew = getIn().nextLine()).isEmpty()){}		
		while((text = getIn().nextLine()).isEmpty()){};
		mw.getTextArea().append(text + "\n");
		int i = 0;
		while(!(mw.getListRooms().getItem(i).equals(roomNameOld))){
			i++;
		}
		mw.getListRooms().remove(i);
		mw.getListRooms().add(roomNameNew, i);
		mw.getRaumNameLabel().setText("Du bist als " + mw.getUserName() + " im Raum " + "\"" + mw.getListRooms().getItem(mw.getRoomIndex() -1) + "\".");
	}

	void changeServerName(){
		String serverName;
		while((serverName = getIn().nextLine()).isEmpty()){}
		mw.setServerName(serverName);
		mw.getLabelConnection().setText("Du bist verbunden mit dem Server: " + "\"" + serverName + "\"");
	}

	public Socket getS() {
		return s;
	}

	public void setS(Socket s) {
		this.s = s;
	}

	public void setIn(Scanner in) {
		this.in = in;
	}

	public Scanner getIn() {
		return in;
	}
}
