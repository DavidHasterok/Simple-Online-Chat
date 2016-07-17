import java.util.*;

class Room {

	private String roomName;
	private Vector<ServerThread> usersInRoom = new Vector<ServerThread>(64);

	Room(String s) {
		this.setRoomName(s);
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public Vector<ServerThread> getUsersInRoom() {
		return usersInRoom;
	}

	public void setUsersInRoom(Vector<ServerThread> usersInRoom) {
		this.usersInRoom = usersInRoom;
	}

}