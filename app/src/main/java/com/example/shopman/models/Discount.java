package com.example.shopman.models;

import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class Discount implements Parcelable {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("desc")
    private String desc;

    @SerializedName("value")
    private String value;

    @SerializedName("type")
    private String type;

    @SerializedName("code")
    private String code;

    @SerializedName("StartDate")
    private String startDate;

    @SerializedName("EndDate")
    private String endDate;

    @SerializedName("MaxUses")
    private int maxUses;

    @SerializedName("UserCounts")
    private int userCounts;

    @SerializedName("MinValueOrders")
    private String minValueOrders;

    @SerializedName("status")
    private String status;

    @SerializedName("ShopId")
    private Integer shopId;

    @SerializedName("CampaignId")
    private Integer campaignId;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public int getMaxUses() { return maxUses; }
    public void setMaxUses(int maxUses) { this.maxUses = maxUses; }
    public int getUserCounts() { return userCounts; }
    public void setUserCounts(int userCounts) { this.userCounts = userCounts; }
    public String getMinValueOrders() { return minValueOrders; }
    public void setMinValueOrders(String minValueOrders) { this.minValueOrders = minValueOrders; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getShopId() { return shopId; }
    public void setShopId(Integer shopId) { this.shopId = shopId; }
    public Integer getCampaignId() { return campaignId; }
    public void setCampaignId(Integer campaignId) { this.campaignId = campaignId; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // Parcelable implementation
    protected Discount(android.os.Parcel in) {
        id = in.readInt();
        name = in.readString();
        desc = in.readString();
        value = in.readString();
        type = in.readString();
        code = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        maxUses = in.readInt();
        userCounts = in.readInt();
        minValueOrders = in.readString();
        status = in.readString();
        if (in.readByte() == 0) {
            shopId = null;
        } else {
            shopId = in.readInt();
        }
        if (in.readByte() == 0) {
            campaignId = null;
        } else {
            campaignId = in.readInt();
        }
        createdAt = in.readString();
        updatedAt = in.readString();
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(desc);
        dest.writeString(value);
        dest.writeString(type);
        dest.writeString(code);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeInt(maxUses);
        dest.writeInt(userCounts);
        dest.writeString(minValueOrders);
        dest.writeString(status);
        if (shopId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(shopId);
        }
        if (campaignId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(campaignId);
        }
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Discount> CREATOR = new Creator<Discount>() {
        @Override
        public Discount createFromParcel(android.os.Parcel in) {
            return new Discount(in);
        }

        @Override
        public Discount[] newArray(int size) {
            return new Discount[size];
        }
    };
}