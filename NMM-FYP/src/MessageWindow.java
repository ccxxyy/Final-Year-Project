import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class MessageWindow extends JDialog implements ActionListener {

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
