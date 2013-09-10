import java.util.Vector;


public class BoardOperation {

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
