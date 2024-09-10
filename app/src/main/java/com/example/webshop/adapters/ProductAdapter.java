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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webshop.MainActivity;
import com.example.webshop.fragments.ProductDetailDialogFragment;
import com.example.webshop.R;
import com.example.webshop.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private static FragmentActivity activity;

    public ProductAdapter(List<Product> productList, FragmentActivity activity) {
        this.productList = productList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);

        // Set an OnClickListener to open the dialog
        holder.itemView.setOnClickListener(v -> {
            ProductDetailDialogFragment dialog = ProductDetailDialogFragment.newInstance(product);
            dialog.show(activity.getSupportFragmentManager(), "ProductDetailDialog");
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productDescription, productStock;
        ImageView productImage;
        Button addToCartButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);

            productStock = itemView.findViewById(R.id.productStock);
            productImage = itemView.findViewById(R.id.productImage);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }

        public void bind(Product product) {
            productName.setText(product.getName());
            productPrice.setText(String.valueOf("DKK " + product.getPrice()));


            if (product.getStock() > 0) {
                productStock.setText("In stock");
                productStock.setTextColor(itemView.getContext().getColor(R.color.dark_green));
                addToCartButton.setEnabled(true);  // Enable button
            } else {
                productStock.setText("Out of stock");
                productStock.setTextColor(itemView.getContext().getColor(R.color.red));
                addToCartButton.setEnabled(false);  // Disable button
            }


            addToCartButton.setOnClickListener(v -> {
                ((MainActivity) activity).addItemToCart(product);
                Toast.makeText(itemView.getContext(), "Added " + product.getName() + " to cart", Toast.LENGTH_SHORT).show();

            });

            // Load image from Base64 string
            String base64Image = product.getImageBase64();
            if (base64Image != null && !base64Image.isEmpty()) {
                byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                productImage.setImageBitmap(bitmap);
            } else {
                productImage.setImageResource(R.drawable.baseline_image_not_supported_24);
            }
        }
    }
}