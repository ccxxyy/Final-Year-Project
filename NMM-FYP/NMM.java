import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.Serializable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.*;
import java.awt.*;
import java.util.Vector;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;





/**
 * A chess server, that has 10 rooms, and each room can
 *  have two players and some watchers!
 * 
 * @author Administrator
 *
 */
class ChessServer {
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


class BoardEventPool<T> {

	private final List<T> eventPool;
	private int tail;
	private int head;
	private int count;

	public BoardEventPool(int count) {
		this.eventPool = new ArrayList<T>(count);
		this.head = 0;
		this.tail = 0;
		this.count = 0;
	}

	public synchronized void put(T event) throws InterruptedException {
		while (count > eventPool.size()) {
			wait();
		}
		eventPool.add(tail, event);
		tail = (tail + 1) % eventPool.size();
		count++;
		notifyAll();
	}

	public synchronized T get() throws InterruptedException {
		while (count <= 0) {
			wait();
		}
		T event = eventPool.get(head);
		head = (head + 1) % eventPool.size();
		count--;
		notifyAll();
		return event;
	}

}

interface CanConnect {
	public boolean connect(ConnectInfo cif) throws Exception ;
	public boolean disconnect() throws Exception ;
}

interface CanPlay {
	public void doEvent(PlayInfo pif);
}


class Command {
	public final static int TYPE_PLAY_EVENT 	= 1;  // play event
	public final static int TYPE_REGIST_PLAYER = 2;  // register to play
	public final static int TYPE_REGIST_WATHCH = 3;  // register to watch play
	public final static int TYPE_UNREGIST 		= 4;  // unregister
	public final static int TYPE_MESSAGE_GROUP = 5;  // send message to group
	public final static int TYPE_MESSAGE_ALL 	= 6;  // send message to all
	
	public int type;
	public String value;
	
	
	public Command(int type, String command) {
		this.type = type;
		this.value = command;
	}
	
	public Command(String value) {
		String[] cl = value.split(":");
		this.type = Integer.parseInt(cl[0]);
		this.value = cl[1];
	}
	
	public String build() {
		return String.format("%s:%s", type, value);
	}
	
}

class ConnectInfo {
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


class MyUtil {
	
	public static void writeObject(OutputStream out, Object obj) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(out);  
        oos.writeObject(obj);  
        oos.flush();
	}
	
	public static Object readObject(InputStream in) throws IOException, ClassNotFoundException {
		ObjectInputStream oos = new ObjectInputStream(in);  
        return oos.readObject();
	}
	
	
	public static String MD5(String val) {
		byte[] source = val.getBytes();
		String s = null;
		char hexDigits[] = { 
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
				'e', 'f' };
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest(); 
			char str[] = new char[16 * 2]; 
			int k = 0; 
			for (int i = 0; i < 16; i++) { 
				byte byte0 = tmp[i]; 
				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; 
				str[k++] = hexDigits[byte0 & 0xf]; 
			}
			s = new String(str);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	
	public static boolean isboolIp(String ipAddress)  
	{  
	       String ip = "([1-9]|[1-9]//d|1//d{2}|2[0-4]//d|25[0-5])(//.(//d|[1-9]//d|1//d{2}|2[0-4]//d|25[0-5])){3}";   
	       Pattern pattern = Pattern.compile(ip);   
	       Matcher matcher = pattern.matcher(ipAddress);   
	       return matcher.matches();   
	}  
	
	public static String getClientInfo(Socket client) {
		String str = client.getInetAddress().toString() + ":" + client.getPort();
		return str;
	}
	
	public static String getClientID(Socket client) {
		String str = client.getInetAddress().toString() + ":" + client.getPort();
		return MyUtil.MD5(str);
	}
}


class PlayInfo implements Serializable {

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



class HelpAbout extends JDialog implements ActionListener {
	
	private JLabel hostLabel = new JLabel("HOST:");
	private JLabel userLabel = new JLabel("USER:");
	private JLabel roomLabel = new JLabel("ROOM:");
	
	private JTextField host = new JTextField();
	private JTextField user = new JTextField();
	private JTextField room = new JTextField();
	
	private JButton submit = new JButton("submit");
	private JButton reset = new JButton("reset");
	
	private String hostStr, userStr, roomStr;

	public HelpAbout(JFrame mainFrame)
    {
		  setLayout(null);
		  hostLabel.setBounds(50, 20, 100, 20);
		  host.setBounds(150, 20, 150, 20);
		  add(hostLabel);
		  add(host);
		  add(userLabel);
		  add(user);
		  add(roomLabel);
		  add(room);
		  add(submit);
		  submit.addActionListener(this);
		  reset.addActionListener(this);
          setVisible(true);
    }

	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==submit){
			hostStr = host.getText();
			userStr =user.getText();
			roomStr =room.getText();
			this.dispose();
		}
	}
	
	
	
	public String getHostStr() {
		return hostStr;
	}

	public void setHostStr(String hostStr) {
		this.hostStr = hostStr;
	}

	public String getUserStr() {
		return userStr;
	}

	public void setUserStr(String userStr) {
		this.userStr = userStr;
	}

	public String getRoomStr() {
		return roomStr;
	}

	public void setRoomStr(String roomStr) {
		this.roomStr = roomStr;
	}

	public static void main(String[] args) {
		JFrame main = new JFrame();
		HelpAbout aboutDialog=new HelpAbout(main);
		aboutDialog.setSize(500,500);
		aboutDialog.show();
		System.out.println(aboutDialog.getHostStr());
	}
}



class MyServerStub {

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

class Board extends JPanel {
	
	//Gui GUI= new Gui();
	private	Vector<Integer> redStateStore = new Vector<Integer>();
	private	Vector<Integer> blueStateStore = new Vector<Integer>();
	
	int blueCounter=9;
	int redCounter=9;
	int total;
	
	
	public void set(int a, int b){
		redCounter=a;
		blueCounter=b;
	}
	
	public void set(Vector<Integer> a,Vector<Integer> b){
		redStateStore=a;
		blueStateStore=b;
	}
	public void passBoard(){
		

//		 JFrame f=new JFrame();
//	     f.setSize(300,300);
//	     Container cp=f.getContentPane();
//	     JScrollPane sp=new JScrollPane(ta);
//	     cp.add(sp);
//	     this.setBounds(180, 100, 600, 500); 
//	     f.setVisible(true);

		repaint();
	}
	
