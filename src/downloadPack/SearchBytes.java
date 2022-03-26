package downloadPack;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;

import mainPack.FileBlockRequestMessage;
import mainPack.FileDetails;

public class SearchBytes extends Thread {
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private String path;
	private Object object;
	public SearchBytes(Socket socket, String path, Object object, ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
		this.socket=socket;
		this.path=path;
		this.object=object;
		this.oos = oos;
		this.ois =ois;
	}

	public void run() {
		int pos=0;
		int count=0;
		File[] files = new File(path).listFiles();
		String filename;
		FileDetails filedetails=((FileBlockRequestMessage) object).getFiledetails();
		for(File f :files) {
			filename = (String)f.getName();
			if(filename.equals((filedetails.getNome())) && filedetails.getTamanho()==f.length()) {
				pos=count;
			}
			count++;
		}
		byte[] fileContentsAll;
		try {
			fileContentsAll = Files.readAllBytes(files[pos].toPath());

			while(object instanceof FileBlockRequestMessage) {
				FileBlockRequestMessage bloco= (FileBlockRequestMessage) object;
				byte[] toSend= new byte[(int) bloco.getLength()];
				for(int i =0;i<(int) bloco.getLength();i++) {							
					toSend[i]=fileContentsAll[(int) (bloco.getOffset())+i];
				}
				oos.writeObject(toSend);
				object=ois.readObject();
			}
		} catch (IOException e) {
			System.out.println("Esta tarefa não executou");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("sai");
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
