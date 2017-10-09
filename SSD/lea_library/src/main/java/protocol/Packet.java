package protocol;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Packet {
	private byte code;
	private byte padding_size;
	private byte[] padding = null;
	private byte id;
	private byte[] nonce = new byte[4];
	private List<Byte> data = new LinkedList<Byte>();
	public static final int PacketSize = 33;

	public byte getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = (byte)code;
	}

	public byte getId() {
		return id;
	}

	public void setId(int id) {
		this.id = (byte)id;
	}

	public byte[] getData() {
		byte[] tmp = new byte[this.data.size()];
		for (int i=0;i<this.data.size();i++){
			tmp[i] = this.data.get(i);
		}
		return tmp;
	}

	public Packet(){

	}

	public Packet(byte[] data){
		int offset = 0;
		this.code = data[offset++];
		if(data[offset] >= 24) {
			this.padding_size = 24;
		} else{
			this.padding_size = data[offset++];
		}
		offset += this.padding_size;
		this.id = data[offset++];
		this.nonce[0] = data[offset+3];
		this.nonce[1] = data[offset+2];
		this.nonce[2] = data[offset+1];
		this.nonce[3] = data[offset+0];
		offset +=4;
		for(int i=offset;i<PacketSize;i++){
			this.data.add(data[i]);
		}
	}

	public void setData(byte[] b) {
		data.clear();
		for(int i=0;i<b.length;i++)
		{
			data.add(b[i]);
		}
	}
	public void getNonce(byte[] nonce) {
		for(int i = 0;i<this.nonce.length;i++){
			nonce[i] = this.nonce[i];
		}
	}
	public int getNonce(){
		int result = 0;
		int[] tmp = new int[this.nonce.length];
		for(int i=0;i<tmp.length;i++){
			tmp[i] = this.nonce[i] & 0xFF;
		}
		result = tmp[0]*256*256*256
				+ tmp[1]*256*256
				+ tmp[2]*256
				+ tmp[3];
		return result;
	}

	public void setNonce(int nonce) {
		this.nonce[0] = (byte)((0xFF000000 & nonce)>>24);
		this.nonce[1] = (byte)((0x00FF0000 & nonce)>>16);
		this.nonce[2] = (byte)((0x0000FF00 & nonce)>>8);
		this.nonce[3] = (byte)(0x000000FF & nonce);
	}

	public void setNonce(byte[] nonce) {
		for(int i=0;i<nonce.length;i++){
			this.nonce[i] = nonce[i];
		}
	}

	public byte getPadding_size() {
		return padding_size;
	}

	public byte[] getPacket(){
		List<Byte> tmp = new ArrayList<>();
		tmp.add(code);

		tmp.add(this.padding_size);

		for(int i=0;i<this.padding_size;i++)		{
			tmp.add(this.padding[i]);
		}

		tmp.add(this.id);

		for(int i=0;i<this.nonce.length;i++)		{
			tmp.add(this.nonce[i]);
		}

		for(int i=0;i<this.data.size();i++)		{
			tmp.add(this.data.get(i));
		}


		byte[] b = new byte[tmp.size()];
		for(int i = 0;i<tmp.size();i++){
			b[i] = tmp.get(i);
		}

		return b;
	}

	public void fillPadding(){
		this.padding_size = (byte) (Packet.PacketSize - (data.size() + 3 + this.nonce.length));
		this.padding = new byte[padding_size];
		for(int i=0;i<this.padding_size;i++){
			this.padding[i] = (byte) (Math.random() * 256);
		}
	}

	public boolean comp(int seq_num){
		if(this.getNonce() == seq_num){
			return true;
		} else {
			return false;
		}
	}
}