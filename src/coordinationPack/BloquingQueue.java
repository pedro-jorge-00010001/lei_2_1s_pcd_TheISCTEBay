package coordinationPack;
import java.util.Queue;
import java.util.LinkedList;
//implementada na aula e com uma pequena modificação
public class BloquingQueue <T> {
	private final int MAX_SIZE_DEFAULT = 100;
	private Queue<T> queue = new LinkedList<>();
	private int tamanho;
	
	public BloquingQueue ( int size){
		tamanho=size;
	}

	public BloquingQueue (){
		tamanho=0;
	}
	
	public int getTamanho() {
		return tamanho;
	}

	public void setTamanho(int tamanho) {
		this.tamanho = tamanho;
	}

	public synchronized void offer(T e) throws InterruptedException{
		if(tamanho != 0) {
			while (tamanho == size()) {
				wait();
			}
		}
		queue.add(e);
		notifyAll();
	}

	public synchronized T poll() throws InterruptedException{
		while (size() == 0) {
			this.wait();
		}
		notifyAll();
		return queue.poll();
	}


	public int size (){
		return queue.size();
	}

	public void clear(){
		queue.clear();
	}

}