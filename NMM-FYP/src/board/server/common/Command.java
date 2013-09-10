package board.server.common;

public class Command {
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
