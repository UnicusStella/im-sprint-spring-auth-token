package com.codestates.seb.TokenServer.Domain.Refresh;

public class RefreshRes {

    private RefreshInfo data;
    private String message;

    public RefreshInfo getData() {
        return data;
    }

    public void setData(RefreshInfo data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