	public void paintComponent(Graphics g){
		//System.out.println("test successful3");
		
		super.paintComponent(g);
		Graphics2D g2d=(Graphics2D)g;
		Stroke stroke=new BasicStroke(3.0f);
		g2d.setStroke(stroke); 
		// outer square
		g2d.setColor(Color.black);
		
		g.drawLine(10, 10, 270, 10);
		//g.drawLine(150, 10, 260, 10);
		
		g.drawLine(270, 10, 270, 270);
		//g.drawLine(270, 150, 270, 260);
		
		g.drawLine(270, 270, 10, 270);
		//g.drawLine(130, 270, 20, 270);
		
		g.drawLine(10, 270, 10, 10);
		//g.drawLine(10, 130, 10, 20);
		
		// middle square
		g.drawLine(60, 60, 220, 60);
		//g.drawLine(150, 60, 210, 60);
		
		g.drawLine(220, 60, 220, 220);
		
		//g.drawLine(220, 150, 220, 210);
		
		g.drawLine(220, 220, 60, 220);
		//g.drawLine(130, 220, 70, 220);
		
		g.drawLine(60, 220, 60, 60);
		//g.drawLine(60, 130, 60, 70);
		
		// inner square
		g.drawLine(110, 110, 170, 110);
		//g.drawLine(150, 110, 160, 110);
		
		g.drawLine(170, 110, 170, 170);
		//g.drawLine(170, 150, 170, 160);
		
		g.drawLine(170, 170, 110, 170);
		//g.drawLine(130, 170, 120, 170);
		
		g.drawLine(110, 170, 110, 110);
		//g.drawLine(110, 130, 110, 120);
		
//		// positions on outer square
//		
//		g.drawRect(0, 0, 20, 20);
//		g.drawRect(130, 0, 20, 20);
//		g.drawRect(260, 0, 20, 20);
//		g.drawRect(260, 130, 20, 20);
//		g.drawRect(260, 260, 20, 20);
//		g.drawRect(130, 260, 20, 20);
//		g.drawRect(0, 260, 20, 20);
//		g.drawRect(0, 130, 20, 20);
//		
//		//position on middle square
//		g.drawRect(50, 50, 20, 20);
//		g.drawRect(130, 50, 20, 20);
//		g.drawRect(210, 50, 20, 20);
//		g.drawRect(210, 130, 20, 20);
//		g.drawRect(210, 210, 20, 20);
//		g.drawRect(130, 210, 20, 20);
//		g.drawRect(50, 210, 20, 20);
//		g.drawRect(50, 130, 20, 20);
//		
//		//position on inner square
//		g.drawRect(100, 100, 20, 20);
//		g.drawRect(130, 100, 20, 20);
//		g.drawRect(160, 100, 20, 20);
//		g.drawRect(160, 130, 20, 20);
//		g.drawRect(160, 160, 20, 20);
//		g.drawRect(130, 160, 20, 20);
//		g.drawRect(100, 160, 20, 20);
//		g.drawRect(100, 130, 20, 20);
		
		//lines connecting outer square to middle square
		g.drawLine(140, 10, 140, 110);
		g.drawLine(170, 140, 270, 140);
		g.drawLine(140, 170, 140, 270);
		g.drawLine(10, 140, 110, 140);
		
		//lines connecting middle square and inner square
		//g.drawLine(140, 70, 140, 100);
		//g.drawLine(180, 140, 210, 140);
		//g.drawLine(140, 180, 140, 210);
		//g.drawLine(70, 140, 100, 140);
		
		for(int m=1;m<=redCounter; m++){
			total = (25*m);
			g.setColor(Color.red);
			g.fillOval(total, 300, 20, 20);
		}
		
		for(int m=1;m<=blueCounter; m++){
			total = (25*m);
			g.setColor(Color.blue);
			g.fillOval(total, 340, 20, 20);
		}
		
		for(int i=0;i<redStateStore.size();i++){
			if(redStateStore.elementAt(i)==0){
				g.setColor(Color.red);
				g.fillOval(100, 100, 20, 20);
			}
			if(redStateStore.elementAt(i)==1){
				g.setColor(Color.red);
				g.fillOval(130, 100, 20, 20);	
			}
			if(redStateStore.elementAt(i)==2){
				g.setColor(Color.red);
				g.fillOval(160, 100, 20, 20);	
			}
			if(redStateStore.elementAt(i)==3){
				g.setColor(Color.red);
				g.fillOval(160, 130, 20, 20);	
			}
			if(redStateStore.elementAt(i)==4){
				g.setColor(Color.red);
				g.fillOval(160, 160, 20, 20);	
			}
			if(redStateStore.elementAt(i)==5){
				g.setColor(Color.red);
				g.fillOval(130, 160, 20, 20);	
			}
			if(redStateStore.elementAt(i)==6){
				g.setColor(Color.red);
				g.fillOval(100, 160, 20, 20);	
			}
			if(redStateStore.elementAt(i)==7){
				g.setColor(Color.red);
				g.fillOval(100, 130, 20, 20);	
			}
			if(redStateStore.elementAt(i)==8){
				g.setColor(Color.red);
				g.fillOval(50, 50, 20, 20);
			}
			if(redStateStore.elementAt(i)==9){
				g.setColor(Color.red);
				g.fillOval(130, 50, 20, 20);
			}
			if(redStateStore.elementAt(i)==10){
				g.setColor(Color.red);
				g.fillOval(210, 50, 20, 20);
			}
			if(redStateStore.elementAt(i)==11){
				g.setColor(Color.red);
				g.fillOval(210, 130, 20, 20);
			}
			if(redStateStore.elementAt(i)==12){
				g.setColor(Color.red);
				g.fillOval(210, 210, 20, 20);
			}
			if(redStateStore.elementAt(i)==13){
				g.setColor(Color.red);
				g.fillOval(130, 210, 20, 20);
			}
			if(redStateStore.elementAt(i)==14){
				g.setColor(Color.red);
				g.fillOval(50, 210, 20, 20);
			}
			if(redStateStore.elementAt(i)==15){
				g.setColor(Color.red);
				g.fillOval(50, 130, 20, 20);
			}
			if(redStateStore.elementAt(i)==16){
				g.setColor(Color.red);
				g.fillOval(0, 0, 20, 20);
			}
			if(redStateStore.elementAt(i)==17){
				g.setColor(Color.red);
				g.fillOval(130, 0, 20, 20);
			}
			if(redStateStore.elementAt(i)==18){
				g.setColor(Color.red);
				g.fillOval(260, 0, 20, 20);
			}
			if(redStateStore.elementAt(i)==19){
				g.setColor(Color.red);
				g.fillOval(260, 130, 20, 20);
			}
			if(redStateStore.elementAt(i)==20){
				g.setColor(Color.red);
				g.fillOval(260, 260, 20, 20);
			}
			if(redStateStore.elementAt(i)==21){
				g.setColor(Color.red);
				g.fillOval(130, 260, 20, 20);
			}
			if(redStateStore.elementAt(i)==22){
				g.setColor(Color.red);
				g.fillOval(0, 260, 20, 20);
			}
			if(redStateStore.elementAt(i)==23){
				g.setColor(Color.red);
				g.fillOval(0, 130, 20, 20);
			}
		}
		for(int i=0;i<blueStateStore.size();i++){
			if(blueStateStore.elementAt(i)==0){
				g.setColor(Color.blue);
				g.fillOval(100, 100, 20, 20);
			}
			if(blueStateStore.elementAt(i)==1){
				g.setColor(Color.blue);
				g.fillOval(130, 100, 20, 20);	
			}
			if(blueStateStore.elementAt(i)==2){
				g.setColor(Color.blue);
				g.fillOval(160, 100, 20, 20);	
			}
			if(blueStateStore.elementAt(i)==3){
				g.setColor(Color.blue);
				g.fillOval(160, 130, 20, 20);	
			}
			if(blueStateStore.elementAt(i)==4){
				g.setColor(Color.blue);
				g.fillOval(160, 160, 20, 20);	
			}
			if(blueStateStore.elementAt(i)==5){
				g.setColor(Color.blue);
				g.fillOval(130, 160, 20, 20);	
			}
			if(blueStateStore.elementAt(i)==6){
				g.setColor(Color.blue);
				g.fillOval(100, 160, 20, 20);	
			}
			if(blueStateStore.elementAt(i)==7){
				g.setColor(Color.blue);
				g.fillOval(100, 130, 20, 20);	
			}
			if(blueStateStore.elementAt(i)==8){
				g.setColor(Color.blue);
				g.fillOval(50, 50, 20, 20);
			}
			if(blueStateStore.elementAt(i)==9){
				g.setColor(Color.blue);
				g.fillOval(130, 50, 20, 20);
			}
			if(blueStateStore.elementAt(i)==10){
				g.setColor(Color.blue);
				g.fillOval(210, 50, 20, 20);
			}
			if(blueStateStore.elementAt(i)==11){
				g.setColor(Color.blue);
				g.fillOval(210, 130, 20, 20);
			}
			if(blueStateStore.elementAt(i)==12){
				g.setColor(Color.blue);
				g.fillOval(210, 210, 20, 20);
			}
			if(blueStateStore.elementAt(i)==13){
				g.setColor(Color.blue);
				g.fillOval(130, 210, 20, 20);
			}
			if(blueStateStore.elementAt(i)==14){
				g.setColor(Color.blue);
				g.fillOval(50, 210, 20, 20);
			}
			if(blueStateStore.elementAt(i)==15){
				g.setColor(Color.blue);
				g.fillOval(50, 130, 20, 20);
			}
			if(blueStateStore.elementAt(i)==16){
				g.setColor(Color.blue);
				g.fillOval(0, 0, 20, 20);
			}
			if(blueStateStore.elementAt(i)==17){
				g.setColor(Color.blue);
				g.fillOval(130, 0, 20, 20);
			}
			if(blueStateStore.elementAt(i)==18){
				g.setColor(Color.blue);
				g.fillOval(260, 0, 20, 20);
			}
			if(blueStateStore.elementAt(i)==19){
				g.setColor(Color.blue);
				g.fillOval(260, 130, 20, 20);
			}
			if(blueStateStore.elementAt(i)==20){
				g.setColor(Color.blue);
				g.fillOval(260, 260, 20, 20);
			}
			if(blueStateStore.elementAt(i)==21){
				g.setColor(Color.blue);
				g.fillOval(130, 260, 20, 20);
			}
			if(blueStateStore.elementAt(i)==22){
				g.setColor(Color.blue);
				g.fillOval(0, 260, 20, 20);
			}
			if(blueStateStore.elementAt(i)==23){
				g.setColor(Color.blue);
				g.fillOval(0, 130, 20, 20);
			}
		}
	}
	
}


class BoardClient implements CanPlay, CanConnect {
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


class BoardOperation {

