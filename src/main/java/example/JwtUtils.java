package example;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;


import java.security.Key;
import java.time.Duration;
import java.util.Date;

public class JwtUtils {
    //1. 서명에 사용할 키 생성 (실개발때는 프로퍼티로 관리)
    private String keyString = "Mauristinciduntpurustortoretfusceaaaabbbbeeeccc";  //랜덤 문장 생성기에서 랜덤 생성

    public String createToken(String userName, String role) {
        //2. 만료시간 설정
        Date now = new Date();
        Date expiration = new Date(now.getTime() + Duration.ofDays(1).toMillis()); // 만료기간 1일

        //3. JWT 생성
        Claims claims = Jwts.claims().setSubject(userName); // JWT payload 에 저장되는 정보단위
        claims.put("roles", role); // role 받아서 권한 설정해준다. 나중에는 이 권한 파싱해서 권한제어에 사용 . (admin -> 관리자 권한, user -> 일반사용자)

        String jws = Jwts.builder().setSubject(userName)  //토큰 발급 받은 사람 특정
                .setIssuer("testProduct")  //발급자
                .setIssuedAt(now).setExpiration(expiration) //발급시간,  만료시간
                .claim("roles",role)
                .setSubject(userName)
                .signWith(SignatureAlgorithm.HS256,keyString)
                .compact();

        return jws;

    }

    public Jws<Claims> isValid(String jws){
        //userName 뽑아내어 리턴  -> 추후에는 롤이나 다른 중요 값 뽑아서 검증해도 됨.
        String userName =null;
        Jws<Claims> parsedJwt =null;
        try {
            parsedJwt = Jwts.parser().setSigningKey(keyString).parseClaimsJws(jws);
        }catch (SignatureException e){ // 서명 인증 안되는 경우
            throw new SignatureException("Invalid token");
        }catch (ExpiredJwtException e){ //만료된 경우
            throw new ExpiredJwtException(parsedJwt.getHeader(),parsedJwt.getBody(),"Expired!");
        }
        return parsedJwt;
    }

    // jwt 파싱한 내용 확인
    public String getInfoByToken(Jws<Claims> parsedJwt) throws Exception {
        String jwtBody  =parsedJwt.getBody().toString();
        if (jwtBody == null){
            throw new Exception("jwtBody is empty");
        }
        return jwtBody;
    }


}
