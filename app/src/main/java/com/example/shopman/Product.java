package com.example.shopman;

import com.example.shopman.models.ProductDetails.SpuToSku;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Product implements Serializable {
    private String id; // Định danh sản phẩm
    private String name; // Tên sản phẩm
    private String desc; // Mô tả ngắn
    private String desc_plain; // Mô tả dạng text
    private String price; // Giá (String để tương thích với chi tiết sản phẩm)
    private String thumb; // URL hình ảnh
    private float rating; // Điểm đánh giá
    private int discount_percentage; // Phần trăm giảm giá
    private String slug; // Slug để điều hướng
    private int CategoryId; // ID danh mục
    private List<Integer> CategoryPath; // Đường dẫn danh mục
    private int ShopId; // ID cửa hàng
    private int sale_count; // Số lượng đã bán
    private Map<String, Object> attrs; // Thuộc tính (sizes, colors, style, material)
    private List<SpuToSku> SpuToSkus; // Danh sách biến thể
    private boolean has_variations; // Có biến thể hay không

    // Constructor
    public Product() {}

    public Product(String id, String name, String desc, String desc_plain, String price, String thumb,
                   float rating, int discount_percentage, String slug, int CategoryId, List<Integer> CategoryPath,
                   int ShopId, int sale_count, Map<String, Object> attrs, List<SpuToSku> SpuToSkus, boolean has_variations) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.desc_plain = desc_plain;
        this.price = price;
        this.thumb = thumb;
        this.rating = rating;
        this.discount_percentage = discount_percentage;
        this.slug = slug;
        this.CategoryId = CategoryId;
        this.CategoryPath = CategoryPath;
        this.ShopId = ShopId;
        this.sale_count = sale_count;
        this.attrs = attrs;
        this.SpuToSkus = SpuToSkus;
        this.has_variations = has_variations;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }

    public String getDesc_plain() { return desc_plain; }
    public void setDesc_plain(String desc_plain) { this.desc_plain = desc_plain; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getThumb() { return thumb; }
    public void setThumb(String thumb) { this.thumb = thumb; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public int getDiscount_percentage() { return discount_percentage; }
    public void setDiscount_percentage(int discount_percentage) { this.discount_percentage = discount_percentage; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public int getCategoryId() { return CategoryId; }
    public void setCategoryId(int categoryId) { CategoryId = categoryId; }

    public List<Integer> getCategoryPath() { return CategoryPath; }
    public void setCategoryPath(List<Integer> categoryPath) { CategoryPath = categoryPath; }

    public int getShopId() { return ShopId; }
    public void setShopId(int shopId) { ShopId = shopId; }

    public int getSale_count() { return sale_count; }
    public void setSale_count(int sale_count) { this.sale_count = sale_count; }

    public Map<String, Object> getAttrs() { return attrs; }
    public void setAttrs(Map<String, Object> attrs) { this.attrs = attrs; }

    public List<SpuToSku> getSpuToSkus() { return SpuToSkus; }
    public void setSpuToSkus(List<SpuToSku> spuToSkus) { SpuToSkus = spuToSkus; }

    public boolean isHas_variations() { return has_variations; }
    public void setHas_variations(boolean has_variations) { this.has_variations = has_variations; }
}