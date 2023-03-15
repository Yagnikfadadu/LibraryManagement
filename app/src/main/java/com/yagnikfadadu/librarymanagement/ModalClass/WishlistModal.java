package com.yagnikfadadu.librarymanagement.ModalClass;

public class WishlistModal {
    String id;
    String enrollment;
    String wishListBookID;
    String wishListBookName;
    String WishListAuthorName;
    String wishListurl;

    int available;

    public void setAvailable(int available) {
        this.available = available;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment;
    }

    public void setWishListBookID(String wishListBookID) {
        this.wishListBookID = wishListBookID;
    }

    public void setWishListBookName(String wishListBookName) {
        this.wishListBookName = wishListBookName;
    }

    public void setWishListAuthorName(String wishListAuthorName) {
        WishListAuthorName = wishListAuthorName;
    }

    public void setWishListurl(String wishListurl) {
        this.wishListurl = wishListurl;
    }

    public String getId() {
        return id;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public String getWishListBookID() {
        return wishListBookID;
    }

    public String getWishListBookName() {
        return wishListBookName;
    }

    public String getWishListAuthorName() {
        return WishListAuthorName;
    }

    public String getWishListurl() {
        return wishListurl;
    }

    public int getAvailable() {
        return available;
    }
}