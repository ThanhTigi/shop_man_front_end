package com.example.shopman.fragments.home;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopman.Product;
import com.example.shopman.ProductAdapter;
import com.example.shopman.R;
import com.example.shopman.utilitis.ProductsConst;

import java.util.ArrayList;
import java.util.List;

public class CategoryProductActivity extends AppCompatActivity {

    private TextView tvCategoryTitle;
    private RecyclerView rvProducts;
    private ImageView ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_product);

        ivBack = findViewById(R.id.ivBack);
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        rvProducts = findViewById(R.id.categoryRecyclerView);

        ivBack.setOnClickListener(v -> finish());

        String category = getIntent().getStringExtra("categoryName");
        if (category == null) {
            category = "Unknown";
            Toast.makeText(this, "Category not found", Toast.LENGTH_SHORT).show();
        }
        tvCategoryTitle.setText(category + " Products");

        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        List<Product> products = getSampleProducts(category);
        ProductAdapter adapter = new ProductAdapter(products);
        rvProducts.setAdapter(adapter);
    }

    private List<Product> getSampleProducts(String category) {
        List<Product> products = new ArrayList<>();

        switch (category.toLowerCase()) {
            case "beauty":
                products.add(ProductsConst.totalProducts.get(0));
                products.add(ProductsConst.totalProducts.get(3));
                products.add(ProductsConst.totalProducts.get(4));
                products.add(ProductsConst.totalProducts.get(19));
                products.add(ProductsConst.totalProducts.get(21));
                break;
            case "fashion":
                products.add(ProductsConst.totalProducts.get(16));
                products.add(ProductsConst.totalProducts.get(17));
                products.add(ProductsConst.totalProducts.get(26));
                products.add(ProductsConst.totalProducts.get(27));
                products.add(ProductsConst.totalProducts.get(29));
                products.add(ProductsConst.totalProducts.get(30));
                products.add(ProductsConst.totalProducts.get(31));
                products.add(ProductsConst.totalProducts.get(43));
                products.add(ProductsConst.totalProducts.get(45));
                products.add(ProductsConst.totalProducts.get(49));
                break;
            case "kids":
                products.add(ProductsConst.totalProducts.get(6));
                products.add(ProductsConst.totalProducts.get(8));
                products.add(ProductsConst.totalProducts.get(18));
                products.add(ProductsConst.totalProducts.get(22));
                products.add(ProductsConst.totalProducts.get(35));
                products.add(ProductsConst.totalProducts.get(37));
                products.add(ProductsConst.totalProducts.get(42));
                products.add(ProductsConst.totalProducts.get(47));
                break;
            case "men":
                products.add(ProductsConst.totalProducts.get(9));
                products.add(ProductsConst.totalProducts.get(10));
                products.add(ProductsConst.totalProducts.get(12));
                products.add(ProductsConst.totalProducts.get(39));
                products.add(ProductsConst.totalProducts.get(41));
                products.add(ProductsConst.totalProducts.get(48));
                break;
            case "women":
                products.add(ProductsConst.totalProducts.get(13));
                products.add(ProductsConst.totalProducts.get(44));
                break;
            default:
                products.add(ProductsConst.totalProducts.get(1));
                products.add(ProductsConst.totalProducts.get(2));
                products.add(ProductsConst.totalProducts.get(5));
                products.add(ProductsConst.totalProducts.get(7));
                products.add(ProductsConst.totalProducts.get(11));
                products.add(ProductsConst.totalProducts.get(14));
                products.add(ProductsConst.totalProducts.get(15));
                products.add(ProductsConst.totalProducts.get(20));
                products.add(ProductsConst.totalProducts.get(24));
                products.add(ProductsConst.totalProducts.get(25));
                products.add(ProductsConst.totalProducts.get(28));
                products.add(ProductsConst.totalProducts.get(32));
                products.add(ProductsConst.totalProducts.get(33));
                products.add(ProductsConst.totalProducts.get(34));
                products.add(ProductsConst.totalProducts.get(36));
                products.add(ProductsConst.totalProducts.get(38));
                products.add(ProductsConst.totalProducts.get(46));
                break;
        }
        return products;
    }
}