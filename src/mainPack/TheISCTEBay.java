package mainPack;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TheISCTEBay {
	public static void main(String[] args) throws UnknownHostException, IOException {
		InetAddress	address = InetAddress.getLocalHost();

		//See the current Ip of the Directory (Sometime is "0.0.0.0")
		User m=new User("User",address.getHostAddress(),"0.0.0.0", Directory.DIRECTORY_PORT, Integer.parseInt(args[0]),args[1]);
		m.runUtilizador();
	}
	

}
