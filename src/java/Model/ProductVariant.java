/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author ADMIN
 */
public class ProductVariant {

    private int id;
    private Product product;
    private Color color;
    private int stock;

    public ProductVariant() {
    }

    public ProductVariant(int id, Product product, Color color, int stock) {
        this.id = id;
        this.product = product;
        this.color = color;
        this.stock = stock;
    }

    public ProductVariant(int id, Color color, int stock) {
        this.id = id;
        this.color = color;
        this.stock = stock;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

}
