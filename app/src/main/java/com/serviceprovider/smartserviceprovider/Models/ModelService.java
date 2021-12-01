package com.serviceprovider.smartserviceprovider.Models;



public class ModelService {
    private String sellerName,productId,serviceDescription,serviceCategory,serviceQuantity,serviceIcon
            ,originalPrice,discountPrice,discountNote,discountAvailable,timeTamp,uid;


    public ModelService() {

    }

    public ModelService(String sellerName, String productId, String serviceDescription, String serviceCategory,
                        String serviceQuantity, String serviceIcon,
                        String originalPrice, String discountPrice,
                        String discountNote, String discountAvailable,
                        String timeTamp, String uid)
    {
        this.sellerName = sellerName;
        this.productId = productId;
        this.serviceDescription = serviceDescription;
        this.serviceCategory = serviceCategory;
        this.serviceQuantity = serviceQuantity;
        this.serviceIcon = serviceIcon;
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
        this.discountNote = discountNote;
        this.discountAvailable = discountAvailable;
        this.timeTamp = timeTamp;
        this.uid = uid;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public String getServiceQuantity() {
        return serviceQuantity;
    }

    public void setServiceQuantity(String serviceQuantity) {
        this.serviceQuantity = serviceQuantity;
    }

    public String getServiceIcon() {
        return serviceIcon;
    }

    public void setServiceIcon(String serviceIcon) {
        this.serviceIcon = serviceIcon;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String   getDiscountNote() {
        return discountNote;
    }

    public void setDiscountNote(String discountNote) {
        this.discountNote = discountNote;
    }

    public String getDiscountAvailable() {
        return discountAvailable;
    }

    public void setDiscountAvailable(String discountAvailable) {
        this.discountAvailable = discountAvailable;
    }

    public String getTimeTamp() {
        return timeTamp;
    }

    public void setTimeTamp(String timeTamp) {
        this.timeTamp = timeTamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
