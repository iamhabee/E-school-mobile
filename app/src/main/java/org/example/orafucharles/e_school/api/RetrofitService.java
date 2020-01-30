package org.example.orafucharles.e_school.api;


import org.example.orafucharles.e_school.SchoolAdminLoginResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitService {

    @FormUrlEncoded
    @POST("auth/schoolLogin")
    Call<ResponseBody> schoolLogin(
            @Field("email") String email,
            @Field("password") String password
    );



}
