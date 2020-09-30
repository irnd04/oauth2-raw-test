package com.example.oauth2rawtest.oauth2;

public class CodeResponse {
    public String code;
    public String scope;
    public String authuser;
    public String prompt;

    public void setCode(String code) {
        this.code = code;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setAuthuser(String authuser) {
        this.authuser = authuser;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getCode() {
        return code;
    }

    public String getScope() {
        return scope;
    }

    public String getAuthuser() {
        return authuser;
    }

    public String getPrompt() {
        return prompt;
    }
}
