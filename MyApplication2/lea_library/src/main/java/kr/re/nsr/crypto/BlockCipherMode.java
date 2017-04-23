package kr.re.nsr.crypto;

public abstract class BlockCipherMode {

	public abstract String getAlgorithmName();

	public abstract int getOutputSize(int len);

	public abstract int getUpdateOutputSize(int len);

	public abstract void init(BlockCipher.Mode mode, byte[] mk);

	public abstract void init(BlockCipher.Mode mode, byte[] mk, byte[] iv);

	public abstract void reset();

	public abstract void setPadding(Padding padding);

	public abstract byte[] update(byte[] msg);

	public abstract byte[] doFinal(byte[] msg);

	public abstract byte[] doFinal();

}
