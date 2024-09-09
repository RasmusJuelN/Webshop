package com.example.webshop.api;

import com.example.webshop.models.Product;
import com.example.webshop.models.ProductDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductApi {
    @GET("products")
    Call<List<Product>> getProducts();

    @PUT("products/{id}")
    Call<ProductDto> updateProduct(
            @Path("id") int id,
            @Body ProductDto productDto
    );
}

