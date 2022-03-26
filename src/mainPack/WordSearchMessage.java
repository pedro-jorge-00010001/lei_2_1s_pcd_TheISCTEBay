package mainPack;

import java.io.Serializable;

public class WordSearchMessage implements Serializable {
	private String word;
	public WordSearchMessage(String stringKeyword) {
		word=stringKeyword;
	}
	public String getWord() {
		return word;
	}
	
}
