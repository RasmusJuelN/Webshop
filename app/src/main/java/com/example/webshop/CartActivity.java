package com.example.webshop;

import static com.example.webshop.R.id.cart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webshop.adapters.CartAdapter;
import com.example.webshop.api.ProductApi;
import com.example.webshop.models.CartItem;



import java.util.List;

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
            // Implement checkout logic here
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
        resultIntent.putExtra("updatedCartItemCount", totalItemCount);
        setResult(RESULT_OK, resultIntent);
        finish(); // Close activity and return result
    }
}