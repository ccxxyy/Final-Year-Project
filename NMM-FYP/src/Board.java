import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class Board extends JPanel {
	
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
