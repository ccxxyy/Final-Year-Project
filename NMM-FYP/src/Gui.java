import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import board.server.common.CanConnect;
import board.server.common.CanPlay;
import board.server.common.Command;
import board.server.common.ConnectInfo;
import board.server.common.PlayInfo;


public class Gui extends JFrame implements ActionListener, MouseListener, CanPlay {
	
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