	private	Vector<Integer> redStateStore = new Vector<Integer>();
	private	Vector<Integer> blueStateStore = new Vector<Integer>();
	int countRed=0;
	int countBlue=0;
	int red=0;
	int blue=0;
	int random=100;

	int selectedCounter;
	int count1,count2,count3,count4,count5,count6,count7,count8,count9,count10,count11,count12,
	count13,count14,count15,count16=0;
	
	public boolean randomSelect(){
		random=(int) Math.round(Math.random()*23);
//		System.out.println("random is "+random);
//		System.out.println("red is " +redStateStore);
//		System.out.println("blue is "+blueStateStore);
		for(int i=0;i<redStateStore.size();i++){
			if(random==redStateStore.elementAt(i)){
				return false;
			}
		}
		for(int j=0;j<blueStateStore.size();j++){
			if(random==blueStateStore.elementAt(j)){
				return false;
			}
		}
		addStateBlue(random);
		return true;
	}
	
	public boolean randomRemove(){
		random=(int) Math.round(Math.random()*23);
		for(int i=0;i<redStateStore.size();i++){
			if(random==redStateStore.elementAt(i)){
				removeStateRed(random);
				return true;
			}
		}
		return false;
	}
	
	public boolean randomMove(){
		random=(int) Math.round(Math.random()*23);
		for(int j=0;j<blueStateStore.size();j++){
			if(random==blueStateStore.elementAt(j)){
				for(int i=0; i<24;i++){
					if(detectIllegalMove(random,i)==true){
						removeStateBlue(random);
						addStateBlue(i);
						return true;
					}
				}
				
			}
		}
		return false;
	}
	
	public void chooseCounter(int p){
		selectedCounter=p;
	}
	public int getChooseCounter(){
		return selectedCounter;
	}
	
	public Vector<Integer> getRedVector(){
		return redStateStore;
	}
	public Vector<Integer> getBlueVector(){
		return blueStateStore;
	}
	
	public void addStateRed(int p){
		redStateStore.addElement(p);
	}
	
	public void addStateBlue(int p){
		blueStateStore.addElement(p);
	}
	
	public boolean detectSelectedRedCounter(int p){
		for(int i=0;i<redStateStore.size();i++){
			if(p==redStateStore.elementAt(i)){
				red++;
			}
		}
		if(red!=1){
			red=0;
			return false;
		}
		else{
			red=0;
			return true;
		}
	}
	
	public boolean detectSelectedBlueCounter(int p){
		for(int i=0;i<blueStateStore.size();i++){
			if(p==blueStateStore.elementAt(i)){
				blue++;
			}
		}
		if(blue!=1){
			blue=0;
			return false;
		}
		else{
			blue=0;
			return true;
		}
	}
	
