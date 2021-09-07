package com.codestates.seb.TokenServer.Controller;

import com.codestates.seb.TokenServer.Domain.Userdata;
import com.codestates.seb.TokenServer.Entity.UserList;
import com.codestates.seb.TokenServer.Service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class TokenController {
    //accessToken 유효 시간
    private final static Long ACCESS_TIME = 15L;
    //refreshToken 유효 시간
    private final static Long REFRESH_TIME = 1800L;
    private final TokenService tokenService;

    @Autowired
    public TokenController(TokenService tokenService){
        this.tokenService = tokenService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<?> TokenIndex(){
        return ResponseEntity.ok().body("Hello CodeStates!");
    }

    // id와 password를 비교한 후 토큰을 발행합니다.
    @PostMapping(value = "/login")
    public ResponseEntity<?> UserLogin(@RequestBody(required = true) Userdata LoginData, HttpServletResponse response){
        try{
            // id와 password 를 기준으로 데이터베이스에 일치하는 유저 데이터를 불어옵니다.
            // 유저 데이터에 userId 와 password를 토큰에 담아 accesstoken과 refreshToken을 생성합니다.
            // refreshToken은 쿠키(key -> refreshToken)에 담겨 전달 됩니다.
            // accesstoken은 body에 담겨 전달 됩니다.
            // 지속 시간은 필드에 선언 된 상수를 사용합니다.
            // TODO:


        }catch (Exception error){
            System.out.println("Error Message : " + error);
            return ResponseEntity.badRequest().body("Error : " + error);
        }
    }

    // token이 유효한지 확인 후 user 데이터를 전달합니다.
    @GetMapping(value = "/accesstokenrequest")
    public ResponseEntity<?> GetAccesstokenRequest(@RequestHeader Map<String, String> requestHeader) {
        tokenService.validationAuthorizationHeader(requestHeader.get("authorization"));

        //해더를 통해 받은 accesstoken에 유효성을 검증합니다.
        //accesstoken이 유효하면 데이터베이스에서 동일한 userId 값을 가진 유저 데이터 정보를 찾아 응답합니다.
        //응답 데이터에는 password 만 제외합니다.
        //TODO :
        Map<String, String> checkResult;

        if(checkResult.get("id") != null){

        }else{

        }
    }

    // accesstoken 이 만료되면, refreshToken를 검증하여 새로운 accesstoken을 발급합니다.
    @GetMapping(value = "/refreshtokenrequest")
    public ResponseEntity<?> GetRefreshtokenRequest(HttpServletRequest request){
        // 쿠키를 통해 refreshToken을 받아옵니다.
        // 쿠키에 refreshToken 값이 담겨 있는지 검중합니다. -> 값이 없으면 404를 리턴합니다.
        // refreshToken 검증 후 데이터베이스에서 동일한 토큰에 id와 동일한 user를 찾습니다.
        // 찾은 user 데이터를 사용하여 새 accesstoken을 발급하여 리턴합니다.

        String cookiesResult = "";

        if(cookiesResult.equals("")){
            return ResponseEntity.badRequest().body(new HashMap<>() {
                {
                    put("data", null);
                    put("message", "refresh token not provided");
                }
            });
        }

        // TODO :
        Map<String, String> checkResult;
        if(checkResult.get("id") != null){


        }else{

        }
    }

}
