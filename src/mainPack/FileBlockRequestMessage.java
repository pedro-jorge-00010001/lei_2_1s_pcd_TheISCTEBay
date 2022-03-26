package mainPack;

import java.io.Serializable;

public class FileBlockRequestMessage implements Serializable {
	private FileDetails filedetails;
	private long offset;
	private long length;
	public FileBlockRequestMessage(FileDetails filedetails, long offset, long length) {
		super();
		this.filedetails = filedetails;
		this.offset = offset;
		this.length = length;
	}
	public FileDetails getFiledetails() {
		return filedetails;
	}
	public long getOffset() {
		return offset;
	}
	public long getLength() {
		return length;
	}
	
}
