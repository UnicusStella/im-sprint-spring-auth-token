package com.codestates.seb.TokenServer.Domain.CreateToken;

public class TokenRes {
    private TokenData data;
    private String message;

    public TokenData getData() {
        return data;
    }

    public void setData(TokenData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
