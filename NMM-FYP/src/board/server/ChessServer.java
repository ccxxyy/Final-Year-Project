

package board.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import board.server.common.Command;
import board.server.common.MyUtil;
import board.server.common.PlayInfo;

/**
 * A chess server, that has 10 rooms, and each room can
 *  have two players and some watchers!
 * 
 * @author Administrator
 *
 */
public class ChessServer {
	public final static int DEFAULT_PORT = 8888;
	
	//names of rooms 
	private final String[] roomNames = {
			"room1", "room2", "room3", "room4", "room5",
			"room6", "room7", "room8", "room9", "room10"
	};
	
	private Map<String, Set<MyServerStub>> rooms = new HashMap<String, Set<MyServerStub>>();
	
	public ChessServer() {
		for(String rn : roomNames)
			rooms.put(rn, new HashSet<MyServerStub>());
	}
	
	public String[] queryRooms() {
		return roomNames;
	}
	
	public void processPlayEvent(PlayInfo pif, MyServerStub stub) {
		System.out.println("Play: " + pif);
		Set<MyServerStub> stubs = this.rooms.get(pif.roomName);
		for(MyServerStub s : stubs) {
			if(s!=stub) {
				s.sentToClient(pif);
			}
		}
	}
	
	 
	public void doRegister(PlayInfo pif, MyServerStub stub) {
		Set<MyServerStub> mssl = rooms.get(pif.roomName);
		switch(pif.type) {
		case Command.TYPE_REGIST_PLAYER:
			stub.isPlayer = true;
			mssl.add(stub);
			break;
		case Command.TYPE_REGIST_WATHCH:
			stub.isPlayer = false;
			mssl.add(stub);
			break;
		case Command.TYPE_UNREGIST:
			mssl.remove(stub);
			break;
		}
	}
	
	// send message to players who are in the same room
	public void notifyRoom(PlayInfo pif, MyServerStub stub) {
		Set<MyServerStub> stubs = this.rooms.get(pif.roomName);
		if(stubs==null)
			return;
		for(MyServerStub s : stubs) {
			if(s!=stub) 
				s.sentToClient(pif);
		}
	}
	
	// send message to all the server
	public void notifyServer(PlayInfo pif, MyServerStub stub) {
		Iterator<Set<MyServerStub>> iter = this.rooms.values().iterator();
		while(iter.hasNext()) {
			Iterator<MyServerStub> i = iter.next().iterator();
			while(i.hasNext()) {
				MyServerStub s = i.next();
				if(s!=stub)
					s.sentToClient(pif);
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("Server Starting...");
		ChessServer chessServer = new ChessServer();
		ServerSocket socketServer = new ServerSocket(DEFAULT_PORT);
		while (true) {
			final Socket socket = socketServer.accept();
			System.out.println("Accepted a client " + MyUtil.getClientInfo(socket) );
			new MyServerStub(socket, chessServer).beginWork();

		}
	}

}
