package protocol;

public abstract class ComPacket {
	PacketProcess process = null;
	
	abstract public void send(byte[] send);
	abstract public Packet receive();
	public void setProcess(PacketProcess process) {
		this.process = process;
	}
	
}
