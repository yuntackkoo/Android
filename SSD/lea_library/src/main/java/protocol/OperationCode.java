package protocol;

public interface OperationCode {
	public static byte Re_Request = 0;//재요청
	public static byte Join = 1;//접속
	public static byte Key_Ex = 2;//키교환
	public static byte UnLock = 3;//잠금해제
	public static byte Req_Data = 4;//로그 요청
	public static byte Req_KeyOffer = 5;//추가 등록시 키 요청
	public static byte Confirm_KeyOffer = 6;//추가 등록시 키 확인
	
	public static byte Reponse = 60;//응답
	public static byte Confirm_KeyEx = 61;//초기 등록 또는 추가 등록시 키교환 확인
	public static byte KeyOffer = 62;//키 교환 요구 받을시 키 제공
	public static byte Offer_Data = 63;//로그 요청 받을시 로그 응답
	public static byte Unlock_Other = 64;//다른 사람이 문 열때 서버에서 전송
}
