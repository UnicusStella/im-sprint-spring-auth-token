package com.codestates.seb.TokenServer.Service;

import com.codestates.seb.TokenServer.Domain.Userdata;
import com.codestates.seb.TokenServer.Entity.UserList;
import com.codestates.seb.TokenServer.Repository.TokenRepository;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {

    private final static String SIGN_KEY = "codestateskey";
    private final TokenRepository tokenRepository;

    @Autowired
    public TokenService(TokenRepository tokenRepository){
        this.tokenRepository = tokenRepository;
    }

    // 유저 정보와 유효 시간을 입력 받아 토큰을 생성합니다.
    public String CreateJwtToken(UserList userList, Long time) {
        // claim에 포함 되어야 하는 내용은 "userId", "password" 입니다.
        // TODO :
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("fresh")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Duration.ofSeconds(time).toMillis()))
                .claim("userId", userList.getUserId())
                .claim("password", userList.getPassword())
                .signWith(SignatureAlgorithm.HS256,SIGN_KEY)
                .compact();
    }

    // 토큰의 유효성을 체크하여 알맞은 응답을 보냅니다.
    public Map<String, String> CheckJWTToken(String key){
        try{
            // TODO :
            Claims claims = Jwts.parser().setSigningKey(SIGN_KEY)
                    .parseClaimsJws(key)
                    .getBody();


            // 토큰을 체크 후 "userId 값을 리턴합니다."
            String userid = (String)claims.get("userId");
            return new HashMap<>(){
                {
                    put("id", userid);
                    put("message :", "ok");
                }
            };

        }catch (ExpiredJwtException e){
            return new HashMap<>(){
                {
                    put("id", null);
                    put("message", "토큰 시간이 만료 되었습니다.");
                }
            };

        }catch (JwtException e){
            return new HashMap<>(){
                {
                    put("id", null);
                    put("message", "토큰이 유효하지 않습니다.");
                }
            };
        }
    }

    // userId를 기준으로 유저 데이터를 찾아옵니다.
    public UserList FindByUserId(String userId){
        return tokenRepository.UserFindByUserId(userId);
    }

    // userdata 객체를 기준으로 id와 password를 비교하여 유저 데이터를 찾아옵니다.
    public UserList FindUser(Userdata userdata){
        UserList userList = tokenRepository.UserFindByUserId(userdata.getUserId());
        if(userList.getPassword().equals(userdata.getPassword())){
            return userList;
        }else {
            return null;
        }
    }

    // 헤더에 "Bearer "가 포함 되어 있는지 체크합니다.
    public void validationAuthorizationHeader(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException();
        }
    }
    // 헤더에 "Bearer "를 제거합니다.
    public String extractToken(String authorizationHeader) {
        return authorizationHeader.substring("Bearer ".length());
    }



}
