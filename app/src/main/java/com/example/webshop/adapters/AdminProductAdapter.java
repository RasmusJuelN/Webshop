package com.example.webshop.adapters;

import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webshop.R;
import com.example.webshop.models.Product;

import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private OnProductActionListener listener;


    public interface OnProductActionListener {
        void onEdit(Product product);
        void onRemove(Product product);
    }

    public AdminProductAdapter(List<Product> productList, OnProductActionListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.valueOf("DKK " + product.getPrice()));
        holder.productStock.setText(String.valueOf(product.getStock() + " in stock"));

        if (product.getImageBase64() != null && !product.getImageBase64().isEmpty()) {
            byte[] decodedString = Base64.decode(product.getImageBase64(), Base64.DEFAULT);
            holder.productImage.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
        } else {
            holder.productImage.setImageResource(R.drawable.baseline_image_not_supported_24); // Placeholder image
        }

        holder.editButton.setOnClickListener(v -> listener.onEdit(product));

        holder.removeButton.setOnClickListener(v -> listener.onRemove(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productStock;
        ImageView productImage;
        ImageButton editButton, removeButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productStock = itemView.findViewById(R.id.productStock);
            productImage = itemView.findViewById(R.id.productImage);
            editButton = itemView.findViewById(R.id.editButton);
            removeButton = itemView.findViewById(R.id.removeButton);
        }

//        public Button getEditButton() {
//            return editButton;
//        }
//
//        public Button getRemoveButton() {
//            return removeButton;
//        }
    }
}