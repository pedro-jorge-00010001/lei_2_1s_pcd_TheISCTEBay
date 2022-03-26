package mainPack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
public class Directory {
	private List<UserData> userList= new ArrayList<UserData>();
	public static final Integer DIRECTORY_PORT = 8080;
	public void runServer() {
		try {			
			ServerSocket server = new ServerSocket(DIRECTORY_PORT);
			while(true) {
				Socket socket=server.accept();
				System.out.println("Conex�o aceite");
				ConnectionThread m=new ConnectionThread(socket);//trata da conex�o com os utilizadores
				m.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		Directory m=new Directory();
		m.runServer();
	}
	class ConnectionThread extends Thread {//clase interna
		Socket socket;
		BufferedReader in;
		PrintWriter out;
		UserData thisUser;
		public ConnectionThread(Socket socket) throws IOException {
			this.socket=socket;
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			in= new BufferedReader(new InputStreamReader(socket.getInputStream()));	
		}
		@Override
		public void run() {
			try {
				while(true){
					String msg=in.readLine();
					System.out.println(msg);
					String vetor[]=msg.split(" ");
					if(vetor[0].equals("INSC")){
						userList.add(thisUser=new UserData(cutString(vetor[1]),cutString(vetor[2]),Integer.parseInt(cutString(vetor[3]))));
					}
					if(vetor[0].equals("CLT")){
						for(UserData a: userList){
							out.println(a.toString());
						}
						out.println("END");
					}
				}
				
				//				System.out.println(socket.isClosed());
			} catch (IOException e) {//catch para se o utlizador se desligar o remover da lista
				socketClose();
				userList.remove(thisUser);
//				System.out.println(socket.isClosed());				
			}
		}
		private void socketClose(){
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static String cutString(String string){// clase estatica para tirar <> das mensagens
		String result=string.substring(1, string.length()-1);
		return result;
	}
}
