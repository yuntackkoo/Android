package protocol;

import java.net.SocketException;

public abstract class ComPacket {
	private PacketProcess process = null;
	private SsdCryptoModule cryptoModule = new SsdCryptoModule();
	private Packet current = null;
	private Comunication com = null;

	abstract public void send(Packet send);
	abstract public void receive();
	public void setProcess(PacketProcess process) {
		this.process = process;
	}

	public SsdCryptoModule getCryptoModule() {
		return cryptoModule;
	}

	public Packet getCurrent() {
		return current;
	}

	public void setCurrent(Packet current) {
		this.current = current;
	}

	abstract public boolean isConnect();

	public Comunication getCom() {
		return com;
	}

	public void setCom(Comunication com) {
		this.com = com;
	}
}