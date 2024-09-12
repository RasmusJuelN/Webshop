package com.example.webshop;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webshop.adapters.AdminProductAdapter;
import com.example.webshop.api.ProductApi;
import com.example.webshop.models.Product;
import com.example.webshop.models.ProductDto;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
    private static final int PICK_IMAGE_REQUEST = 1;
    private String imageBase64;

    private RecyclerView recyclerView;
    private AdminProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private OnImagePickedListener imagePickedListener;
    private EditText productNameEditText;
    private EditText productPriceEditText;
    private EditText productStockEditText;
    private EditText productDescriptionEditText;
    private Button addProductButton;
    private Button chooseImageButton;
    private ImageView imagePreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize UI components
        initUi();

        // Set up button listeners
        chooseImageButton.setOnClickListener(v -> openImagePicker(bitmap -> imagePreview.setImageBitmap(bitmap)));
        addProductButton.setOnClickListener(v -> addProduct());

        // Set up RecyclerView
        int spanCount = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 3 : 5;
        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

        // Initialize the adapter
        productAdapter = new AdminProductAdapter(productList, new AdminProductAdapter.OnProductActionListener() {
            @Override
            public void onEdit(Product product) {
                showEditProductDialog(product);
            }

            @Override
            public void onRemove(Product product) {
                showDeleteConfirmationDialog(product);
            }
        }, this);
        recyclerView.setAdapter(productAdapter);

        // Fetch products from API
        fetchProducts();
    }

    private void initUi() {
        recyclerView = findViewById(R.id.recyclerView);
        productNameEditText = findViewById(R.id.productName);
        productPriceEditText = findViewById(R.id.productPrice);
        productStockEditText = findViewById(R.id.productStock);
        productDescriptionEditText = findViewById(R.id.productDescription);
        chooseImageButton = findViewById(R.id.chooseImageButton);
        addProductButton = findViewById(R.id.addProductButton);
        imagePreview = findViewById(R.id.imagePreview);
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

    private void showDeleteConfirmationDialog(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete this product?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteProduct(product))
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void deleteProduct(Product product) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.32.85:8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ProductApi productApi = retrofit.create(ProductApi.class);
        productApi.deleteProduct(product.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminActivity.this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                    fetchProducts(); // Refresh the product list
                } else {
                    Toast.makeText(AdminActivity.this, "Failed to delete product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImagePicker(OnImagePickedListener listener) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
        this.imagePickedListener = listener;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageBase64 = encodeImageToBase64(bitmap);
                if (imagePickedListener != null) {
                    imagePickedListener.onImagePicked(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private interface OnImagePickedListener {
        void onImagePicked(Bitmap bitmap);
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void addProduct() {
        String name = productNameEditText.getText().toString();
        String priceStr = productPriceEditText.getText().toString();
        String stockStr = productStockEditText.getText().toString();
        String description = productDescriptionEditText.getText().toString();

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
                    clearInputFields();
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

    // Clears the input fields after adding a product
    private void clearInputFields() {
        productNameEditText.setText("");
        productPriceEditText.setText("");
        productStockEditText.setText("");
        productDescriptionEditText.setText("");
        imageBase64 = null;
        imagePreview.setImageResource(0);
        imagePreview.setVisibility(View.GONE);
    }

    // Shows dialog to edit a product
    public void showEditProductDialog(Product product) {
        // Initializing the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_product, null);
        builder.setView(dialogView);

        // Initializing the dialog UI components
        EditText productNameEditText = dialogView.findViewById(R.id.productName);
        EditText productPriceEditText = dialogView.findViewById(R.id.productPrice);
        EditText productStockEditText = dialogView.findViewById(R.id.productStock);
        EditText productDescriptionEditText = dialogView.findViewById(R.id.productDescription);
        ImageView productImageView = dialogView.findViewById(R.id.productImage);
        Button chooseImageButton = dialogView.findViewById(R.id.chooseImageButton);
        Button saveButton = dialogView.findViewById(R.id.saveButton);

        // Set product details in dialog
        productNameEditText.setText(product.getName());
        productPriceEditText.setText(String.valueOf(product.getPrice()));
        productStockEditText.setText(String.valueOf(product.getStock()));
        productDescriptionEditText.setText(product.getDescription());

        // Reset imageBase64 to the current product's image
        imageBase64 = product.getImageBase64();

        // Load image from Base64 string
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            byte[] imageBytes = Base64.decode(imageBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            productImageView.setImageBitmap(bitmap);
        } else {
            productImageView.setImageResource(R.drawable.baseline_image_not_supported_24);
        }

        chooseImageButton.setOnClickListener(v -> openImagePicker(bitmap -> {
            productImageView.setImageBitmap(bitmap);
            imageBase64 = encodeImageToBase64(bitmap);
        }));

        // Building the dialog and showing it on the screen
        AlertDialog dialog = builder.create();
        dialog.show();

        // Save button calling the updateProduct method with new product details
        saveButton.setOnClickListener(v -> {
            String name = productNameEditText.getText().toString();
            String priceStr = productPriceEditText.getText().toString();
            String stockStr = productStockEditText.getText().toString();
            String description = productDescriptionEditText.getText().toString();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(stockStr) || TextUtils.isEmpty(description)) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);

            product.setName(name);
            product.setPrice(price);
            product.setStock(stock);
            product.setDescription(description);
            product.setImageBase64(imageBase64); // Update the product's image

            // Calls the updateProduct method that handles the api response
            updateProduct(product, dialog);
        });


    }


    private void updateProduct(Product product, AlertDialog dialog) {
        // Retrofit instance made to make API calls
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.32.85:8080/api/")
                .addConverterFactory(GsonConverterFactory.create()) // GsonConverterFactory is used to convert the JSON responses from the API into Java objects (like ProductDto).
                .build();

        ProductApi productApi = retrofit.create(ProductApi.class);

        RequestBody nameBody = RequestBody.create(MediaType.parse("multipart/form-data"), product.getName());
        RequestBody priceBody = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(product.getPrice()));
        RequestBody stockBody = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(product.getStock()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("multipart/form-data"), product.getDescription());

        MultipartBody.Part imagePart = null;
        // If imageBase64 is not null then create a MultipartBody.Part object
        if (imageBase64 != null) {
            RequestBody requestFile = RequestBody.create(
                    MediaType.parse("multipart/form-data"),
                    Base64.decode(imageBase64, Base64.DEFAULT)
            );
            imagePart = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);
        }

        // Calls the updateProduct method from the ProductApi interface
        // Using Retrofit object containing the base URL and GsonConverterFactory
        // The method is called with the product's id, name, price, stock, description, and imagePart
        productApi.updateProduct(product.getId(), nameBody, priceBody, stockBody, descriptionBody, imagePart).enqueue(new Callback<ProductDto>() {
            @Override
            public void onResponse(Call<ProductDto> call, Response<ProductDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                    fetchProducts();
                    dialog.dismiss();
                } else {
                    Toast.makeText(AdminActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductDto> call, Throwable t) {
                Toast.makeText(AdminActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}