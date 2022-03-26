package p2pPack;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.DefaultListModel;

import mainPack.FileDetails;
import mainPack.UserData;
import mainPack.WordSearchMessage;

public class UserBeingUser extends Thread{//faz como user na conexao user_user
	private ArrayList<FileDetails> files;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private WordSearchMessage wordkey;
	private Socket socket;
	private int porto;
	public UserBeingUser(Socket socket,WordSearchMessage wordkey,int porto) throws IOException {
		//enviar o objeto para o ServerUser ThreadCorrespondente
		this.socket= socket;
		this.porto=porto;
		this.wordkey=wordkey;
		ois = new ObjectInputStream(socket.getInputStream());
		oos = new ObjectOutputStream(socket.getOutputStream());		
	}
	public void run() {
		try {
			oos.writeObject(wordkey);
			files = (ArrayList<FileDetails>) ois.readObject();
//			for(FileDetails b: files) {	
//				b.getOwners().add(new UserData("User",socket.getInetAddress().getHostAddress(),porto));
//			}
			socket.close();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public ArrayList<FileDetails> getFilesDetails() {
		return files;
	}
}