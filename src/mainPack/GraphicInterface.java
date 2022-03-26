package mainPack;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mainPack.User.ConnectionDirThread;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

class GraphicInterface extends JFrame{
	private DefaultListModel<FileDetails> files = new DefaultListModel<FileDetails>();
	private JList<FileDetails> filesList = new JList<FileDetails>(files);
	private ConnectionDirThread connectionDir;
	private JTextField aprocurar;
	private MyProgressBar progressBar;
	private JButton botaodescarregar;

	public GraphicInterface(ConnectionDirThread connectionDir){
		this.connectionDir=connectionDir;
		setLayout(new BorderLayout());

		JPanel procura=new JPanel();
		procura.setLayout(new GridLayout(1,3));
		JLabel text1=new JLabel("Texto a procurar");
		procura.add(text1);
		aprocurar=new JTextField("");
		procura.add(aprocurar);
		JButton botaoProcura =new JButton("Procurar");
		botaoProcura.addActionListener(new Procura());
		procura.add(botaoProcura);

		JPanel resto=new JPanel();
		resto.setLayout(new GridLayout(1,2));
		resto.add(filesList);
		filesList.addListSelectionListener(new Selection());	

		JPanel botoes=new JPanel();
		botoes.setLayout(new GridLayout(2,1));
		botaodescarregar= new JButton("Descarregar");
		botaodescarregar.addActionListener(new Descarregar());
		progressBar=new MyProgressBar();
		progressBar.setMaximum(100);
		botoes.add(botaodescarregar);
		botoes.add(progressBar);
		resto.add(botoes);
		add(resto,BorderLayout.CENTER);
		add(procura,BorderLayout.NORTH);
	}
	private class Selection implements ListSelectionListener{
		private int previous = -1;
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (filesList.getSelectedIndex() != -1
					&& previous != filesList.getSelectedIndex()) {
				System.out.println(filesList.getSelectedValue().getTamanho());
				for(UserData b:filesList.getSelectedValue().getOwners()) {
					System.out.println(b.getPorto()+" "+b.getIpadress());
				}
			}
			previous = filesList.getSelectedIndex();
		}

	}
	private class Procura implements ActionListener{ //clase interna
		public void actionPerformed(ActionEvent e) {
			String text=aprocurar.getText().trim();
			if(!text.equals("")) {
				try {
					if(files != null ) {
						files.clear();
					}
					connectionDir.clt(new WordSearchMessage(text),files);//trim() para tirar espa�os em branco
				} catch (IOException | InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	private class Descarregar implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(filesList != null && filesList.getSelectedIndex()!=-1) {
				botaodescarregar.setEnabled(false);
				//				progressBar.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				System.out.println(filesList.getSelectedValue().getNome());
				connectionDir.search(filesList.getSelectedValue(),filesList.getSelectedValue().getOwners(),progressBar,botaodescarregar);
			}
		}

	}
	public class MyProgressBar extends JProgressBar	implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if ("progress" == evt.getPropertyName()) {
				int progress = (Integer) evt.getNewValue();
				progressBar.setValue(progress);
			} 
		}
	}
	public void open() {
		setLocation(500, 250);
		setVisible(true);
		setSize(300, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}