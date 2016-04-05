package common;

public class DuplicateNameEntryException extends Exception{

	public DuplicateNameEntryException(){}
	
	public DuplicateNameEntryException(String msg){
		super(msg);
	}
}