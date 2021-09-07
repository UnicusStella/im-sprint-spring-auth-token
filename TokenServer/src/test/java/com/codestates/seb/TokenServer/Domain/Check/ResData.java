package com.codestates.seb.TokenServer.Domain.Check;

public class ResData {

    private InfoData data;
    private String message;

    public InfoData getData() {
        return data;
    }

    public void setData(InfoData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
