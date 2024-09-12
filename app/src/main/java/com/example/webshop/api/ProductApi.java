package com.example.webshop.api;

import com.example.webshop.models.Product;
import com.example.webshop.models.ProductDto;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductApi {
    @GET("products")
    Call<List<Product>> getProducts();

    @Multipart
    @PUT("products/{id}")
    Call<ProductDto> updateProduct(
            @Path("id") int id,
            @Part("name") RequestBody name,
            @Part("price") RequestBody price,
            @Part("stock") RequestBody stock,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part image
    );

    @Multipart
    @POST("products")
    Call<ProductDto> addProduct(
            @Part("name") RequestBody name,
            @Part("price") RequestBody price,
            @Part("stock") RequestBody stock,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part image
    );

    @DELETE("products/{id}")
    Call<Void> deleteProduct(@Path("id") int id);
}

