package downloadPack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class WriterFile extends Thread {
	private ArrayList<Downloader> threadList=new ArrayList<Downloader>();
	private byte[] fileContents;
	private String path;
	private String nome;
	public WriterFile(ArrayList<Downloader> threadList, byte[] fileContents, String path, String nome) {
		this.nome=nome;
		this.path=path;
		this.threadList=threadList;
		this.fileContents=fileContents;
	}
	public void run() {
		String toShow="Descarga completa\n";
		long startTime = System.currentTimeMillis();
		int usersincomplete=0;
		for(Downloader b: threadList) {
			try {
				b.join();
				if(!b.isAtivo()) {
					usersincomplete++;
				}
				toShow=toShow+b.toString()+"\n";
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(usersincomplete!=threadList.size()) {
			try {
				Files.write(Paths.get(path+"/"+nome), fileContents);
				long endTime = System.currentTimeMillis();
				toShow=toShow+"Tempo transcurrido: "+ (int)((endTime-startTime)/1000)+"s";
				JOptionPane.showMessageDialog(null, toShow);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			JOptionPane.showMessageDialog(null, "O ficheiro não foi descarregado\n Todos os usuarios encarregados da descarga desligaram");
		}
	}


}
