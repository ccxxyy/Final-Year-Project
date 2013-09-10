package board.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

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