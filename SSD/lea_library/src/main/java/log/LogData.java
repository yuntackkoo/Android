package log;

import java.util.Calendar;
import java.util.Date;

public class LogData {
	private int date;
	private byte id;
	private byte devid;
	public static long start = 1451574000083L;
	private Calendar current;

	public LogData(byte[] input){
		date = (((input[0] & 0xFF)<<24)|
				((input[1] & 0xFF)<<16)|
				((input[2] & 0xFF)<<8)|
				(input[3] & 0xFF));
		id = input[4];
	}
	
	public LogData(byte id){
		this.id = id;
		current = Calendar.getInstance();
		long time = current.getTimeInMillis();
		time -= start;
		time = time/1000;
		this.date = (int)time;
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

	public byte getDevid() {
		return devid;
	}

	public void setDevid(byte devid) {
		this.devid = devid;
	}

	public static int getDate(int year,int month,int day){
		int tmp = 0;
		if(year >= 2016) {
			tmp = (year * 365) + (month * 60) + day;
			tmp += 3600;
			return tmp;
		} else return 0;
	}
}
