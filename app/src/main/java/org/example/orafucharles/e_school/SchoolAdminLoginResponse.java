package org.example.orafucharles.e_school;

public class SchoolAdminLoginResponse {

    private String access_token;
    private String message;
    private String user;
    private String token_type;


    public SchoolAdminLoginResponse(String access_token, String message, String user, String token_type) {
        this.access_token = access_token;
        this.message = message;
        this.user = user;
        this.token_type = token_type;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getMessage() {
        return message;
    }

    public String getUser() {
        return user;
    }

    public String getToken_type() {
        return token_type;
    }
}
