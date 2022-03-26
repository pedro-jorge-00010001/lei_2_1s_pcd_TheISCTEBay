package mainPack;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import coordinationPack.BloquingQueue;
import downloadPack.Distribuitor;
import downloadPack.Downloader;
import downloadPack.WriterFile;
import mainPack.GraphicInterface.MyProgressBar;
import p2pPack.UserBeingUser;
import p2pPack.WaitForConnection;
public class User extends UserData{
	private String path;
	public final static long TAMANHO=1024;
	private final int portoDir;
	private String ipAdressDir;
	
	public User(String name,String ipadress,String ipAdressDir,int portoDir,int porto, String path) {
		super(name,ipadress,porto);
		this.ipAdressDir=ipAdressDir;
		this.portoDir=portoDir;
		this.path=path;
	}

	public void runUtilizador() throws UnknownHostException, IOException {		
		Socket socket =new Socket(ipAdressDir,portoDir);//ligar ao Direct�rio
		ConnectionDirThread connectionDir=new ConnectionDirThread(socket);//thread encarregada da conex�o com o Direct�rio
		connectionDir.start();		
		GraphicInterface m=new GraphicInterface(connectionDir);//Interface Gr�fica
		m.setTitle(""+getPorto());
		WaitForConnection n=new WaitForConnection(getPorto(),path);//fazer como server para poder aceitar conex�es dos outros utilizadores
		n.start();
		m.open();
	}

	class ConnectionDirThread extends Thread {
		Socket socket;
		BufferedReader in;
		PrintWriter out;
		public ConnectionDirThread(Socket socket) throws IOException {
			this.socket=socket;
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			in= new BufferedReader(new InputStreamReader(socket.getInputStream()));	
		}
		@Override
		public void run() {
			insc();		
		}
		private void insc() {
			out.println("INSC"+addSymbols(getName()) + addSymbols(getIpadress()) + UserData.addSymbols(""+getPorto()));	//inscri��o
		}
		public void clt(WordSearchMessage wordmsg,DefaultListModel<FileDetails> files) throws IOException, InterruptedException{//procura dos utilizadores online
			out.println("CLT");
			String iptemp;
			int portotemp; 
			String msg;
			ArrayList<UserBeingUser> ThreadsList=new ArrayList<UserBeingUser>();
			do{
				msg=in.readLine();
				if(!msg.equals("END") ) {
					String vetor[]=msg.split(" ");
					iptemp=Directory.cutString(vetor[1]);
					portotemp=Integer.parseInt(Directory.cutString(vetor[2]));
					//if(!iptemp.equals(userdata.getIpadress())) {
					if(getPorto()!=portotemp) {// por causa de que estou no mesmo pc e o ip � o mesmo,verificar que nao se conecta com ele propio
						Socket sockettemp =new Socket(iptemp,portotemp);
						UserBeingUser m=new UserBeingUser(sockettemp,wordmsg,portotemp);//lig��o ao respectivo user,este a fazer como user e o que vou ligar como server
						ThreadsList.add(m);
						m.start();//faz as liga��es com todos os user,e esta thread fica junto com a UserBeingServer intercambian objetos
					}
					//}
					System.out.println(msg);
				}
			}while(!msg.equals("END"));
			for(UserBeingUser b: ThreadsList) {
				b.join();
			}
			ArrayList<FileDetails> finalList=new ArrayList<FileDetails>();
			boolean same;
			for(UserBeingUser b: ThreadsList) {
				ArrayList<FileDetails> tempList=b.getFilesDetails();
				if(tempList!=null && !tempList.isEmpty()) {
					for(FileDetails a: tempList) {
						if(!finalList.isEmpty()) {
							same=false;
							for(FileDetails d: finalList) {
								if(d.isEqual(a)) {
									d.getOwners().add(a.getOwners().get(0));
									same=true;
								}
							}
							if(!same) {
								finalList.add(a);
							}
						}else {
							finalList.add(a);
						}
					}
				}
			}
			for(FileDetails b:finalList) {
				files.addElement(b);
			}
		}
		public void search(FileDetails file,ArrayList<UserData> users,MyProgressBar progressBar, JButton botaodescarregar) {
			File[] files = new File(path).listFiles();
			String filename;
			boolean iHave=false;
			for(File f :files) {
				filename = (String)f.getName();
				if(filename.equals((file.getNome())) && f.length()==file.getTamanho()) {
					iHave=true;
				}
			}
			if(!iHave) {
				Queue<FileBlockRequestMessage> blocos = new LinkedList<FileBlockRequestMessage>();
				long lastBlock = 0;
				long nBlocks = file.getTamanho()/TAMANHO;
				if (file.getTamanho()%TAMANHO != 0){
					lastBlock = file.getTamanho()%TAMANHO;
				}
				long posInicial=0;
				int total=(int)nBlocks;
				for (; nBlocks > 0 ; nBlocks--){
					blocos.add(new FileBlockRequestMessage(file,posInicial,TAMANHO));
					posInicial=posInicial+TAMANHO;

				}
				if (lastBlock>0){
					blocos.add(new FileBlockRequestMessage(file,posInicial,lastBlock));
					System.out.println("Last bloco "+posInicial );
					total++;
				}
				//fila de bloques criada
				////////////////////////////////////////////////////////////////////////////
				BloquingQueue <FileBlockRequestMessage> queue =new BloquingQueue(users.size());
				//FilaBloqueante
				/////////////////////////////////////////////////////////////////////////////
				byte[] fileContents = new byte[(int) file.getTamanho()] ;
				Distribuitor task= new Distribuitor(queue,blocos,total,botaodescarregar);
				task.addPropertyChangeListener(progressBar);
				task.execute();
				//distribuidor de blocos----> encarregase de fazer offer() na FilaBloqueante
				////////////////////////////////////////////////////////////////////////////
				ArrayList<Downloader> threadList=new ArrayList<Downloader>();
				for(UserData b: users) {
					try {
						Socket sockettemp =new Socket(b.getIpadress(),b.getPorto());
						//ligacao com o resto dos users para fazer download por partes
						////////////////////////////////////////////////////////////////////////////	
						Downloader m=new Downloader(sockettemp,queue,fileContents,blocos);
						threadList.add(m);
						m.start();
						//encarregado dos downloads
						////////////////////////////////////////////////////////////////////////////
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				WriterFile writer =new WriterFile(threadList,fileContents,path,file.getNome());
				writer.start();		
				//espera por as thread e escreve o ficheiro
				////////////////////////////////////////////////////////////////////////////
			}else {
				botaodescarregar.setEnabled(true);
				JOptionPane.showMessageDialog(null, "Este ficheiro j� existe no seu diret�rio");
			}
		}
	}
}
