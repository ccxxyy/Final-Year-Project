package board.server.common;

import java.io.Serializable;

public class PlayInfo implements Serializable {

	private static final long serialVersionUID = -391725658759269208L;
	
	public int type;
	public String roomName;
	public String userName;
	public String message;
	public int x,y;
	
	public static PlayInfo buildPlayEvent(String room, String name, int x, int y) {
		return new PlayInfo(Command.TYPE_PLAY_EVENT, room, name, "", x, y);
	}
	
	public static PlayInfo buildMsgEvent(ConnectInfo cif, String msg) {
		return new PlayInfo(Command.TYPE_MESSAGE_GROUP, cif.getRoom(), cif.getUsername(), msg, 0, 0);
	}
	
	public static PlayInfo buildRegistEvent(ConnectInfo cif) {
		return new PlayInfo(Command.TYPE_REGIST_PLAYER, cif.getRoom(), cif.getUsername(), "", 0, 0);
	}
	
	public PlayInfo(int type, String roomName, String userName, String message, int x, int y) {
		this.type = type;
		this.roomName = roomName;
		this.userName = userName;
		this.message = message;
		this.x = x;
		this.y = y;
	}
	
	public String toString() {
		String rt = "";
		switch(type) {
		case Command.TYPE_MESSAGE_ALL:
			rt = "[Message] ";
			break;
		case Command.TYPE_MESSAGE_GROUP:
			rt = "[Message] ";
			break;
		case Command.TYPE_PLAY_EVENT:
			rt = "[Play] ";
			break;
		case Command.TYPE_REGIST_PLAYER:
			rt = "[Regist] ";
			break;
		case Command.TYPE_REGIST_WATHCH:
			rt = "[Regist] ";
			break;
		case Command.TYPE_UNREGIST:
			rt = "[Unregist] ";
			break;
		}
		rt += "Room:" + roomName + ", UserName:" + userName + ", Message: " + message + ", pos[" + x  +"," + y + "]";
		return rt;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	
}
