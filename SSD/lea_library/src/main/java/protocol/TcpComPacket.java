package protocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class TcpComPacket extends ComPacket{
	private SocketAddress socket = null;
	private java.net.Socket tcp_send = null;
	private BufferedOutputStream out = null;
	private BufferedInputStream in = null;
	private byte[] buffer = new byte[33];
	private PacketProcess process = null;
	private int port = 0;
	
	public TcpComPacket(String dn, String port) {
		try{
			this.port = Integer.parseInt(port);
			tcp_send = new Socket();
			socket = new InetSocketAddress(dn,this.port);
			tcp_send.connect(socket,20000);
			in = new BufferedInputStream(tcp_send.getInputStream());
			out = new BufferedOutputStream(tcp_send.getOutputStream());
			tcp_send.setSoTimeout(100000);
		}
		catch (ConnectException e){
			System.out.println(e.getMessage());
		}
		catch(IOException e){
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void send(Packet send) {
		try {
			//byte[] tmp = super.getCryptoModule().enCrypt(send);
			send.fillPadding();
			if(isConnect()) {
				out.write(send.getPacket());
				out.flush();
			} else{
				if(out != null) {
					out.close();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			//tmp = null;
		}
	}

	@Override
	public void receive() {
		int check = 0;
		boolean flag = true;
		for(;flag;){
			try {
				if(isConnect()) {
					in.read(buffer);
				} else {
					break;
				}
				super.setCurrent(new Packet(buffer));
				flag = false;
                check = 0;
			} catch (SocketException e){
				try {
					in.close();
					tcp_send.close();
					super.setCurrent(null);
				} catch (IOException e2){
					System.out.println("연결 해제");
				}
			}
			catch (SocketTimeoutException e){
				if(check < 15){
					check++;
					System.out.println("시간 초과?");
				}
				else{
					System.out.println("시간 초과");
					flag = false;

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(isConnect()) {
			this.process.doProcess(super.getCom());
		}
		super.setCurrent(null);
	}

	@Override
	public void setProcess(PacketProcess process) {
		this.process = process;
	}

	@Override
	public boolean isConnect() {
		return tcp_send.isConnected();
	}

	@Override
	protected void finalize() throws Throwable {
		if(tcp_send != null){
			tcp_send.close();
		}
		if(in != null){
			in.close();
		}
		if(out != null){
			out.close();
		}
		super.finalize();
	}
}