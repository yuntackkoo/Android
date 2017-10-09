package log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LogData {
	private long date;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");

	public LogData(byte[] input){
		date = 0;
		date = (long)(((input[0] & 0x00000000000000FFL)<<56) |
				((input[1] & 0x00000000000000FFL)<<48) |
				((input[2] & 0x00000000000000FFL)<<40) |
				((input[3] & 0x00000000000000FFL)<<32) |
				((input[4] & 0x00000000000000FFL)<<24) |
				((input[5] & 0x00000000000000FFL)<<16) |
				((input[6] & 0x00000000000000FFL)<<8) |
				((input[7] & 0x00000000000000FFL)));
	}
	
	public LogData(){
		this.date = System.currentTimeMillis();
	}
	
	public byte[] getByte(){
		byte[] returnbyte = new byte[8];
		returnbyte[0] = (byte)((this.date & 0xFF00000000000000L)>>56);
		returnbyte[1] = (byte)((this.date & 0x00FF000000000000L)>>48);
		returnbyte[2] = (byte)((this.date & 0x0000FF0000000000L)>>40);
		returnbyte[3] = (byte)((this.date & 0x000000FF00000000L)>>32);
		returnbyte[4] = (byte)((this.date & 0x00000000FF000000L)>>24);
		returnbyte[5] = (byte)((this.date & 0x0000000000FF0000L)>>16);
		returnbyte[6] = (byte)((this.date & 0x000000000000FF00L)>>8);
		returnbyte[7] = (byte)((this.date & 0x00000000000000FFL));
		return returnbyte;
	}

	public long getDate() {
		return date;
	}

	public void setDate(int date) {
		this.date = date;
	}

	public static int getDate(int year,int month,int day){
		int tmp = 0;
		if(year >= 2016) {
			tmp = (year * 365) + (month * 60) + day;
			tmp += 3600;
			return tmp;
		} else return 0;
	}

	public String dateString(){
		Date d = new Date(this.date);
		System.out.println(d.toString());
		return dateFormat.format(d);
	}
}
