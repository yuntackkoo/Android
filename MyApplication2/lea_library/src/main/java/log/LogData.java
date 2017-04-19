package log;

public class LogData {
	private int date;
	private byte id;
	
	public LogData(byte[] input){
		date = ((input[0]<<24)|
				(input[1]<<16)|
				(input[2]<<8)|
				(input[3]));
		id = input[4];
	}
	
	public LogData(int date, byte id){
		this.date = date;this.id = id;
	}
	
	public byte[] getByte(){
		byte[] returnbyte = new byte[5];
		returnbyte[0] = (byte)((this.date & 0xFF000000)>>24);
		returnbyte[1] = (byte)((this.date & 0x00FF0000)>>16);
		returnbyte[2] = (byte)((this.date & 0x0000FF00)>>8);
		returnbyte[3] = (byte)((this.date & 0x000000FF));
		returnbyte[4] = this.id;
		
		return returnbyte;
	}

	public int getDate() {
		return date;
	}

	public void setDate(int date) {
		this.date = date;
	}

	public byte getId() {
		return id;
	}

	public void setId(byte id) {
		this.id = id;
	}
	
	
}
