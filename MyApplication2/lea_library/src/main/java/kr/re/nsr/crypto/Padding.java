package kr.re.nsr.crypto;

public abstract class Padding {

	protected int blocksize;

	public Padding(int blocksize) {
		this.blocksize = blocksize;
	}

	public abstract byte[] pad(byte[] in);

	public abstract void pad(byte[] in, int inOff);

	public abstract byte[] unpad(byte[] in);

	public abstract int getPadCount(byte[] in);

}