	public boolean detectIllegalMove(int p, int q){
		
		for(int i=0;i<redStateStore.size();i++){
			if(q==redStateStore.elementAt(i)){
				return false;
			}
		}
		for(int j=0;j<blueStateStore.size();j++){
			if(q==blueStateStore.elementAt(j)){
				return false;
			}
		}
		
		if(q==p+1||q==p-1){
			return true;
		}
		if(p==0&&q==7){
			return true;
		}
		if(p==7&&q==0){
			return true;
		}
		if(p==8&&q==15){
			return true;
		}
		if(p==15&&q==8){
			return true;
		}
		if(p==16&&q==23){
			return true;
		}
		if(p==23&&q==16){
			return true;
		}
		if(p==1&&q==9){
			return true;
		}
		if(p==3&&q==11){
			return true;
		}
		if(p==5&&q==13){
			return true;
		}
		if(p==7&&q==15){
			return true;
		}
		if(p==17&&q==9){
			return true;
		}
		if(p==19&&q==11){
			return true;
		}
		if(p==21&&q==13){
			return true;
		}
		if(p==23&&q==15){
			return true;
		}
		if(p==9){
			if(q==1||q==17){
				return true;
			}
		}
		if(p==11){
			if(q==3||q==19){
				return true;
			}
		}
		if(p==13){
			if(q==5||q==21){
				return true;
			}
		}
		if(p==15){
			if(q==7||q==23){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkRedRemove(int p){
		for(int i=0;i<redStateStore.size();i++){
			if(p==redStateStore.elementAt(i)){
				return false;
			}
		}
		return true;
	}
	
	public boolean checkBlueRemove(int p){
		for(int i=0;i<blueStateStore.size();i++){
			if(p==blueStateStore.elementAt(i)){
				return false;
			}
		}
		return true;
	}

	
	public void removeStateRed(int p){
		redStateStore.removeElement(p);
		if(count1!=0){
			if(p==1||p==0||p==2){
				count1=0;
			}
		}
		if(count2!=0){
			if(p==2||p==3||p==4){
				count2=0;
			}
		}
		if(count3!=0){
			if(p==4||p==5||p==6){
				count3=0;
			}
		}
		if(count4!=0){
			if(p==0||p==7||p==6){
				count4=0;
			}
		}
		if(count5!=0){
			if(p==8||p==9||p==10){
				count5=0;
			}
		}
		if(count6!=0){
			if(p==1||p==9||p==7){
				count6=0;
			}
		}
		if(count7!=0){
			if(p==10||p==11||p==12){
				count7=0;
			}
		}
		if(count8!=0){
			if(p==3||p==11||p==19){
				count8=0;
			}
		}
		if(count9!=0){
			if(p==12||p==13||p==14){
				count9=0;
			}
		}
		if(count10!=0){
			if(p==5||p==13||p==21){
				count10=0;
			}
		}
		if(count11!=0){
			if(p==14||p==15||p==8){
				count11=0;
			}
		}
		if(count12!=0){
			if(p==7||p==5||p==13){
				count12=0;
			}
		}
		if(count13!=0){
			if(p==16||p==17||p==18){
				count13=0;
			}
		}
		if(count14!=0){
			if(p==18||p==19||p==20){
				count14=0;
			}
		}
		if(count15!=0){
			if(p==20||p==21||p==23){
				count15=0;
			}
		}
		if(count16!=0){
			if(p==16||p==22||p==23){
				count16=0;
			}
		}
		System.out.println(count1);
	}
	
	public void removeStateBlue(int p){
		blueStateStore.removeElement(p);
		if(count1!=0){
			if(p==1||p==0||p==2){
				count1=0;
			}
		}
		if(count2!=0){
			if(p==2||p==3||p==4){
				count2=0;
			}
		}
		if(count3!=0){
			if(p==4||p==5||p==6){
				count3=0;
			}
		}
		if(count4!=0){
			if(p==0||p==7||p==6){
				count4=0;
			}
		}
		if(count5!=0){
			if(p==8||p==9||p==10){
				count5=0;
			}
		}
		if(count6!=0){
			if(p==1||p==9||p==7){
				count6=0;
			}
		}
		if(count7!=0){
			if(p==10||p==11||p==12){
				count7=0;
			}
		}
		if(count8!=0){
			if(p==3||p==11||p==19){
				count8=0;
			}
		}
		if(count9!=0){
			if(p==12||p==13||p==14){
				count9=0;
			}
		}
		if(count10!=0){
			if(p==5||p==13||p==21){
				count10=0;
			}
		}
		if(count11!=0){
			if(p==14||p==15||p==8){
				count11=0;
			}
		}
		if(count12!=0){
			if(p==7||p==5||p==13){
				count12=0;
			}
		}
		if(count13!=0){
			if(p==16||p==17||p==18){
				count13=0;
			}
		}
		if(count14!=0){
			if(p==18||p==19||p==20){
				count14=0;
			}
		}
		if(count15!=0){
			if(p==20||p==21||p==23){
				count15=0;
			}
		}
		if(count16!=0){
			if(p==16||p==22||p==23){
				count16=0;
			}
		}
	}
	
	public boolean checkLegal(int p){
		for(int i=0; i<redStateStore.size();i++){
			if(p==redStateStore.elementAt(i)){
				return false;
			}
		}
		for(int j=0; j<blueStateStore.size(); j++){
			if(p==blueStateStore.elementAt(j)){
				return false;
			}
		}
		if(p==1000){
			return false;
		}
		return true;
	}
	
	public boolean checkMill(Vector<Integer> a){
		System.out.println("red is " +redStateStore);
		System.out.println("blue is "+blueStateStore);
		//System.out.println("a is "+ a);
		for(int i=0; i<a.size();i++){
			if(a.elementAt(i)==1){
				System.out.println("click 1");
				for(int j=0; j<a.size();j++){
					if(a.elementAt(j)==0){
						count1++;
						System.out.println("element1 "+count1);
					}
					if(a.elementAt(j)==2){
						count1++;
						System.out.println("element2 " +count1);
					}
				}
				if(count1==2){
					return true;
				}

				if(count1%2!=0){
					count1=0;

				}
			}
			if(a.elementAt(i)==3){
				for(int j=0; j<a.size();j++){
					if(a.elementAt(j)==2||a.elementAt(j)==4){
						count2++;
					}
				}
				if(count2==2){
					return true;
				}
//				if(count2!=0 && count2!=2 && count2%2==0){
//					return false;
//				}
				if(count2%2!=0){
					count2=0;
//					return false;
				}
			}
			if(a.elementAt(i)==5){
				for(int j=0; j<a.size();j++){
					if(a.elementAt(j)==4||a.elementAt(j)==6){
						count3++;
					}
				}
				if(count3==2){
					return true;
				}
//				if(count3!=0 && count3!=2 && count3%2==0){
//					return false;
//				}
				if(count3%2!=0){
					count3=0;
//					return false;
				}
			}
			if(a.elementAt(i)==7){
				for(int j=0; j<a.size();j++){
					if(a.elementAt(j)==0||a.elementAt(j)==6){
						count4++;
					}
				}
				if(count4==2){
					return true;
				}
//				if(count4!=0 && count4!=2 && count4%2==0){
//					return false;
//				}
				if(count4%2!=0){
					count4=0;
//					return false;
				}
			}
			if(a.elementAt(i)==9){
				for(int j=0; j<a.size();j++){
					if(a.elementAt(j)==8||a.elementAt(j)==10){
						count5++;
					}
					if(a.elementAt(j)==1||a.elementAt(j)==17){
						count6++;
					}
				}
				if(count5==2||count6==2){
					return true;
				}
//				if(count5!=0 && count5!=2 && count5%2==0){
//					return false;
//				}
//				if(count6!=0 && count6!=2 && count6%2==0){
//					return false;
//				}
				if(count5%2!=0){
					count5=0;
			
//					return false;
				}
				if(count6%2!=0){
					count6=0;
				}
			}
			if(a.elementAt(i)==11){
				for(int j=0; j<a.size();j++){
					if(a.elementAt(j)==10||a.elementAt(j)==12){
						count7++;
					}
					if(a.elementAt(j)==3||a.elementAt(j)==19){
						count8++;
					}
				}
				if(count7==2||count8==2){
					return true;
				}
//				if(count7!=0 && count7!=2 && count7%2==0){
//					return false;
//				}
//				if(count8!=0 && count8!=2 && count8%2==0){
//					return false;
//				}
				if(count7%2!=0){
					count7=0;
			
//					return false;
				}
				if(count8%2!=0){
					count8=0;
				}
			}
			if(a.elementAt(i)==13){
				for(int j=0; j<a.size();j++){
					if(a.elementAt(j)==14||a.elementAt(j)==12){
						count9++;
					}
					if(a.elementAt(j)==5||a.elementAt(j)==21){
						count10++;
					}
				}
				if(count9==2||count10==2){
					return true;
				}
//				if(count9!=0 && count9!=2 && count9%2==0){
//					return false;
//				}
//				if(count10!=0 && count10!=2 && count10%2==0){
//					return false;
//				}
				if(count9%2!=0){
					count9=0;
			
//					return false;
				}
				if(count10%2!=0){
					count10=0;
				}
			}
			if(a.elementAt(i)==15){
				for(int j=0; j<a.size();j++){
					if(a.elementAt(j)==14||a.elementAt(j)==8){
						count11++;
					}
					if(a.elementAt(j)==7||a.elementAt(j)==23){
						count12++;
					}
				}
				if(count11==2||count12==2){
					return true;
				}
//				if(count11!=0 && count11!=2 && count11%2==0){
//					return false;
//				}
//				if(count12!=0 && count12!=2 && count12%2==0){
//					return false;
//				}
				if(count11%2!=0){
					count11=0;
			
//					return false;
				}
				if(count12%2!=0){
					count12=0;
				}
			}
			if(a.elementAt(i)==17){
				for(int j=0; j<a.size();j++){
					if(a.elementAt(j)==16||a.elementAt(j)==18){
						count13++;
					}
				}
				if(count13==2){
					return true;
				}
//				if(count13!=0 && count13!=2 && count13%2==0){
//					return false;
//				}
				if(count13%2!=0){
					count13=0;
//					return false;
				}
			}
			if(a.elementAt(i)==19){
				for(int j=0; j<a.size();j++){
					if(a.elementAt(j)==18||a.elementAt(j)==20){
						count14++;
					}
				}
				if(count14==2){
					return true;
				}
//				if(count14!=0 && count14!=2 && count14%2==0){
//					return false;
//				}
				if(count14%2!=0){
					count14=0;
//					return false;
				}
			}
			if(a.elementAt(i)==21){
				for(int j=0; j<a.size();j++){
					if(a.elementAt(j)==20||a.elementAt(j)==22){
						count15++;
					}
				}
				if(count15==2){
					return true;
				}
//				if(count15!=0 && count15!=2 && count15%2==0){
//					return false;
//				}
				if(count15%2!=0){
					count15=0;
//					return false;
				}
			}
			if(a.elementAt(i)==23){
				for(int j=0; j<a.size();j++){
					if(a.elementAt(j)==16||a.elementAt(j)==22){
						count16++;
					}
				}
				if(count16==2){
					return true;
				}
//				if(count16!=0 && count16!=2 && count16%2==0){
//					return false;
//				}
				if(count16%2!=0){
					count16=0;
//					return false;
				}
			}
		}
		return false;
	}
	
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */




/**
 *
 * @author Administrator
 */
class ConnectDialog extends javax.swing.JDialog {
    private ConnectInfo cif;
    /**
     * Creates new form ConnectDialog
     */
    public ConnectDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LHost = new javax.swing.JLabel();
        LUsername = new javax.swing.JLabel();
        LRoom = new javax.swing.JLabel();
        vHost = new javax.swing.JTextField();
        LDot = new javax.swing.JLabel();
        vPort = new javax.swing.JTextField();
        vRooms = new javax.swing.JComboBox();
        vUsername = new javax.swing.JTextField();
        bCancel = new javax.swing.JButton();
        bConnect = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Connect to Server");

        LHost.setText("Host:");

        LUsername.setText("Username:");

        LRoom.setText("Room:");

        vHost.setText("localhost");

        LDot.setText(":");

        vPort.setText("8888");
        vPort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vPortActionPerformed(evt);
            }
        });

        vRooms.setModel(new javax.swing.DefaultComboBoxModel(new String[] { 
    			"room1", "room2", "room3", "room4", "room5",
    			"room6", "room7", "room8", "room9", "room10" }));

        bCancel.setText("Cancel");
        bCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCancelActionPerformed(evt);
            }
        });

