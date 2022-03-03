import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import example.*;

import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtTest {
    static JwtUtils jwtUtil =new JwtUtils() ;
    static String jwt = null;

    @BeforeAll
    static void init(){
        jwt = jwtUtil.createToken("testUser","admin");
        System.out.println(jwt);
    }

    @Test
    void isValid(){
        assertThat(jwtUtil.isValid(jwt)).isNotNull();
    }

    @Test
    void getInfoByParsing() throws Exception {
        String body = jwtUtil.getInfoByToken(jwtUtil.isValid(jwt));
        System.out.println(body);
    }

}
