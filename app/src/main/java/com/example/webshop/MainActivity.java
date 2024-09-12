package com.example.webshop;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webshop.adapters.ProductAdapter;
import com.example.webshop.api.ProductApi;
import com.example.webshop.models.CartItem;
import com.example.webshop.models.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int CART_REQUEST_CODE = 1;

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private ArrayList<Product> productList = new ArrayList<>();
    private ArrayList<CartItem> cartItems = new ArrayList<>();

    private int cartItemCount = 0;
    private TextView cartBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initUi();

        // Sending the cartItems to the CartActivity
        findViewById(R.id.cartIcon).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            intent.putExtra("cartItems", (Serializable) cartItems);
            startActivityForResult(intent, CART_REQUEST_CODE);
        });

        int spanCount = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 5 : 3;

        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));
        productAdapter = new ProductAdapter(productList, this);
        recyclerView.setAdapter(productAdapter);

        fetchProducts();
    }

    private void initUi() {
        recyclerView = findViewById(R.id.recyclerView);
        cartBadge = findViewById(R.id.cartBadge);
    }

    // Refresh product list when returning to MainActivity
    @Override
    protected void onResume() {
        super.onResume();
        fetchProducts();
    }

    //
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
                    // Handle unsuccessful responses (e.g., 404 or 500 errors)
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void updateCartBadge() {
        if (cartItemCount > 0) {
            cartBadge.setText(String.valueOf(cartItemCount));
            cartBadge.setVisibility(View.VISIBLE);
        } else {
            cartBadge.setVisibility(View.GONE);
        }
    }

    public void addItemToCart(Product product) {
        boolean found = false;
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct().getId() == product.getId()) {
                if (cartItem.getQuantity() < product.getStock()) {
                    cartItem.setQuantity(cartItem.getQuantity() + 1);
                    cartItemCount++;
                    found = true;
                } else {

                    return;
                }
                break;
            }
        }
        if (!found) {
            if (product.getStock() > 0) {
                cartItems.add(new CartItem(product, 1));
                cartItemCount++;
            } else {

                return;
            }
        }
        updateCartBadge();
    }

    public void recalculateCartItemCount() {
        cartItemCount = 0;
        for (CartItem cartItem : cartItems) {
            cartItemCount += cartItem.getQuantity();
        }
        updateCartBadge();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CART_REQUEST_CODE && resultCode == RESULT_OK) {
            cartItems = (ArrayList<CartItem>) data.getSerializableExtra("updatedCartItems");
            recalculateCartItemCount();
        }
    }
}