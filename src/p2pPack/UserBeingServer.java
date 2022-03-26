package p2pPack;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;

import coordinationPack.ThreadPool;
import downloadPack.SearchBytes;
import mainPack.FileBlockRequestMessage;
import mainPack.FileDetails;
import mainPack.UserData;
import mainPack.WordSearchMessage;

public class UserBeingServer extends Thread{//faz como server na conexao user_user
	private ArrayList<FileDetails> filesList=new ArrayList<FileDetails>();
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private String path;
	private ThreadPool threadPool;
	public UserBeingServer(Socket socket,String path, ThreadPool threadPool) throws IOException {
		this.socket=socket;
		this.path=path;
		this.threadPool=threadPool;
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
	}
	public void run() {
		try {
			System.out.println(socket.getLocalPort());		
			Object object=ois.readObject();		
			if(object instanceof WordSearchMessage) {
				File[] files = new File(path).listFiles();
				String filename;
				WordSearchMessage wordkey= (WordSearchMessage) object;
				System.out.println(path);
				for(File f :files) {
					filename = (String)f.getName();
					if(filename.contains(wordkey.getWord())) {
						filesList.add(new FileDetails(f.getName(),f.length(),new UserData("User",InetAddress.getLocalHost().getHostAddress(),socket.getLocalPort())));
					}
				}
				oos.writeObject(filesList);
				System.out.println("sai");
				socket.close();
			}
			if(object instanceof FileBlockRequestMessage) {
				SearchBytes search=new SearchBytes(socket,path,object,ois,oos);
				threadPool.submit(search);
			}
			
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}