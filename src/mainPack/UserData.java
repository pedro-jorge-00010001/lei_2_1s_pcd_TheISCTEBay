package mainPack;

import java.io.Serializable;

public class UserData implements Serializable{
	private String name;
	private String ipadress;
	private int porto;
	
	public UserData(String name, String ipadress, int porto) {
		this.name=name;
		this.ipadress=ipadress;
		this.porto=porto;
	}
	public String getName() {
		return name;
	}
	public String getIpadress() {
		return ipadress;
	}
	public int getPorto() {
		return porto;
	}
	public String toString() {
		String m="CLT"+ addSymbols(getIpadress()) + addSymbols(""+getPorto());
		return m;
	}
	
	public static String addSymbols(String string) {
		String s=" <" + string +">";
		return s;
	}
}
