package com.mayank.punjabidelight.Model;

public class CartProducts {

    private String pid,pname,price,quantity,discount,image,producttotal;

    public CartProducts() {
    }

    public CartProducts(String pid, String pname, String price, String quantity, String discount, String image, String producttotal) {
        this.pid = pid;
        this.pname = pname;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
        this.image = image;
        this.producttotal = producttotal;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProducttotal() {
        return producttotal;
    }

    public void setProducttotal(String producttotal) {
        this.producttotal = producttotal;
    }
}
