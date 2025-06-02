package com.example.shopman.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CampaignResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    @SerializedName("metadata")
    private CampaignMetadata metadata;

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public CampaignMetadata getMetadata() {
        return metadata;
    }

    public static class CampaignMetadata {
        @SerializedName("message")
        private String message;

        @SerializedName("metadata")
        private CampaignDetail metadata;

        public String getMessage() {
            return message;
        }

        public CampaignDetail getMetadata() {
            return metadata;
        }
    }

    public static class CampaignDetail {
        @SerializedName("campaign")
        private Campaign campaign;

        @SerializedName("discount")
        private List<Discount> discounts;

        public Campaign getCampaign() {
            return campaign;
        }

        public List<Discount> getDiscounts() {
            return discounts;
        }
    }

    public static class Campaign {
        @SerializedName("id")
        private int id;

        @SerializedName("title")
        private String title;

        @SerializedName("slug")
        private String slug;

        @SerializedName("description")
        private String description;

        @SerializedName("start_time")
        private String startTime;

        @SerializedName("end_time")
        private String endTime;

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getSlug() {
            return slug;
        }

        public String getDescription() {
            return description;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getEndTime() {
            return endTime;
        }
    }

    public static class Discount {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("code")
        private String code;

        @SerializedName("value")
        private String value;

        @SerializedName("type")
        private String type;

        @SerializedName("StartDate")
        private String startDate;

        @SerializedName("EndDate")
        private String endDate;

        @SerializedName("MinValueOrders")
        private String minValueOrders;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }

        public String getType() {
            return type;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public String getMinValueOrders() {
            return minValueOrders;
        }
    }

    public static class ApiCategory {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("thumb")
        private String thumb;

        @SerializedName("desc")
        private String desc;

        @SerializedName("status")
        private String status;

        @SerializedName("slug")
        private String slug;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("updatedAt")
        private String updatedAt;

        @SerializedName("deletedAt")
        private String deletedAt;

        @SerializedName("ParentId")
        private String parentId;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getThumb() {
            return thumb;
        }

        public String getDesc() {
            return desc;
        }

        public String getStatus() {
            return status;
        }

        public String getSlug() {
            return slug;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public String getDeletedAt() {
            return deletedAt;
        }

        public String getParentId() {
            return parentId;
        }
    }

    public static class CategoryMetadata {
        @SerializedName("message")
        private String message;

        @SerializedName("metadata")
        private List<Category> metadata;

        public String getMessage() {
            return message;
        }

        public List<Category> getMetadata() {
            return metadata;
        }
    }

    public static class CategoryProductsMetadata {
        @SerializedName("message")
        private String message;

        @SerializedName("metadata")
        private ProductsData metadata;

        public String getMessage() {
            return message;
        }

        public ProductsData getMetadata() {
            return metadata;
        }

        public static class ProductsData {
            @SerializedName("data")
            private List<Product> data; // com.example.shopman.models.Product

            @SerializedName("total")
            private int total;

            @SerializedName("suggest")
            private List<Object> suggest;

            @SerializedName("lastSortValues")
            private List<Object> lastSortValues;

            public List<Product> getData() {
                return data;
            }

            public int getTotal() {
                return total;
            }

            public List<Object> getSuggest() {
                return suggest;
            }

            public List<Object> getLastSortValues() {
                return lastSortValues;
            }
        }
    }

    public static class CategoryProductsResponse {
        @SerializedName("message")
        private String message;

        @SerializedName("status")
        private int status;

        @SerializedName("metadata")
        private CategoryProductsMetadata metadata;

        public String getMessage() {
            return message;
        }

        public int getStatus() {
            return status;
        }

        public CategoryProductsMetadata getMetadata() {
            return metadata;
        }
    }

    public static class CategoryResponse {
        @SerializedName("message")
        private String message;

        @SerializedName("status")
        private int status;

        @SerializedName("metadata")
        private CategoryMetadata metadata;

        public String getMessage() {
            return message;
        }

        public int getStatus() {
            return status;
        }

        public CategoryMetadata getMetadata() {
            return metadata;
        }
    }

    public static class CheckOTPMetadata {
        private String message;
        private ResetTokenMetadata metadata;

        public ResetTokenMetadata getMetadata() {
            return metadata;
        }
    }

    public static class CheckOTPResponse {
        @SerializedName("message")
        private String message;

        @SerializedName("status")
        private int status;

        @SerializedName("metadata")
        private Metadata metadata;

        public String getMessage() {
            return message;
        }

        public int getStatus() {
            return status;
        }

        public Metadata getMetadata() {
            return metadata;
        }

        public static class Metadata {
            @SerializedName("message")
            private String message;

            @SerializedName("metadata")
            private InnerMetadata innerMetadata;

            public String getMessage() {
                return message;
            }

            public InnerMetadata getInnerMetadata() {
                return innerMetadata;
            }
        }

        public static class InnerMetadata {
            @SerializedName("resetToken")
            private String resetToken;

            public String getResetToken() {
                return resetToken;
            }
        }
    }

    public static class ResetTokenMetadata {

        private String resetToken;

        public void setResetToken(String resetToken) {
            this.resetToken = resetToken;
        }

        public String getResetToken() {
            return resetToken;
        }
    }
}