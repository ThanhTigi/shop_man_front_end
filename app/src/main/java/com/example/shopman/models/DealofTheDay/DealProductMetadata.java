package com.example.shopman.models.DealofTheDay;

import com.example.shopman.models.searchproducts.SearchProduct;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DealProductMetadata {
    @SerializedName("data")
    private List<SearchProduct> data;

    @SerializedName("total")
    private int total;

    @SerializedName("suggest")
    private List<String> suggest;

    @SerializedName("lastSortValues")
    private List<Object> lastSortValues;

    // Getters and setters
    public List<SearchProduct> getData() { return data; }
    public void setData(List<SearchProduct> data) { this.data = data; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public List<String> getSuggest() { return suggest; }
    public void setSuggest(List<String> suggest) { this.suggest = suggest; }
    public List<Object> getLastSortValues() { return lastSortValues; }
    public void setLastSortValues(List<Object> lastSortValues) { this.lastSortValues = lastSortValues; }
}
