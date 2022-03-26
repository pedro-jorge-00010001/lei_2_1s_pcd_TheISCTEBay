package mainPack;

import java.io.Serializable;
import java.util.ArrayList;

public class FileDetails implements Serializable{
	private String nome;
	private long tamanho;
	private ArrayList<UserData> owners = new ArrayList<UserData>();
	public FileDetails(String nome, long tamanho,UserData owner) {
		owners.add(owner);
		this.nome = nome;
		this.tamanho = tamanho;
	}
	public String getNome() {
		return nome;
	}
	public ArrayList<UserData> getOwners() {
		return owners;
	}
	public long getTamanho() {
		return tamanho;
	}
	@Override
	public String toString() {
		return  nome;
	}
	public boolean isEqual(FileDetails f) {
		boolean value=false;
		if(nome.equals(f.getNome()) && tamanho==f.getTamanho())
			value=true;
		return value;
	}
	
}
