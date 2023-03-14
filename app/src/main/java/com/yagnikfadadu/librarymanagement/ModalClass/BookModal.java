package com.yagnikfadadu.librarymanagement.ModalClass;

public class BookModal {
    double price;
    int id;
    int quantity;
    int available;
    int totalIssues;
    int totalRatedUser;
    double rating;
    String name;
    String field;
    String author;
    String coverPhoto;
    String publication;
    String description;

    public BookModal() {

    }

    public double getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getTotalIssues() {
        return totalIssues;
    }

    public void setTotalIssues(int totalIssues) {
        this.totalIssues = totalIssues;
    }

    public int getTotalRatedUser() {
        return totalRatedUser;
    }

    public void setTotalRatedUser(int totalRatedUser) {
        this.totalRatedUser = totalRatedUser;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getPublication() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BookModal(float price, int quantity, String name, String author, String coverPhoto, String field, String publication, String description){
        this.name = name;
        this.price = price;
        this.field = field;
        this.rating = 0;
        this.quantity = quantity;
        this.totalIssues = 0;
        this.totalRatedUser = 0;
        this.author = author;
        this.available = quantity;
        this.coverPhoto = coverPhoto;
        this.publication = publication;
        this.description = description;
        // To Do : add comment class in MongoDB
    }
}
