package coordinationPack;
import java.util.ArrayList;
//implementada por mim e acorde ao meu trabalho
public class ThreadPool {
	private BloquingQueue <Thread> tasks=new BloquingQueue<Thread>();
	private ArrayList<ThreadWorker> threadWorkers = new ArrayList<ThreadWorker>(); 
	
	public ThreadPool(int i) {
		for(;i>0;i--) {
			ThreadWorker threadWorker=new ThreadWorker();
			threadWorkers.add(threadWorker);
			threadWorker.start();
		}
	}

	public synchronized void submit(Thread task) throws InterruptedException {
		tasks.offer(task);
	}

	class ThreadWorker extends Thread{
		public void run() {
			Thread task;
			while(true) {
				try {
					task=tasks.poll();
					task.run();
					task.join();		//executa uma tarefa e espera por ela acabar assim não vai ir a fila e por outra thread a correr
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
