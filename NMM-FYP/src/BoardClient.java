import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import board.server.common.BoardEventPool;
import board.server.common.CanConnect;
import board.server.common.CanPlay;
import board.server.common.Command;
import board.server.common.ConnectInfo;
import board.server.common.MyUtil;
import board.server.common.PlayInfo;

public class BoardClient implements CanPlay, CanConnect {
	public final static int DEFAULT_PORT = 8888;
	private Socket server;
	private Gui game;
	private String groupId;
	public boolean isRegisted = false;
	public boolean isPlayer = false;
	public String ln = System.getProperty("line.separator");
	
	private Thread readThread, writeThread;
	
	private BoardEventPool<PlayInfo> outEventPool = new BoardEventPool<PlayInfo>(10);
	
	public BoardClient() {
		game = new Gui("Nine Men's Morris", this, this);
		game.CreateWindow();
		game.setVisible(true);
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public static void main(String[] args) throws Exception {
		BoardClient client = new BoardClient();
	}

	@Override
	public boolean connect(ConnectInfo cif) throws Exception {
		server = new Socket(InetAddress.getByName(cif.getHost()), cif.getPort());
		System.out.println(String.format("Connection to server %s:%s successfully!", cif.getHost(), cif.getPort()));
//		final BufferedReader in = new BufferedReader(new InputStreamReader(
//				server.getInputStream()));
//		final PrintWriter out = new PrintWriter(server.getOutputStream());
		final InputStream in = server.getInputStream();
		final OutputStream out = server.getOutputStream();
		
		// write event to other players
		writeThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						MyUtil.writeObject(out, outEventPool.get());
					} catch (Exception e) {
						e.printStackTrace();
						break;
					}
				}
			}

		});

		// Receive the event from other players;
		readThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						processResult((PlayInfo)MyUtil.readObject(in));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
				}
			}

		});
		
		readThread.start();
		writeThread.start();
		Thread.sleep(500);
		
		this.outEventPool.put(PlayInfo.buildRegistEvent(cif));  //do registry
		this.isPlayer = true;
		this.isRegisted = true;
		
		
		
		return true;
	}

	@Override
	public boolean disconnect() throws Exception {
		this.server.close();
		return true;
	}
	

	// can process three messages
	private void processResult(PlayInfo pif) {
		if(pif==null)
			return;
		game.doEvent(pif);
	}

	@Override
	public void doEvent(PlayInfo pif) {
		if(!(isRegisted&&isPlayer)){
			return;
		}
		try {
			this.outEventPool.put(pif);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
