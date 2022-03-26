package downloadPack;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import coordinationPack.BloquingQueue;
import mainPack.FileBlockRequestMessage;

public class Downloader extends Thread{
	private BloquingQueue <FileBlockRequestMessage> queue;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Socket socket;
	private byte[] fileContents;
	private Queue<FileBlockRequestMessage> blocos;
	private boolean ativo=true;
	private int npartes=0;
	private final int TEMPOESPERA = 1000;//ms
	public Downloader(Socket sockettemp, BloquingQueue<FileBlockRequestMessage> queue, byte[] fileContents, Queue<FileBlockRequestMessage> blocos) throws IOException {
		this.fileContents=fileContents;
		this.socket=sockettemp;
		this.queue=queue;
		this.blocos=blocos;
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
	}

	public void run() {
		FileBlockRequestMessage bloco=null;
		byte [] filePart;
		long startTime;
		long endTime;
		long diftime;
		while((!blocos.isEmpty() || queue.size()!=0) && ativo) {
			try {
				startTime = System.currentTimeMillis();
				bloco=queue.poll();
				oos.writeObject(bloco);	
				filePart=(byte[]) ois.readObject();
				putBytes(filePart,bloco);
				npartes++;
				endTime = System.currentTimeMillis();
				diftime=endTime-startTime;
				if(queue.getTamanho()!=1) {
					if(diftime>TEMPOESPERA) {
						System.out.println("tirei um");
						queue.setTamanho(queue.getTamanho()-1);
						ativo=false;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				if(queue.getTamanho()!=1) {
					try {
						if(bloco!=null) {
							queue.offer(bloco);
						}
					} catch (InterruptedException e1) {	
						e1.printStackTrace();
					}
				}
				queue.setTamanho(queue.getTamanho()-1);
				ativo=false;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			oos.writeObject("END");
			socket.close();
			System.out.println("fechei a socket");

		} catch (IOException e) {
			System.out.println("sai antes do tempo");
		}
	}
	public String toString() {
		return "Fornecedor[endereço =" + socket.getInetAddress()+","+ "porto="+socket.getPort()+"]:"+npartes;
	}
	public boolean isAtivo() {
		return ativo;
	}
	private synchronized void putBytes(byte [] filePart,FileBlockRequestMessage bloco) {
		for(int i =0;i<(int) bloco.getLength();i++) {
			fileContents[(int) (bloco.getOffset())+i]=filePart[i];
		}
	}
	class MyTimer extends Thread{

	}
}
