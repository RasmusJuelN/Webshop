package com.example.webshop.api;

import com.example.webshop.models.User;
import com.example.webshop.models.UserDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApi {
    @POST("users/login")
    Call<String> login(@Body UserDto userDto);

    @POST("users")
    Call<User> createUser(@Body UserDto userDto);

    @GET("users/{id}")
    Call<User> getUser(@Path("id") int id);

    @GET("users")
    Call<List<User>> getUsers();

    @PUT("users/{id}")
    Call<User> updateUser(@Path("id") int id, @Body UserDto userDto);

    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") int id);
}
