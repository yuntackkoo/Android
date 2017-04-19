package protocol;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kr.re.nsr.crypto.BlockCipher.Mode;
import kr.re.nsr.crypto.BlockCipherMode;
import kr.re.nsr.crypto.symm.LEA;

public class SsdCryptoModule {
	private byte[] key = new byte[16];
	private BlockCipherMode crypt_module = null;
	
	//배열 전체에 랜덤한 값을 넣어 키 생성
	public void keyGenerate()	{
		for(int i = 0;i<key.length;i++)		{
			key[i] = (byte) (Math.random() * 256); 
		}
	}
	
	//입력받은 문자열로 해쉬함수를 돌려 키를 생성
	/*
	 * 예제
	 * SsdCryptoModule ssd = new SsdCryptoModule();
		ssd.keyGenerate("가나다라");
		for(int i=0;i<ssd.getKey().length;i++){
			System.out.printf("%x",ssd.getKey()[i]);
		}
	 */
	public void keyGenerate(String pass){
		byte[] shabyte = null;
		
		try{
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			sha.update(pass.getBytes());
			shabyte = sha.digest();
		}catch(NoSuchAlgorithmException e){
			shabyte = null;
			System.out.println("키 생성 실패");
		}
		
		if(shabyte != null){
			for(int i=0;i<key.length;i++){
				key[i] = shabyte[i];
			}
		}
	}
	
	//생성자
	public SsdCryptoModule(){
		crypt_module = new LEA.ECB();
	}
	
	//패킷을 받아서 암호화된 byte배열로 리턴
	public byte[] enCrypt(Packet p){
		byte[] b = p.getPacket();
		byte[] block1 = new byte[16];
		byte[] block2 = new byte[16];
		byte[] tmpkey = new byte[16];
		byte[] enblock = new byte[32];
		
		System.arraycopy(b, 0, block1, 0, 16);
		System.arraycopy(b, 16, block2, 0, 16);
		
		if(key != null)		{
			this.crypt_module.init(Mode.ENCRYPT,this.key);
			block1 = this.crypt_module.update(block1);
			for(int i=0;i<16;i++){
				tmpkey[i] = (byte) (key[i] ^ block1[i]);
			}
			this.crypt_module.init(Mode.ENCRYPT,tmpkey);
			block2 = this.crypt_module.update(block2);
		}
		
		System.arraycopy(block1, 0, enblock, 0, 16);
		System.arraycopy(block2, 0, enblock, 16, 16);
		
		return enblock;
	}
	
	public byte[] enCrypt(byte[] input){
		int size = input.length/16;
		int padding_size = 16-(input.length%16);
		int block_size;
		byte[] enblock = null;
		byte[] tmpblock = new byte[16];
		byte[] tmpkey = new byte[16];
		
		if(padding_size == 0){
			block_size = 0;
		} else{
			block_size = size+1;
		}
		
		enblock = new byte[block_size*16];
		this.crypt_module.init(Mode.ENCRYPT, this.key);
		
		for(int i=0;i<size;i++){
			for(int j=0;j<tmpblock.length;j++){
				tmpblock[j] = input[(16*i)+j];
			}
			
			tmpblock = this.crypt_module.update(tmpblock);
			for(int j=0;j<tmpblock.length;j++){
				enblock[(16*i)+j] = tmpblock[j];
				tmpkey[j] = (byte) (this.key[j] ^ tmpblock[j]);
			}
			this.crypt_module.init(Mode.ENCRYPT, tmpkey);
		}
		
		for(int i=0;i<input.length%16;i++){
			tmpblock[i] = input[size*16+i];
		}
		
		for(int i=0;i<padding_size;i++){
			tmpblock[i + input.length%16] = (byte) (Math.random()*256);
		}
		tmpblock = this.crypt_module.update(tmpblock);
		for(int i=0;i<tmpblock.length;i++){
			enblock[(16*size)+i] = tmpblock[i];
		}
		
		return enblock;
	}
	
	//입력받은 바이트 배열을 복호화 하여 바이트 배열로 리턴
	public byte[] deCrypt(byte[] a){
		byte[] deblock = new byte[32];
		byte[] block1 = new byte[16];
		byte[] block2 = new byte[16];
		byte[] tmpkey = new byte[16];
		
		System.arraycopy(a, 0, block1, 0, 16);
		System.arraycopy(a, 16, block2, 0, 16);
		
		if(key != null){
			//일회용 키 생성
			for(int i=0;i<16;i++){
				tmpkey[i] = (byte)(block1[i] ^ key[i]);
			}
			this.crypt_module.init(Mode.DECRYPT, key);
			block1 = this.crypt_module.update(block1);
			this.crypt_module.init(Mode.DECRYPT, tmpkey);
			block2 = this.crypt_module.update(block2);
		}

		System.arraycopy(block1, 0, deblock, 0, 16);
		System.arraycopy(block2, 0, deblock, 16, 16);

		
		return deblock;
	}

	public byte[] getKey() {
		return key;
	}
}
