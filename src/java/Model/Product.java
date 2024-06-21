package Model;

public class Product {
    private int id;
    private String name;
    private String image;
    private int price;
    private String description;
    private int stock;
    private Category category;
    private Brand brand;

    public Product() {
    }

    public Product(int id, String name, String image, int price, String description, int stock, Category category, Brand brand) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.description = description;
        this.stock = stock;
        this.category = category;
        this.brand = brand;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    @Override
    public String toString() {
        return "Product{" + "id=" + id + ", name=" + name + ", image=" + image + ", price=" + price + ", description=" + description + ", stock=" + stock + ", category=" + category + ", brand=" + brand + '}';
    }
   
}
