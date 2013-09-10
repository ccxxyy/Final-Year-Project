package board.server.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyUtil {
	
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
