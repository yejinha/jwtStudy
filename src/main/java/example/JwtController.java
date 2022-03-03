package example;

import java.util.Map;

public class JwtController {

    @GetMapping(value ="/example")
    @ResponseBody
    public ResponseEntity<?> loginAction( @RequestParam(value="name") String name, @RequestParam(value="pwd") String pwd, HttpServletResponse response) {

        JwtController jwtcon = new JwtController();
        String token = null;

        //id pwd 검증하는 부분 -> 맞으면 토큰 발급. 여기선 그냥 yj 이면 friend ,  choi 면 visitor 함.
        if(name.equals("yj") ){
            token = jwtcon.createToken(name,"friend");
        }else if (name.equals("choi")){
            token = jwtcon.createToken(name,"visitor");
        }
        //

        response.setHeader("X-AUTH-TOKEN", token);  // response header 세팅
        Cookie cookie = new Cookie("X-AUTH-TOKEN", token); // 쿠키에 저장해 다음 요청때 쓸 수 있도록 함.
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);

        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    // @requestHeader 어노테이션 사용해 요청의 헤더값 사용
    @GetMapping(value ="/exampletoken")
    @ResponseBody
    public ResponseEntity<?> loginActionWithToken( @RequestHeader Map<String, String> data) {

        JwtController jwtcon = new JwtController();
        String token = null;

        //header 에  accesstoken이란 값이 있다면 파싱
        if(data.get("accesstoken") !=null) {
            token =jwtcon.validToken(data.get("accesstoken"));
        }

        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
