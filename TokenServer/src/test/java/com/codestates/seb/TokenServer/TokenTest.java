package com.codestates.seb.TokenServer;

import com.codestates.seb.TokenServer.CodeStatesSubmit.Submit;
import com.codestates.seb.TokenServer.Domain.Check.ResData;
import com.codestates.seb.TokenServer.Domain.CreateToken.TokenRes;
import com.codestates.seb.TokenServer.Domain.Refresh.RefreshRes;
import com.codestates.seb.TokenServer.Domain.Userdata;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;

@AutoConfigureMockMvc
@SpringBootTest

public class TokenTest {

    private static Submit submit = new Submit();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private EntityManager entityManager;

    private RestTemplate restTemplate = new RestTemplate();

    @AfterAll
    static void after() throws Exception {
        submit.SubmitJson("im-sprint-spring-token", 5);
        submit.ResultSubmit();
    }

    @BeforeEach
    public void beforEach() throws Exception{
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(MockMvcResultHandlers.print())
                .build();

        objectMapper = Jackson2ObjectMapperBuilder.json()
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .modules(new JavaTimeModule())
                .build();
    }

    @Test
    @DisplayName(value = "유저 정보가 유효하지 않으면 토큰 생성을 거절합니다.")
    void CheckUser() throws Exception{
        MvcResult result = null;
        String url = "/login";
        String standard = "{\"data\":null,\"message\":\"not authorized\"}";

        Userdata userdata = new Userdata();
        userdata.setUserId("kimcoding");
        userdata.setPassword("12345");

        try{
            String content = objectMapper.writeValueAsString(userdata);
            result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                            .content(content)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            submit.ResultSave(result.getResponse().getContentAsString().equals(standard));
        }catch (Exception e){
            System.out.println(e);
        }finally {
            Assertions.assertEquals(result.getResponse().getContentAsString(),standard);
        }
    }

    @Test
    @DisplayName(value = "유저 정보가 유효하면 토큰을 생성합니다.")
    void CreateToken() throws Exception {
        MvcResult result = null;
        String url = "/login";
        String standard = "refreshToken : kimcoding accessToken : kimcoding message : ok";
        String CheckData = "";

        Userdata userdata = new Userdata();
        userdata.setUserId("kimcoding");
        userdata.setPassword("1234");

        try{
            String content = objectMapper.writeValueAsString(userdata);
            result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                            .content(content)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            //refreshToken 유효성 체크
            Cookie cookie = result.getResponse().getCookie("refreshToken");
            Claims refreshToken_claims = Jwts.parser().setSigningKey("codestateskey")
                    .parseClaimsJws(cookie.getValue())
                    .getBody();

            String refreshToken_userid = (String)refreshToken_claims.get("userId");

            //accessToken 유효성 체크
            String bodyData = result.getResponse().getContentAsString();
            TokenRes tokenRes =objectMapper.readValue(bodyData, TokenRes.class);

            Claims accessToken_claims = Jwts.parser().setSigningKey("codestateskey")
                    .parseClaimsJws(tokenRes.getData().getAccessToken())
                    .getBody();

            String accessToken_userid = (String)accessToken_claims.get("userId");

            CheckData = "refreshToken : " + refreshToken_userid + " accessToken : " + accessToken_userid + " message : " + tokenRes.getMessage();
            submit.ResultSave(CheckData.equals(standard));
        }catch (Exception e){
            System.out.println(e);
        }finally {
            Assertions.assertEquals(CheckData,standard);
        }
    }

    @Test
    @DisplayName(value = "accesstoken이 유효하면 유저 데이터를 전달합니다.")
    void AccesstokenChek() throws Exception{
        MvcResult result = null;
        ResData resData = null;

        Userdata userdata = new Userdata();
        userdata.setUserId("kimcoding");
        userdata.setPassword("1234");

        try{
            String content = objectMapper.writeValueAsString(userdata);
            result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                            .content(content)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            TokenRes tokenRes =objectMapper.readValue(result.getResponse().getContentAsString(), TokenRes.class);

            result = mockMvc.perform(MockMvcRequestBuilders.get("/accesstokenrequest")
                            .header("authorization", "Bearer " + tokenRes.getData().getAccessToken())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            resData = objectMapper.readValue(result.getResponse().getContentAsString(), ResData.class);
            submit.ResultSave(resData.getData().getUserInfo().getUserId().equals("kimcoding"));

        }catch (Exception e){
            System.out.println(e);
        }finally {
            Assertions.assertEquals(resData.getData().getUserInfo().getUserId(),"kimcoding");
        }
    }

    @Test
    @DisplayName(value = "refreshToken이 유효하면 새로운 accesstoken과 유저 정보를 전달합니다.")
    void refreshTokenChek1() throws Exception{
        MvcResult result = null;
        RefreshRes refreshRes = null;
        String user = "";
        String standard = "";

        Userdata userdata = new Userdata();
        userdata.setUserId("kimcoding");
        userdata.setPassword("1234");

        try{
            String content = objectMapper.writeValueAsString(userdata);
            result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                            .content(content)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            Cookie cookie = result.getResponse().getCookie("refreshToken");
            result = mockMvc.perform(MockMvcRequestBuilders.get("/refreshtokenrequest")
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            System.out.println(result.getResponse().getContentAsString());
            submit.ResultSave(result.getResponse().getContentAsString().equals(standard));

            refreshRes = objectMapper.readValue(result.getResponse().getContentAsString(), RefreshRes.class);

            Claims claims = Jwts.parser().setSigningKey("codestateskey")
                    .parseClaimsJws(refreshRes.getData().getAccessToken())
                    .getBody();

            user = (String)claims.get("userId");
            submit.ResultSave(user.equals(refreshRes.getData().getUserInfo().getUserId()));

        }catch (Exception e){
            System.out.println(e);
        }finally {
            Assertions.assertEquals(user,refreshRes.getData().getUserInfo().getUserId());
        }
    }

    @Test
    @DisplayName(value = "refreshToken이 유효하지 않으면 에러 메세지를 전달합니다.")
    void refreshTokenChek2() throws Exception{
        MvcResult result = null;
        String standard = "{\"data\":null,\"message\":\"refresh token not provided\"}";
        try{
            Cookie cookie = new Cookie("refreshToken", "");
            result = mockMvc.perform(MockMvcRequestBuilders.get("/refreshtokenrequest")
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();
            submit.ResultSave(result.getResponse().getContentAsString().equals(standard));
        }catch (Exception e){
            System.out.println(e);
        }finally {
            Assertions.assertEquals(result.getResponse().getContentAsString(),standard);
        }
    }

}
