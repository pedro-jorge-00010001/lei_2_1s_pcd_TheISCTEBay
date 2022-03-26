package downloadPack;
import java.awt.Toolkit;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.SwingWorker;

import coordinationPack.BloquingQueue;
import mainPack.FileBlockRequestMessage;
public class Distribuitor extends SwingWorker<Void, Integer> {
	private BloquingQueue <FileBlockRequestMessage> queue;
	private Queue<FileBlockRequestMessage> blocos = new LinkedList<FileBlockRequestMessage>();
	private FileBlockRequestMessage bloco;
	private int total;
	private JButton boataodescarregar;
	///fila de bytes
	public Distribuitor(BloquingQueue<FileBlockRequestMessage> queue,Queue<FileBlockRequestMessage> blocos,int total, JButton botaodescarregar) {
		super();
		this.boataodescarregar=botaodescarregar;
		this.total=total;
		this.queue = queue;
		this.blocos=blocos;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		int progress = 0;
		setProgress(0);
		while(!blocos.isEmpty()) {
			try {	
				progress++;
				setProgress(progress*100/total);
				bloco=blocos.poll();
				queue.offer(bloco);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	@Override
    public void done() {
        Toolkit.getDefaultToolkit().beep();
        boataodescarregar.setEnabled(true);
    }
}
