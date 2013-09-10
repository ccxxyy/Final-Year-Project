package board.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import board.server.common.BoardEventPool;
import board.server.common.Command;
import board.server.common.MyUtil;
import board.server.common.PlayInfo;

public class MyServerStub {

	private String clientKey;
	ChessServer server;
	public boolean isPlayer = true;
	private Runnable writeThread;
	private Runnable readThread;
	
	private BoardEventPool<PlayInfo> eventPool = new BoardEventPool<PlayInfo>(10);

	public MyServerStub(Socket socket, ChessServer server)
			throws IOException {
		this.server = server;
		this.clientKey = MyUtil.getClientInfo(socket);
//		this.clientKey = MyUtil.getClientID(socket);
//		final BufferedReader in = new BufferedReader(new InputStreamReader(
//				socket.getInputStream()));
//		final PrintWriter out = new PrintWriter(socket.getOutputStream());
		final InputStream in = socket.getInputStream();
		final OutputStream out = socket.getOutputStream();
		
		// write event to this player
		this.writeThread = new Runnable(){
			@Override
			public void run() {
				while (true) {
					try {
						MyUtil.writeObject(out, eventPool.get());
					} catch (Exception e) {
						e.printStackTrace();
						break;
					}
				}
			}
			
		};
				
		// send command to other players;
		this.readThread = new Runnable(){
			@Override
			public void run() {
				while (true) {
					try {  
						processCommand((PlayInfo)MyUtil.readObject(in));
					}  catch (Exception e) {
						e.printStackTrace();
						break;
					}
				}
			}
		};				
	}
	
	public void beginWork() {
		new Thread(this.writeThread).start();
		new Thread(this.readThread).start();
	}
	
	public void sentToClient(PlayInfo pif) {
		try {
			eventPool.put(pif);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void processCommand(PlayInfo pif) {
		System.out.println(this.clientKey + "=>" + pif.toString());
		switch(pif.type) {
		case Command.TYPE_MESSAGE_ALL :	
			this.server.notifyServer(pif, this);
			break;
		case Command.TYPE_MESSAGE_GROUP:
			this.server.notifyRoom(pif, this);
			break;
		case Command.TYPE_PLAY_EVENT:
			this.server.processPlayEvent(pif, this);
			break;
		case Command.TYPE_REGIST_PLAYER:
		case Command.TYPE_REGIST_WATHCH:
		case Command.TYPE_UNREGIST:
			this.server.doRegister(pif, this);
			break;
		}
	}


}
