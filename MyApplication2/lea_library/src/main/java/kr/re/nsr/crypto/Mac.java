package kr.re.nsr.crypto;

public abstract class Mac {

	public abstract void init(byte[] key);

	public abstract void reset();

	public abstract void update(byte[] msg);

	public abstract byte[] doFinal(byte[] msg);

	public abstract byte[] doFinal();

}
