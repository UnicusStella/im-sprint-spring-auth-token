package com.codestates.seb.TokenServer.Domain.Refresh;

public class RefreshInfo {

    private RefreshUser userInfo;
    private String accessToken;

    public RefreshUser getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(RefreshUser userInfo) {
        this.userInfo = userInfo;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
