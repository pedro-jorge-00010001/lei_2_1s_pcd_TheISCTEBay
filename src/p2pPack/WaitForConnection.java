package p2pPack;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import coordinationPack.ThreadPool;

public class WaitForConnection extends Thread{
	private int port;
	private String path;
	private final static int TAMANHOTHREADPOOL=5;
	private ThreadPool threadPool;
	public WaitForConnection(int port,String path) {
		this.path=path;
		this.port=port;
		
	}
	public void run() {
		threadPool=new ThreadPool(TAMANHOTHREADPOOL);
		try {
			System.out.println("meu while ta a correr com o path" + path);
			ServerSocket server=new ServerSocket(port);
			while(true) {
				Socket socket=server.accept();
				System.out.println("Conexão -user_user- aceite");
				UserBeingServer m=new UserBeingServer(socket,path,threadPool);
				m.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}		