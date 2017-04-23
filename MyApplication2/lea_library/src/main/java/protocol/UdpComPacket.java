package protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import javax.naming.TimeLimitExceededException;

public class UdpComPacket extends ComPacket{
	private DatagramPacket udp_send = null;
	private DatagramPacket udp_receive = null;
	private DatagramSocket soc = null;
	private DatagramSocket receive_socket = null;
	private InetAddress dest = null;
	private byte[] buffer = new byte[32];
	private byte[] send_data = new byte[32];
	private PacketProcess process = null;
	
	public UdpComPacket(String dn,String port) {
		try{
			dest = InetAddress.getByName(dn);
			udp_send = new DatagramPacket(send_data, send_data.length, dest, Integer.parseInt(port));
			udp_receive = new DatagramPacket(buffer, buffer.length);
			receive_socket = new DatagramSocket(Integer.parseInt(port));
			soc = new DatagramSocket();
			soc.setSoTimeout(1000);
			receive_socket.setSoTimeout(1000);
		}
		catch(UnknownHostException e){
			
		}
		catch(SocketException e){
			
		}
	}

	public UdpComPacket(String port){
		try {
			this.receive_socket = new DatagramSocket(Integer.parseInt(port));
			udp_receive = new DatagramPacket(this.buffer,this.buffer.length);
		} catch (SocketException e) {
		}
	}

	@Override
	public void send(byte[] send) {
		for(int i=0;i<this.send_data.length;i++){
			this.send_data[i] = send[i];
		}
		try {
			soc.send(this.udp_send);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public Packet receive() {
		int check = 0;
		boolean flag = true;
		for(;flag;){
			try {
				System.out.println("패킷 받음");
				receive_socket.receive(udp_receive);
				flag = false;
			} catch (SocketTimeoutException e){
				if(check < 3){
					check++;
					System.out.println("시간 초과?");
				}
				else{
					System.out.println("시간 초과");
					return null;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		process.doProcess();
		return null;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}
}
