package com.example.shopman.models.searchproducts;

import java.util.List;

public class SearchProductsData {
    private List<SearchProduct> data;
    private int total;
    private List<String> suggest;
    private List<Object> lastSortValues; // Có thể dùng List<String> nếu server trả về cố định [score, id]

    public List<SearchProduct> getData() {
        return data;
    }

    public void setData(List<SearchProduct> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<String> getSuggest() {
        return suggest;
    }

    public void setSuggest(List<String> suggest) {
        this.suggest = suggest;
    }

    public List<Object> getLastSortValues() {
        return lastSortValues;
    }

    public void setLastSortValues(List<Object> lastSortValues) {
        this.lastSortValues = lastSortValues;
    }
}