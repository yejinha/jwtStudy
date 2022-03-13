# jwtStudy
-  jjwt 라이브러리를 사용해 JWT 생성, 검증 메소드 유틸
-  request, response 객체에 토큰 전달, 확인하는 컨트롤러

---


### JWT (Json Web Token) 
- Json 객체를 통해서 관리되는 토큰 방식. 헤더, 페이로드, 서명으로 구성되어 있다.
- http header 에 담아서 서로 통신.
- 클라이언트에 저장하기에  서버에서 클라이언트의 토큰 조작할 수는 없다.  
                
 *헤더* : 토큰의 타입과  해싱 알고리즘 정보 들어있다. 나중에  토큰 검증할때 서명 부분에 사용된다. 

*페이로드* : 토큰에 담을 정보를 말하며, 이 때 정보 하나씩을 클레임 이라고 한다.  
  - 공개 클레임: 충돌을 방지하기 위한 정보로 URI 형식으로 지음
- 등록된 클레임:  토큰에 대한 정보를 담기 위해 이미 정해진 정보들.     
	Ex) 토큰 발급자,  만료 시간 등 . 고정 정보
	- 공개 클레임: 충돌을 방지하기 위한 정보로 URI 형식으로 지음
	- 비공개 클레임:  클라이언트- 서버 간 협의를 위해 만든 클레임.  (유저 이름 이런거) 

*서명* : 헤더의 인코딩 값과 페이로드의 인코딩 값을 다시 해시해서 만든다. 

이렇게 만든 데이터를 .  으로 조합해 다시 base64 인코딩 시켜서 보낸다. 

### JJWT :  
   자바에서 JWT 토큰 구현하는 라이브러리 중 하나 (https://github.com/jwtk/jjwt#jws).   
   JWT 처리를 위한 builder, parser  제공한다. Dependency 추가해서 사용 시작함. 

- 헤더: 토큰의 타입, 해시 알고리즘 방식 세팅할 수 있고,   
                세팅 안하면 토큰 타입은 JWT,  해시 알고리즘은 sha256으로 세팅된다. (위의 예시)     

-  페이로드 : 기본 정보인 발급자,  발급시간, 만료시간 등은 set  함수로 개별 구현되어있다.  
                        커스텀  내용들은 map<String, String >  으로 만든 후 setClaims ()로  넣는다. 
-  서명 : signWith() 메소드에 각자 secret key 로 사용할 키를 넣는다.    
                  기본 알고리즘은 sha256 이며 , .signWith(key,SignatureAlgorithm.HS512)  같이 알고리즘 변경 가능하다.   


---
만료, 갱신 과정에 대해서 추가 조사하였다.   
이걸 사용해서 인증을 하는건 알겠는데 도대체 어떤 방식으로 운영해나가야할지 룰을 정하기 위해선 스스로 생각해봐야한다 !!  블로그에도 올리겠음. 

### access token, refresh token 만료 기준 정하기  

1) 타 회사들 사용 방법 조사 
- Facebook, Instagram : 
	- refresh token 없이 access token 으로만 사용. 
	- access token :  (기본) 1 or 2시간 , API 통해 60일짜리로 변경 가능 
	- 60일 짜리 긴 토큰으로 변경 했을 때 긴 토큰은 갱신 가능. 이를 갱신해가면서 씀.  

- linkedIn : 
	- access token : 60일, refresh token : 365일
	- access 갱신시 refresh 도 같이 갱신되는데 이때 365일로 계속 뒤로 연장되는게 아니라 처음 365일 기간에서 갱신시점에 남은 시간만큼 된다. 
	- ex) 첫 access 를 60일 쓰고 첫 발급일로부터 60일 지난 날 갱신하면, 두번째 access token 유효기간 60일 , 두번째 refresh token 유효기간 305일로 갱신 발급됨. 
	- [참고](https://docs.microsoft.com/en-us/linkedin/shared/authentication/programmatic-refresh-tokens)
	
- Kakao: 
	- access token : 6시간 (rest api 기준) , refresh token :  2달 (유효기간 한 달 남은 시점부터 갱신 가능) 


2) 토큰 만료시키는 법    
로그인 시에 토큰을 발급하는건 쉽지만,  로그아웃 시에는 도대체 어떻게 토큰을 만료시킬까?   

일단 jwt 자체가 토큰 자체에 값을 가지고 있는 것이기에 만료처리가 어렵다. (불가능 하다는 말도 가능할 거 같다. 여러 stackoverflow 논의들을 보니까 간편히 그렇게 말하는 듯.)  
그래서 access, refresh 구조를 두고 access 는 만료 기간을 짧고, refresh는 만료기간이 다 되어갈때만 갱신 가능 혹은 refresh의 만료기간 고정(링크드인 방식)으로 보완해나간다고 이해했다.  

즉, 토큰 자체를 취소시킬 수 없기에 더 이상 갱신되지 못함으로써 취소 처리를 한다고 보면 된다.  

간단히 카카오의 경우로 설명하면 access 를 가지고 access, 또 필요에 의해선 refresh 갱신하면서 지내다가 사용자가 로그아웃함. 
-> access 는 더 이상 갱신될 일 없기에 금방 만료됨.  갱신할 수 있는 access 없기에 refresh 도 일정기간 후 만료되고, access 없으니까 서비스에 비정상적인 접근 안됨. 
-> 이런 식으로 토큰 자체를 취소시키지 않아도 인증 처리됨. 





