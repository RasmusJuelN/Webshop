package com.example.webshop.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.webshop.MainActivity;
import com.example.webshop.R;
import com.example.webshop.models.Product;

public class ProductDetailDialogFragment extends DialogFragment {

    private static final String ARG_PRODUCT = "product";
    private Product product;

    public static ProductDetailDialogFragment newInstance(Product product) {
        ProductDetailDialogFragment fragment = new ProductDetailDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT, product);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_detail_dialog, container, false);

        if (getArguments() != null) {
            product = (Product) getArguments().getSerializable(ARG_PRODUCT);
        }

        TextView productName = view.findViewById(R.id.productName);
        TextView productPrice = view.findViewById(R.id.productPrice);
        TextView productDescription = view.findViewById(R.id.productDescription);
        ImageView productImage = view.findViewById(R.id.productImage);
        TextView productStock = view.findViewById(R.id.productStock);
        Button addToCartButton = view.findViewById(R.id.addToCartButton);

        productName.setText(product.getName());
        productPrice.setText(String.valueOf("DKK " + product.getPrice()));
        productDescription.setText(product.getDescription());
        if (product.getStock() > 0) {
            productStock.setText(product.getStock() + " in stock");
            productStock.setTextColor(view.getContext().getColor(R.color.green));
            addToCartButton.setEnabled(true);  // Enable the button if in stock
        } else {
            productStock.setText("Out of Stock");
            productStock.setTextColor(view.getContext().getColor(R.color.red));
            addToCartButton.setEnabled(false);  // Disable the button if out of stock
        }

        String base64Image = product.getImageBase64();
        if (base64Image != null && !base64Image.isEmpty()) {
            byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            productImage.setImageBitmap(bitmap);
        } else {
            productImage.setImageResource(R.drawable.baseline_image_not_supported_24);  // Placeholder If no image
        }

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = getResources().getDisplayMetrics().widthPixels;
            int dialogWidth = (int) (width * 0.7); // Set width to 70% of screen

            getDialog().getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // Add product to cart and call MainActivity's addItemToCart method to increment cartItemCount
        // Toast a message to the user
        // Dismiss the dialog
        Button addToCartButton = getDialog().findViewById(R.id.addToCartButton);
        addToCartButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Added " + product.getName() + " to cart.", Toast.LENGTH_SHORT).show();
            ((MainActivity) getActivity()).addItemToCart(product);
            dismiss();
        });
    }
}
