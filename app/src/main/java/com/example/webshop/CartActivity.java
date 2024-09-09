package com.example.webshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webshop.adapters.CartAdapter;
import com.example.webshop.api.ProductApi;
import com.example.webshop.models.CartItem;
import com.example.webshop.models.Product;
import com.example.webshop.models.ProductDto;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CartActivity extends AppCompatActivity {

    private List<CartItem> cartItems;
    private TextView totalPriceTextView;
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private Button checkoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        totalPriceTextView = findViewById(R.id.totalPrice);
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        checkoutButton = findViewById(R.id.checkoutButton);

        cartItems = (List<CartItem>) getIntent().getSerializableExtra("cartItems");

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cartItems, this::handleRemove);
        cartRecyclerView.setAdapter(cartAdapter);

        calculateTotalPrice();

        checkoutButton.setOnClickListener(v -> {
            handleCheckout();
        });
    }

    private void calculateTotalPrice() {
        double totalPrice = 0;
        for (CartItem cartItem : cartItems) {
            totalPrice += cartItem.getProduct().getPrice() * cartItem.getQuantity();
        }
        totalPriceTextView.setText("Total Price: DKK " + totalPrice);
    }

    private void handleRemove(CartItem cartItem) {
        cartItems.remove(cartItem);
        cartAdapter.notifyDataSetChanged();
        calculateTotalPrice();
        updateCartItemCount();
    }

    private void updateCartItemCount() {
        int totalItemCount = 0;
        for (CartItem item : cartItems) {
            totalItemCount += item.getQuantity();
        }
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedCartItems", (Serializable) cartItems);
        setResult(RESULT_OK, resultIntent);
        if(totalItemCount < 1) {
            finish();
        }
    }
    private void handleCheckout() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.32.85:8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ProductApi productApi = retrofit.create(ProductApi.class);

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int newStock = product.getStock() - cartItem.getQuantity();
            product.setStock(newStock);

            // Convert Product to ProductDto
            ProductDto productDto = new ProductDto(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getStock(),
                    product.getDescription(),
                    product.getImageBase64()
            );

            productApi.updateProduct(product.getId(), productDto).enqueue(new Callback<ProductDto>() {
                @Override
                public void onResponse(Call<ProductDto> call, Response<ProductDto> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(CartActivity.this, "Checkout successful", Toast.LENGTH_SHORT).show();
                        // Navigate back to MainActivity
                        Intent intent = new Intent(CartActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(CartActivity.this, "Failed to update product stock", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ProductDto> call, Throwable t) {
                    Toast.makeText(CartActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}