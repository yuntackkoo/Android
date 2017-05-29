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
			//임시 암호키를 만든다
			for(int i=0;i<16;i++){
				tmpkey[i] = (byte) (key[i] ^ block1[i]);
			}
			//비밀키로 라운드키를 게산
			this.crypt_module.init(Mode.ENCRYPT,this.key);
			//block1을 암호화
			block1 = this.crypt_module.update(block1);
			//임시 암호키로 라운드키를 계산
			this.crypt_module.init(Mode.ENCRYPT,tmpkey);
			//block2를 새로운 라운드키로 암호화
			block2 = this.crypt_module.update(block2);
		}
		
		System.arraycopy(block1, 0, enblock, 0, 16);
		System.arraycopy(block2, 0, enblock, 16, 16);
		
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
			//대칭키로 라운드 키를 계산
			this.crypt_module.init(Mode.DECRYPT, key);
			//대칭키로 block1을 복호화
			block1 = this.crypt_module.update(block1);
			//임시 암호키를 생성
			for(int i=0;i<16;i++){
				tmpkey[i] = (byte)(block1[i] ^ key[i]);
			}
			//임시 암호키로 라운드 키를 계산
			this.crypt_module.init(Mode.DECRYPT, tmpkey);
			//새로운 라운드 키로 복호화
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
