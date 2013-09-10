package board.server.common;

public class ConnectInfo {
	private String host, room, username;
	private int port;

	public ConnectInfo(String host, int port, String room, String username) {
		this.host = host;
		this.port = port;
		this.room = room;
		this.username = username;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
