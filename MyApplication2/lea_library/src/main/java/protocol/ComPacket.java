package protocol;

public abstract class ComPacket {
	private PacketProcess process = null;
	private SsdCryptoModule cryptoModule = new SsdCryptoModule();

	abstract public void send(byte[] send);
	abstract public Packet receive();
	public void setProcess(PacketProcess process) {
		this.process = process;
	}

	public SsdCryptoModule getCryptoModule() {
		return cryptoModule;
	}
}