        bConnect.setText("Connect");
        bConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bConnectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bConnect)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bCancel))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(LHost)
                            .addComponent(LRoom)
                            .addComponent(LUsername))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(vHost, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                                    .addComponent(vRooms, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(LDot)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(vPort, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(vUsername))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LHost)
                    .addComponent(vHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LDot)
                    .addComponent(vPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LRoom)
                    .addComponent(vRooms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LUsername)
                    .addComponent(vUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bCancel)
                    .addComponent(bConnect))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void vPortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vPortActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_vPortActionPerformed

    private void bConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bConnectActionPerformed
        if(vUsername.getText().trim().length()==0) {
            vUsername.setBackground(Color.red);
            return;
        }else{
            vUsername.setBackground(Color.white);
        }
        String host = vHost.getText().trim();
        int port = Integer.parseInt(vPort.getText().trim());
        String room = (String) vRooms.getSelectedItem();
        String username = vUsername.getText().trim();
        cif = new ConnectInfo(host, port, room, username);
        this.dispose();
    }//GEN-LAST:event_bConnectActionPerformed

    private void bCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_bCancelActionPerformed

    public ConnectInfo getConnectInfo() {
        return this.cif;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(ConnectDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(ConnectDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(ConnectDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(ConnectDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
        //</editor-fold>

        /*
         * Create and display the dialog
         */
        
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LDot;
    private javax.swing.JLabel LHost;
    private javax.swing.JLabel LRoom;
    private javax.swing.JLabel LUsername;
    private javax.swing.JButton bCancel;
    private javax.swing.JButton bConnect;
    private javax.swing.JTextField vHost;
    private javax.swing.JTextField vPort;
    private javax.swing.JComboBox vRooms;
    private javax.swing.JTextField vUsername;
    // End of variables declaration//GEN-END:variables
}




class Gui extends JFrame implements ActionListener, MouseListener, CanPlay {
	
	Container cp = getContentPane();
	JMenuBar bar = new JMenuBar();
	JMenu fileMenu = new JMenu("File"); 
	JMenuItem playLocal = new JMenuItem("Local Game");
	JMenuItem playComputer = new JMenuItem("With Computer");
	JMenuItem playOnline = new JMenuItem("Online Game");
	JMenuItem stopGame = new JMenuItem("Stop Game");
	JTextArea ta=new JTextArea();
	JTextField red = new JTextField("red",15);
	JTextField blue = new JTextField("blue",15);
	Board board = new Board();
	JPanel jp1=new JPanel();
	JPanel jp2=new JPanel();
	JPanel jp3=new JPanel();
	JLabel jlb2=new JLabel("name: ");
	JButton jb3=new JButton("clear");
	JButton jb4=new JButton("send");
	JLabel text = new JLabel("Welcome to Nine Men's Morris");
	final JTextArea jta1=new JTextArea();//œ‘ æ
	final JTextArea jta2=new JTextArea(3,20);// ‰»Î
	final JTextField jtf1=new JTextField(10);
	
	CanPlay otherPlayer;
	CanConnect conn;
	final JPanel statusbar = new JPanel();
	
	ConnectInfo cif;
	
	JLabel msg;
	
	BoardOperation bo = new BoardOperation();
	int step=1;
	int step2=1;
	int player=1;
	int redCounter=9;
	int blueCounter=9;
	int countRed=1;
	int countBlue=1;
	int sign=0;
	int leftRed=9;
	int leftBlue=9;
	int count=0;
	int mode=0;
	int count100=0;
	public Gui(String name,CanPlay otherPlayer, CanConnect conn){
		super(name);
		this.otherPlayer = otherPlayer;
		this.conn = conn;
	}
	
	public void CreateWindow(){
		
		this.setSize(680, 600);
		this.setResizable(false);
		
		cp.setLayout(null);
		cp.setBackground(Color.cyan);
		fileMenu.add(playLocal);
		fileMenu.add(playComputer);
		fileMenu.add(playOnline);
		fileMenu.add(stopGame);
		bar.add(fileMenu);
		setJMenuBar(bar);

		this.stopGame.setEnabled(false);
		
		playLocal.addActionListener(this);
		playComputer.addActionListener(this);
		playOnline.addActionListener(this);
		stopGame.addActionListener(this);
		board.addMouseListener(this);
		
		
	}
	// this is only response to the menu item "New Game"
	public void actionPerformed(ActionEvent e) {
		
		// TODO Auto-generated method stub
		if(e.getActionCommand().equals("Local Game")){
			//System.out.print("biblibibibibi");
			mode=1;
			showBorad();
			addGameBoard();
			this.validate();
			this.repaint();
			this.playLocal.setEnabled(false);
			this.playOnline.setEnabled(false);
			this.playComputer.setEnabled(false);
			this.stopGame.setEnabled(true);
			this.jtf1.setText("LocalPlayer");
			msg.setText("<HTML><FONT COLOR='RED' SIZE='5'>Hello, LocalPlayer. You are ready to play!</FONT><HTML>");
		}
		
		else if(e.getActionCommand().equals("With Computer")){
			System.out.print("lalalalalalal");
			mode=2;
			showBorad();
			addGameBoard();
			this.validate();
			this.repaint();
			this.playLocal.setEnabled(false);
			this.playOnline.setEnabled(false);
			this.playComputer.setEnabled(false);
			this.stopGame.setEnabled(true);
			this.jtf1.setText("LocalPlayer");
			msg.setText("<HTML><FONT COLOR='RED' SIZE='5'>Hello, LocalPlayer. You are ready to play!</FONT><HTML>");
		}
		
		else if(e.getActionCommand().equals("Online Game")){
			mode=1;
			final ConnectDialog dialog = new ConnectDialog(new javax.swing.JFrame(), true);
			
			dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    dialog.dispose();
                }
            });
            dialog.setVisible(true);
            
            this.cif = dialog.getConnectInfo();
            try {
				this.conn.connect(cif);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
            
            showBorad();
			addGameBoard();
			this.validate();
			this.repaint();
			this.jtf1.setText(cif.getUsername());
			
			this.playLocal.setEnabled(false);
			this.playOnline.setEnabled(false);
			this.playComputer.setEnabled(false);
			this.stopGame.setEnabled(true);
			
				msg.setText("<HTML><FONT COLOR='RED' SIZE='5'>Hello, " + cif.getUsername() + ". Welcome!</FONT><HTML>");
			
			//msg.setText("<HTML><FONT COLOR='RED' SIZE='5'>Hi, " + cif.getUsername() + ". Both ready!</FONT><HTML>");
		}else if(e.getActionCommand().equals("Stop Game")) {
			System.exit(0);
		}
	}
	
	
	public void addGameBoard(){

		jp3.setLocation(130, 470);
		jp3.setBackground(Color.cyan);
		jp3.setSize(200,50);
		jp3.add(text);
		cp.add(jp3);
		
		board.setBackground(Color.cyan);
		board.setSize(290, 381);
		board.setLocation(110, 70);
		
		cp.add(board);
		
		
//		System.out.println("111111111");
		board.passBoard();
		//System.out.println("test successful4");
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		this.doEvent(x, y);
		//send the event to the other side
		if(otherPlayer!=null){
			msg.setText("<HTML><FONT COLOR='RED' SIZE='5'>Hi, " + cif.getUsername() + ". Enjoy it~</FONT><HTML>");
			otherPlayer.doEvent(PlayInfo.buildPlayEvent(cif.getRoom(), cif.getUsername(), x, y));
		}
			
	}
	
	//receive the play event from the website
	@Override
	public void doEvent(PlayInfo pif) {
		switch(pif.type) {
		case Command.TYPE_PLAY_EVENT :
			this.doEvent(pif.x, pif.y);
			break;
		case Command.TYPE_MESSAGE_ALL :
		case Command.TYPE_MESSAGE_GROUP:
			jta1.insert(pif.userName+">>>>"+pif.message+"\n",jta1.getDocument().getLength());
			break;
		//ignore others!
		}
		
	}
	
	
	//method to detect a mouse has entered a defined area
	public void mouseEntered(MouseEvent e){}
	//method to detect a mouse has exited a defined area
	public void mouseExited(MouseEvent e){}
	//method to detect that a mouse button has been released over a defined area
	public void mouseReleased(MouseEvent e){
		//System.out.println("released");
	}
	
	public void mousePressed(MouseEvent e){}
	


	
	private void doEvent(int x, int y) {

		// TODO Auto-generated method stub
		int position=1000;
		// check if mouse button has been clicked over any of the position on the outer square
		if((x>=0 && x<=20)&&(y>=0 && y<=20)){

			position=16;
		}
		if((x>=130 && x<=150)&&(y>=0 && y<=20)){

			position=17;
		}
		if((x>=260 && x<=280)&&(y>=0 && y<=20)){

			position=18;
		}
		if((x>=260 && x<=280)&&(y>=130 && y<=150)){

			position=19;
		}
		if((x>=260 && x<=280)&&(y>=260 && y<=280)){

			position=20;
		}
		if((x>=130 && x<=150)&&(y>=260 && y<=280)){

			position=21;
		}
		if((x>=0 && x<=20)&&(y>=260 && y<=280)){

			position=22;
		}
		if((x>=0 && x<=20)&&(y>=130 && y<=150)){

			position=23;
		}
		// checks if the mouse button has been clicked over any of the position on the middle square
		if((x>=50 && x<=70)&&(y>=50 && y<=70)){

			position=8;
		}
		if((x>=130 && x<=150)&&(y>=50 && y<=70)){

			position=9;
		}
		if((x>=210 && x<=230)&&(y>=50 && y<=70)){

			position=10;
		}
		if((x>=210 && x<=230)&&(y>=130 && y<=150)){

			position=11;
		}
		if((x>=210 && x<=230)&&(y>=210 && y<=230)){

			position=12;
		}
		if((x>=130 && x<=150)&&(y>=210 && y<=230)){

			position=13;
		}
		if((x>=50 && x<=70)&&(y>=210 && y<=230)){

			position=14;
		}
		if((x>=50 && x<=70)&&(y>=130 && y<=150)){

			position=15;
		}
		//checks if the mouse button has been clicked over any of the position on the inner square
		if((x>=100 && x<=120)&&(y>=100 && y<=120)){

			position = 0;
		}
		if((x>=130 && x<=150)&&(y>=100 && y<=120)){

			position =1;
		}
		if((x>=160 && x<=180)&&(y>=100 && y<=120)){

			position=2;
		}
		if((x>=160 && x<=180)&&(y>=130 && y<=150)){

			position=3;
		}
		if((x>=160 && x<=180)&&(y>=160 && y<=180)){
			
			position=4;
		}
		if((x>=130 && x<=150)&&(y>=160 && y<=180)){

			position=5;
		}
		if((x>=100 && x<=120)&&(y>=160 && y<=180)){
			position=6;
		}
		if((x>=100 && x<=120)&&(y>=130 && y<=150)){

			position=7;
		}

		if(mode==2){
			if(step2==1){
				
				if(bo.checkLegal(position)==true){
					bo.addStateRed(position);
					leftRed=leftRed-1;
					
					
					if(bo.checkMill(bo.getRedVector())==false){
//						text.setText("Blue need to place a new counter");
						//player++;
						
						while(bo.randomSelect()==false){
							//System.out.println("waeaeafawefawfew");
						}
						leftBlue=leftBlue-1;
						board.set(leftRed,leftBlue);
						if(bo.checkMill(bo.getBlueVector())==true){
							while(bo.randomRemove()==false){
								
							}	
						}
						if(leftBlue==0){
							MessageWindow placeEnd = new MessageWindow(null);
							placeEnd.finishPlace();
							placeEnd.setLocation(500, 250);
							placeEnd.show();
							text.setText("Red need to move one of his counters");
							step2=3;
						}
						
					}
					else{
						//System.out.print("fasdfsdafasdfasd0");
						board.set(bo.getRedVector(), bo.getBlueVector());
						addGameBoard();
						text.setText("There is a mill, remove one of your opponent's counters");
						MessageWindow mill = new MessageWindow(null);
						mill.millInfo();
						mill.setLocation(500, 250);
						mill.show();
						step2=2;
					}
				}
				else{
					MessageWindow illegalPlace = new MessageWindow(null);
					illegalPlace.illegalPlace();
					illegalPlace.setLocation(500,250);
					illegalPlace.show();
				}
				
			}
			if(step2==2){
				if(bo.checkBlueRemove(position)==false){
					bo.removeStateBlue(position);
					blueCounter--;
					
					
					if(bo.getBlueVector().size()==2){
						board.set(bo.getRedVector(), bo.getBlueVector());
						addGameBoard();
						MessageWindow redWin= new MessageWindow(null);
						redWin.redWin();
						redWin.setLocation(500, 250);
						redWin.show();
						step2=10;
					}
					
					player=2;
					if(sign==3){
						if(step2!=10){
							while(bo.randomMove()==false){
								
							}
							step2=3;
						}
						
					}
					else{
						while(bo.randomSelect()==false){
							//System.out.println("waeaeafawefawfew");
						}
						leftBlue=leftBlue-1;
						board.set(leftRed,leftBlue);
						if(bo.checkMill(bo.getBlueVector())==true){
							while(bo.randomRemove()==false){
								
							}	
						}
						step2=1;
					}
					
				}
				else{
					MessageWindow illegalRemove= new MessageWindow(null);
					illegalRemove.illegalRemove();
					illegalRemove.setLocation(500, 250);
					illegalRemove.show();
				}
			}
			
			if(step2==3){
				if(bo.getRedVector().size()==3){
					if(countRed==1){
						if(bo.detectSelectedRedCounter(position)==true){
							bo.chooseCounter(position);
							countRed++;
						}
						else{
							MessageWindow illegalSelect= new MessageWindow(null);
							illegalSelect.illegalSelected();
							illegalSelect.setLocation(500, 250);
							illegalSelect.show();
						}
						
					}
					else{
						bo.removeStateRed(bo.getChooseCounter());
						bo.addStateRed(position);
						countRed=1;
						//player++;
					}
				}
					
				
				
				else if(countRed==1){
					if(bo.detectSelectedRedCounter(position)==true){
						bo.chooseCounter(position);
						countRed++;
					}
//					else{
//						MessageWindow illegalSelect= new MessageWindow(null);
//						illegalSelect.illegalSelected();
//						illegalSelect.setLocation(500, 250);
//						illegalSelect.show();
//					}
				}
				else{
					if(bo.detectIllegalMove(bo.getChooseCounter(), position)==true){
						bo.removeStateRed(bo.getChooseCounter());
						bo.addStateRed(position);
						if(bo.checkMill(bo.getRedVector())==false){
							text.setText("Blue need to move one of his counters");
							while(bo.randomMove()==false){
								
							}
							//player++;
							countRed=1;
						}
						else{
							board.set(bo.getRedVector(), bo.getBlueVector());
							addGameBoard();
							text.setText("There is a mill, remove one of your opponent's counters");
							MessageWindow mill = new MessageWindow(null);
							mill.millInfo();
							mill.setLocation(500, 250);
							mill.show();
							sign=3;
							step2=2;
							countRed=1;
						}
					}
					else{
						MessageWindow illegalMove= new MessageWindow(null);
						illegalMove.illegalMove();
						illegalMove.setLocation(500, 250);
						illegalMove.show();
						countRed=1;
					}
					countRed=1;
				}
			}
			
			board.set(bo.getRedVector(), bo.getBlueVector());
			addGameBoard();
			
			
		
		
		
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		if(mode==1){
			if(step==1){
				if(bo.checkLegal(position)==true){
					if(player==1){
						bo.addStateRed(position);
						leftRed=leftRed-1;
						board.set(leftRed,leftBlue);
						
						if(bo.checkMill(bo.getRedVector())==false){
							text.setText("Blue need to place a new counter");
							player++;
						}
						else{
							board.set(bo.getRedVector(), bo.getBlueVector());
							addGameBoard();
							text.setText("There is a mill, remove one of your opponent's counters");
							MessageWindow mill = new MessageWindow(null);
							mill.millInfo();
							mill.setLocation(500, 250);
							mill.show();
							step=2;
						}
						
					}
					else{
						bo.addStateBlue(position);
					
						leftBlue=leftBlue-1;
						board.set(leftRed,leftBlue);
						
						board.set(bo.getRedVector(), bo.getBlueVector());
						addGameBoard();
						
						if(bo.getBlueVector().size()==blueCounter){
							
							MessageWindow placeEnd = new MessageWindow(null);
							placeEnd.finishPlace();
							placeEnd.setLocation(500, 250);
							placeEnd.show();
							text.setText("Red need to move one of his counters");
							step=3;
						}
						if(bo.checkMill(bo.getBlueVector())==false){
							text.setText("Red need to place a new counter");
							player--;
						}
						else{
							board.set(bo.getRedVector(), bo.getBlueVector());
							addGameBoard();
							text.setText("There is a mill, remove one of your opponent's counters");
							MessageWindow mill = new MessageWindow(null);
							mill.millInfo();
							mill.setLocation(500, 250);
							mill.show();
							step=2;
						}
						
					}

				}
				else{
					MessageWindow illegalPlace = new MessageWindow(null);
					illegalPlace.illegalPlace();
					illegalPlace.setLocation(500,250);
					illegalPlace.show();
				}

			}
			
			else if(step==2){
				if(player==1){
					if(bo.checkBlueRemove(position)==false){
						bo.removeStateBlue(position);
						blueCounter--;
						
						if(bo.getBlueVector().size()==2 && sign==3){
							board.set(bo.getRedVector(), bo.getBlueVector());
							addGameBoard();
							MessageWindow redWin= new MessageWindow(null);
							redWin.redWin();
							redWin.setLocation(500, 250);
							redWin.show();
							step=10;
						}
						
						player=2;
						if(sign==3){
							step=3;
						}
						else{
							step=1;
						}
						
					}
					else{
						MessageWindow illegalRemove= new MessageWindow(null);
						illegalRemove.illegalRemove();
						illegalRemove.setLocation(500, 250);
						illegalRemove.show();
					}
					
				}
				else if(player==2){
					if(bo.checkRedRemove(position)==false){
						bo.removeStateRed(position);
						redCounter--;
						if(bo.getRedVector().size()==2 && sign==3){
							board.set(bo.getRedVector(), bo.getBlueVector());
							addGameBoard();
							MessageWindow blueWin= new MessageWindow(null);
							blueWin.blueWin();
							blueWin.setLocation(500, 250);
							blueWin.show();
							step=10;
						}
						player=1;
						if(sign==3){
							step=3;
						}
						else{
							step=1;
						}
					}
					else{
						MessageWindow illegalRemove= new MessageWindow(null);
						illegalRemove.illegalRemove();
						illegalRemove.setLocation(500, 250);
						illegalRemove.show();
					}
				}
				
			}
			
			else if(step==3){
				if(player==1){
					
					if(bo.getRedVector().size()==3){
						if(countRed==1){
							if(bo.detectSelectedRedCounter(position)==true){
								bo.chooseCounter(position);
								countRed++;
							}
							else{
								MessageWindow illegalSelect= new MessageWindow(null);
								illegalSelect.illegalSelected();
								illegalSelect.setLocation(500, 250);
								illegalSelect.show();
							}
							
						}
						else{
							bo.removeStateRed(bo.getChooseCounter());
							bo.addStateRed(position);
							countRed=1;
							player++;
						}
					}
						
			
					
					else if(countRed==1){
						
						
						
						if(bo.detectSelectedRedCounter(position)==true){
							bo.chooseCounter(position);
							countRed++;
						}
						else{
							MessageWindow illegalSelect= new MessageWindow(null);
							illegalSelect.illegalSelected();
							illegalSelect.setLocation(500, 250);
							illegalSelect.show();
						}
					}
					else{
						if(bo.detectIllegalMove(bo.getChooseCounter(), position)==true){
							bo.removeStateRed(bo.getChooseCounter());
							bo.addStateRed(position);
							if(bo.checkMill(bo.getRedVector())==false){
								text.setText("Blue need to move one of his counters");
								player++;
								countRed=1;
							}
							else{
								board.set(bo.getRedVector(), bo.getBlueVector());
								addGameBoard();
								text.setText("There is a mill, remove one of your opponent's counters");
								MessageWindow mill = new MessageWindow(null);
								mill.millInfo();
								mill.setLocation(500, 250);
								mill.show();
								sign=3;
								step=2;
								countRed=1;
							}
						}
						else{
							MessageWindow illegalMove= new MessageWindow(null);
							illegalMove.illegalMove();
							illegalMove.setLocation(500, 250);
							illegalMove.show();
							countRed=1;
						}
						countRed=1;
					}
				}
				else{
					
					if(bo.getBlueVector().size()==3){
						if(countBlue==1){
							if(bo.detectSelectedBlueCounter(position)==true){
								bo.chooseCounter(position);
								countBlue++;
							}
							else{
								MessageWindow illegalSelect= new MessageWindow(null);
								illegalSelect.illegalSelected();
								illegalSelect.setLocation(500, 250);
								illegalSelect.show();
							}
							
						}
						else{
							bo.removeStateBlue(bo.getChooseCounter());
							bo.addStateBlue(position);
							countBlue=1;
							player++;
						}
					}
					
					else if(countBlue==1){
						if(bo.detectSelectedBlueCounter(position)==true){
							bo.chooseCounter(position);
							countBlue++;
						}
						else{
							MessageWindow illegalSelect2= new MessageWindow(null);
							illegalSelect2.illegalSelected();
							illegalSelect2.setLocation(500, 250);
							illegalSelect2.show();
						}
						
					}
					else{
						if(bo.detectIllegalMove(bo.getChooseCounter(), position)==true){
							bo.removeStateBlue(bo.getChooseCounter());
							bo.addStateBlue(position);
							if(bo.checkMill(bo.getBlueVector())==false){
								text.setText("Red need to move one of his counters");
								player--;
								countBlue=1;
							}
							else{
								board.set(bo.getRedVector(), bo.getBlueVector());
								addGameBoard();
								text.setText("There is a mill, remove one of your opponent's counters");
								MessageWindow mill = new MessageWindow(null);
								mill.millInfo();
								mill.setLocation(500, 250);
								mill.show();
								sign=3;
								step=2;
								countBlue=1;
							}
							
						}
						else{
							
							MessageWindow illegalMove2= new MessageWindow(null);
							illegalMove2.illegalMove();
							illegalMove2.setLocation(500, 250);
							illegalMove2.show();
							countBlue=1;
						}
						countBlue=1;
					}
				}
			}
		}
		

		
	
		board.set(bo.getRedVector(), bo.getBlueVector());
		addGameBoard();
		
	}



	private void showBorad() {
		jp3.setLocation(420, 50);
		jp3.setBackground(Color.cyan);
		jp3.setSize(200,50);
		jp3.add(text);
		this.cp.add(jp3);
		
		msg = new JLabel("<HTML><FONT COLOR='RED' SIZE='5'>hello</FONT><HTML>");
		
		statusbar.setLocation(0, cp.getHeight()-30);
		statusbar.setSize(cp.getWidth(),30);
//		statusbar.setBounds(0, this.getHeight()+60, this.getWidth(), 30);
		statusbar.add(msg);
		statusbar.setBackground(Color.red);
		cp.add(statusbar);
		
		jp1.setSize(500, 50);
		jp1.setLocation(400,150);
		jp1.setBackground(Color.yellow);
		jp1.add(jlb2);
		jp1.add(jtf1);
		jtf1.setEditable(false);
		
		jp2.setSize(200, 100);
		jp2.setBackground(Color.cyan);
		jp2.setLocation(420,290);
		this.cp.add(jp2);
		this.cp.add(jp1);
		
//		jp3.setSize(140, 50);
//		jp3.setLocation(500,100);
//		cp.add(jp3);
//		jp1.setBackground(Color.PINK);
//		  
//		  jp1.add(jlb2);
//		  jp1.add(jtf1);
//		  

		  jta1.setEditable(false);
		  JScrollPane scroll = new JScrollPane(jta1);

		  scroll.setHorizontalScrollBarPolicy(
	                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		  scroll.setVerticalScrollBarPolicy(
	                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		  final JTextArea jta2=new JTextArea(3,20);// ‰»Î
		  
		  jta1.setSize(200, 100);
		  jta1.setLocation(420, 180);
		  this.cp.add(scroll);
		  this.cp.add(jta1);
//		  cp.add(jp1,"North");
//	
//		  cp.add(jta1,"Center");
		
		  
		  jb3.addActionListener(new ActionListener()
		  {
		   public void actionPerformed(ActionEvent e)
		   {
		    jta2.setText("");
		   }
		  });

		  String s1=jta2.getText();
		  jb4.addActionListener(new ActionListener()
		  {
		   public void actionPerformed(ActionEvent e)
		   {
		    String s1=jtf1.getText();
		    String s2=jta2.getText();
		    
		    jta1.insert(s1+">>>>"+s2+"\n",jta1.getDocument().getLength());
		    if(otherPlayer!=null)
				otherPlayer.doEvent(PlayInfo.buildMsgEvent(cif, s2));
		    jta2.setText("");
		   }
		  });
		  jp2.add(jta2);
		  jp2.add(jb3);
		  jp2.add(jb4);
		  this.cp.add(jp2);
		  this.cp.add(jp1);
//		  jp2.setBackground(Color.PINK);
//		  
//		  
//		  cp.add(jp2,"South");
		  
//		  jf.setSize(400,200);
//		  jf.setVisible(true);
		  
	}
	
}



class MessageWindow extends JDialog implements ActionListener {

	Container cp = getContentPane();
	JButton ok = new JButton("OK");
	
	public MessageWindow(JFrame parent){
		super(parent,"Message Window",true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getActionCommand().equals("OK")){
			dispose();
		}			
	}
	
	public void illegalPlace(){
		cp.setLayout(new FlowLayout(FlowLayout.CENTER));
		cp.add(new JLabel("Illegal Placement! Place again! "));
		cp.add(ok);
		ok.addActionListener(this);
		setSize(250,100);
	}
	
	public void millInfo(){
		cp.setLayout(new FlowLayout(FlowLayout.CENTER));
		cp.add(new JLabel("You have a mill, please remove one of your opponent's counters!"));
		cp.add(ok);
		ok.addActionListener(this);
		setSize(420,100);
	}
	
	public void finishPlace(){
		cp.setLayout(new FlowLayout(FlowLayout.CENTER));
		cp.add(new JLabel("All counters has been placed, please start to move them!"));
		cp.add(ok);
		ok.addActionListener(this);
		setSize(370,100);
	}

	public void illegalRemove(){
		cp.setLayout(new FlowLayout(FlowLayout.CENTER));
		cp.add(new JLabel("Illegal remove, please remove a counter of your opponent!"));
		cp.add(ok);
		ok.addActionListener(this);
		setSize(380,100);
	}
	
	public void illegalSelected(){
		cp.setLayout(new FlowLayout(FlowLayout.CENTER));
		cp.add(new JLabel("You need to reselect, please select a counter belongs to you!"));
		cp.add(ok);
		ok.addActionListener(this);
		setSize(400,100);
	}
	
	public void illegalMove(){
		cp.setLayout(new FlowLayout(FlowLayout.CENTER));
		cp.add(new JLabel("You can not move to there, please select an empty place next to your selected counter!"));
		cp.add(ok);
		ok.addActionListener(this);
		setSize(550,100);
	}
	public void redWin(){
		cp.setLayout(new FlowLayout(FlowLayout.CENTER));
		cp.add(new JLabel("Red win!"));
		cp.add(ok);
		ok.addActionListener(this);
		setSize(300,100);
	}
	public void blueWin(){
		cp.setLayout(new FlowLayout(FlowLayout.CENTER));
		cp.add(new JLabel("Blue win!"));
		cp.add(ok);
		ok.addActionListener(this);
		setSize(300,100);
	}
}
