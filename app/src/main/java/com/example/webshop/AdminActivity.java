package com.example.webshop;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webshop.adapters.AdminProductAdapter;
import com.example.webshop.api.ProductApi;
import com.example.webshop.models.Product;
import com.example.webshop.models.ProductDto;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminActivity extends AppCompatActivity {

    private static final String TAG = "AdminActivity";

    private RecyclerView recyclerView;
    private AdminProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();

    private EditText productNameEditText;
    private EditText productPriceEditText;
    private EditText productStockEditText;
    private EditText productDescriptionEditText;
    private EditText productImageEditText;
    private Button addProductButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        recyclerView = findViewById(R.id.recyclerView);
        productNameEditText = findViewById(R.id.productName);
        productPriceEditText = findViewById(R.id.productPrice);
        productStockEditText = findViewById(R.id.productStock);
        productDescriptionEditText = findViewById(R.id.productDescription);
        productImageEditText = findViewById(R.id.productImage);
        addProductButton = findViewById(R.id.addProductButton);

        // Determine the number of columns based on the orientation
        int spanCount = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 3 : 5;
        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

        productAdapter = new AdminProductAdapter(productList, new AdminProductAdapter.OnProductActionListener() {
            @Override
            public void onEdit(Product product) {
                Toast.makeText(AdminActivity.this, "Hello edit", Toast.LENGTH_SHORT).show();
                editProduct(product);
            }

            @Override
            public void onRemove(Product product) {
                Toast.makeText(AdminActivity.this, "Hello remove", Toast.LENGTH_SHORT).show();
                // Handle remove product
                removeProduct(product);
            }
        });
        recyclerView.setAdapter(productAdapter);

        addProductButton.setOnClickListener(v -> addProduct());

        fetchProducts();
    }

    private void fetchProducts() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.32.85:8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ProductApi productApi = retrofit.create(ProductApi.class);
        productApi.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    productAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "API call failed with response code: " + response.code() + ", message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
            }
        });
    }

    private void addProduct() {
        String name = productNameEditText.getText().toString();
        String priceStr = productPriceEditText.getText().toString();
        String stockStr = productStockEditText.getText().toString();
        String description = productDescriptionEditText.getText().toString();
        String imageBase64 = productImageEditText.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(stockStr) || TextUtils.isEmpty(description) || TextUtils.isEmpty(imageBase64)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int stock = Integer.parseInt(stockStr);

        // Prepare image as MultipartBody.Part
        RequestBody requestFile = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                Base64.decode(imageBase64, Base64.DEFAULT)
        );
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);

        // Prepare other fields as RequestBody
        RequestBody nameBody = RequestBody.create(MediaType.parse("multipart/form-data"), name);
        RequestBody priceBody = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(price));
        RequestBody stockBody = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(stock));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("multipart/form-data"), description);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.32.85:8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ProductApi productApi = retrofit.create(ProductApi.class);
        productApi.addProduct(nameBody, priceBody, stockBody, descriptionBody, imagePart).enqueue(new Callback<ProductDto>() {
            @Override
            public void onResponse(Call<ProductDto> call, Response<ProductDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                    fetchProducts();
                } else {
                    Toast.makeText(AdminActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductDto> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editProduct(Product product) {
        // Implement edit product logic
    }

    private void removeProduct(Product product) {
        // Implement remove product logic
    }
}