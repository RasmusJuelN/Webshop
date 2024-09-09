package com.example.webshop.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webshop.R;
import com.example.webshop.models.CartItem;
import com.example.webshop.models.Product;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private OnRemoveClickListener onRemoveClickListener;

    public CartAdapter(List<CartItem> cartItems, OnRemoveClickListener onRemoveClickListener) {
        this.cartItems = cartItems;
        this.onRemoveClickListener = onRemoveClickListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.cartItemPrice.setText("DKK " + cartItem.getProduct().getPrice());
        holder.cartItemQuantity.setText("Quantity: " + cartItem.getQuantity());
        holder.bind(cartItem.getProduct());

        holder.removeButton.setOnClickListener(v -> {
            if (onRemoveClickListener != null) {
                onRemoveClickListener.onRemoveClick(cartItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView cartItemImage;
        TextView cartItemName, cartItemPrice, cartItemQuantity;
        Button removeButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cartItemImage = itemView.findViewById(R.id.cartItemImage);
            cartItemName = itemView.findViewById(R.id.cartItemName);
            cartItemPrice = itemView.findViewById(R.id.cartItemPrice);
            cartItemQuantity = itemView.findViewById(R.id.cartItemQuantity);
            removeButton = itemView.findViewById(R.id.cartItemRemoveButton);
        }

        public void bind(Product product) {
            cartItemName.setText(product.getName());

            // Load image from Base64 string
            String base64Image = product.getImageBase64();
            if (base64Image != null && !base64Image.isEmpty()) {
                byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                cartItemImage.setImageBitmap(bitmap);
            } else {
                cartItemImage.setImageResource(R.drawable.baseline_image_not_supported_24);
            }
        }
    }

    // Interface for handling remove button clicks
    public interface OnRemoveClickListener {
        void onRemoveClick(CartItem cartItem);
    }
}

