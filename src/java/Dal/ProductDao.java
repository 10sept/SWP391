/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import Model.Brand;
import Model.Category;
import Model.Product;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class ProductDao extends DBContext {

    PreparedStatement ps;

    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "select * from Product";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setImage(rs.getString("image"));
                p.setPrice(rs.getInt("price"));
                p.setDescription(rs.getString("description"));
                p.setStock(rs.getInt("stock"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return list;

    }

    public List<Product> getAllProductDetail() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.id AS product_id, p.name AS product_name, p.image AS product_image, p.price AS product_price, p.description AS product_description, p.stock AS product_stock, "
                + "b.bid AS brand_id, b.name AS brand_name, "
                + "c.cid AS category_id, c.name AS category_name "
                + "FROM Product p "
                + "JOIN Brand b ON p.bid = b.bid "
                + "JOIN Category c ON p.cid = c.cid";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Brand brand = new Brand(rs.getInt("brand_id"), rs.getString("brand_name"));
                Category category = new Category(rs.getInt("category_id"), rs.getString("category_name"));

                Product p = new Product();
                p.setId(rs.getInt("product_id"));
                p.setName(rs.getString("product_name"));
                p.setImage(rs.getString("product_image"));
                p.setPrice(rs.getInt("product_price"));
                p.setDescription(rs.getString("product_description"));
                p.setStock(rs.getInt("product_stock"));
                p.setBrand(brand);
                p.setCategory(category);
                list.add(p);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return list;
    }

    public ArrayList<Category> getCategory() {
        ArrayList<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Category";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Category(rs.getInt("cid"), rs.getString("name")));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return list;
    }

    public ArrayList<Brand> getBrand() {
        ArrayList<Brand> list = new ArrayList<>();
        String sql = "SELECT * FROM Brand";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Brand(rs.getInt("bid"), rs.getString("name")));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return list;
    }

    public void addProduct(Product product) {
        String sql = "INSERT INTO Product (cid, bid, name, image, price, description, stock) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, product.getCategory().getId());
            st.setInt(2, product.getBrand().getId());
            st.setString(3, product.getName());
            st.setString(4, product.getImage());
            st.setDouble(5, product.getPrice()); // Use setDouble for the 'real' type in SQL
            st.setString(6, product.getDescription());
            st.setInt(7, product.getStock());
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // Edit an existing product
    public void editProduct(Product product) {
        String sql = "UPDATE Product SET name = ?, image = ?, price = ?, description = ?, stock = ?, bid = ?, cid = ? WHERE id = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, product.getName());
            st.setString(2, product.getImage());
            st.setInt(3, product.getPrice());
            st.setString(4, product.getDescription());
            st.setInt(5, product.getStock());
            st.setInt(6, product.getBrand().getId());
            st.setInt(7, product.getCategory().getId());
            st.setInt(8, product.getId());
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // Delete a product by id
    public void deleteProduct(int productId) {
        String deleteFeedback = "DELETE FROM Feedback WHERE pid = ?";
        String deleteProductCart = "DELETE FROM ProductCart WHERE pid = (SELECT id FROM Product WHERE id = ?)";
        String deleteOrderDetail = "DELETE FROM OrderDetail WHERE pid = (SELECT id FROM Product WHERE id = ?)";
        String deleteCart = "DELETE FROM Cart WHERE id IN (SELECT cartId FROM ProductCart WHERE pid = ?)";
        String deleteProduct = "DELETE FROM Product WHERE id = ?";

        try {
            connection.setAutoCommit(false); // Bắt đầu giao dịch

            // Xóa các bản ghi liên quan trong bảng Feedback
            try (PreparedStatement psFeedback = connection.prepareStatement(deleteFeedback)) {
                psFeedback.setInt(1, productId);
                psFeedback.executeUpdate();
            }

            // Xóa các bản ghi liên quan trong bảng ProductCart
            try (PreparedStatement psProductCart = connection.prepareStatement(deleteProductCart)) {
                psProductCart.setInt(1, productId);
                psProductCart.executeUpdate();
            }

            // Xóa các bản ghi liên quan trong bảng OrderDetail
            try (PreparedStatement psOrderDetail = connection.prepareStatement(deleteOrderDetail)) {
                psOrderDetail.setInt(1, productId);
                psOrderDetail.executeUpdate();
            }

            // Xóa các bản ghi liên quan trong bảng Cart
            try (PreparedStatement psCart = connection.prepareStatement(deleteCart)) {
                psCart.setInt(1, productId);
                psCart.executeUpdate();
            }

            // Cuối cùng, xóa sản phẩm
            try (PreparedStatement psProduct = connection.prepareStatement(deleteProduct)) {
                psProduct.setInt(1, productId);
                psProduct.executeUpdate();
            }

            connection.commit(); // Cam kết giao dịch nếu tất cả các lệnh đều thành công
            System.out.println("Đã xóa sản phẩm và các bản ghi liên quan thành công!");
        } catch (SQLException e) {
            try {
                connection.rollback(); // Hủy bỏ giao dịch nếu có lỗi
            } catch (SQLException rollbackEx) {
                System.out.println("Rollback failed: " + rollbackEx);
            }
            System.out.println("Lỗi trong quá trình xóa sản phẩm: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true); // Trở lại chế độ auto-commit mặc định
            } catch (SQLException ex) {
                System.out.println("Không thể thiết lập lại chế độ auto-commit: " + ex);
            }
        }
    }

    public Product getProductById(int productId) {
        Product product = null;
        String sql = "SELECT * FROM Product WHERE id = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, productId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                // Create a new Product object and set its properties
                product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setImage(rs.getString("image"));
                product.setPrice(rs.getInt("price"));
                product.setDescription(rs.getString("description"));
                product.setStock(rs.getInt("stock"));

                // Fetch the category and brand objects using their IDs
                Category category = getCategoryById(rs.getInt("cid"));
                Brand brand = getBrandById(rs.getInt("bid"));

                product.setCategory(category);
                product.setBrand(brand);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return product;
    }
    // Utility method to fetch a category by its ID

    private Category getCategoryById(int categoryId) throws SQLException {
        String sql = "SELECT * FROM Category WHERE cid = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, categoryId);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            return new Category(rs.getInt("cid"), rs.getString("name"));
        }
        return null;
    }

// Utility method to fetch a brand by its ID
    private Brand getBrandById(int brandId) throws SQLException {
        String sql = "SELECT * FROM Brand WHERE bid = ?";
        PreparedStatement st = connection.prepareStatement(sql);
        st.setInt(1, brandId);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            return new Brand(rs.getInt("bid"), rs.getString("name"));
        }
        return null;
    }

    public List<Product> searchByNameProduct(String keyword) {
        List<Product> searchResult = new ArrayList<>();
        String sql = "SELECT p.id AS product_id, p.name AS product_name, p.image AS product_image, p.price AS product_price, p.description AS product_description, p.stock AS product_stock, "
                + "b.bid AS brand_id, b.name AS brand_name, "
                + "c.cid AS category_id, c.name AS category_name "
                + "FROM Product p "
                + "JOIN Brand b ON p.bid = b.bid "
                + "JOIN Category c ON p.cid = c.cid "
                + "WHERE p.name LIKE ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, "%" + keyword + "%");
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Brand brand = new Brand(rs.getInt("brand_id"), rs.getString("brand_name"));
                Category category = new Category(rs.getInt("category_id"), rs.getString("category_name"));

                Product p = new Product();
                p.setId(rs.getInt("product_id"));
                p.setName(rs.getString("product_name"));
                p.setImage(rs.getString("product_image"));
                p.setPrice(rs.getInt("product_price"));
                p.setDescription(rs.getString("product_description"));
                p.setStock(rs.getInt("product_stock"));
                p.setBrand(brand);
                p.setCategory(category);
                searchResult.add(p);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return searchResult;
    }

    public List<Product> getProductsByPriceRange(double minPrice, double maxPrice) {
        List<Product> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM Product WHERE price >= ? AND price <= ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setDouble(1, minPrice);
            stm.setDouble(2, maxPrice);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setImage(rs.getString("image"));
                p.setPrice(rs.getInt("price"));
                p.setDescription(rs.getString("description"));
                p.setStock(rs.getInt("stock"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return list;
    }

    public List<Product> getProductsByCategoryId(int categoryId) {
        List<Product> list = new ArrayList<>();
        try {
            String sql = "select * from Product where Product.cid =?";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, categoryId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setImage(rs.getString("image"));
                p.setPrice(rs.getInt("price"));
                p.setDescription(rs.getString("description"));
                p.setStock(rs.getInt("stock"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return list;
    }

    public List<Product> getProductsByBrandId(int BrandId) {
        List<Product> list = new ArrayList<>();
        try {
            String sql = "select * from Product where Product.bid =?";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, BrandId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setImage(rs.getString("image"));
                p.setPrice(rs.getInt("price"));
                p.setDescription(rs.getString("description"));
                p.setStock(rs.getInt("stock"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return list;
    }

    public List<Product> getAllProductsLast() {
        List<Product> list = new ArrayList<>();
        try {
            String sql = "SELECT TOP 4 * FROM product ORDER BY ID DESC";
            PreparedStatement stm = connection.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setImage(rs.getString("image"));
                p.setPrice(rs.getInt("price"));
                p.setDescription(rs.getString("description"));
                p.setStock(rs.getInt("stock"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return list;
    }

    public List<Product> getProductsWithPagging(int page, int PAGE_SIZE) {
        List<Product> list = new ArrayList<>();
        try {
            String sql = "select *  from Product order by id\n"
                    + "offset (?-1)*? row fetch next ? rows only";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, page);
            stm.setInt(2, PAGE_SIZE);
            stm.setInt(3, PAGE_SIZE);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setImage(rs.getString("image"));
                p.setPrice(rs.getInt("price"));
                p.setDescription(rs.getString("description"));
                p.setStock(rs.getInt("stock"));
                list.add(p);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return list;
    }

    public int getTotalProducts() {
        try {
            String sql = "select count(id)  from Product ";

            PreparedStatement stm = connection.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return 0;
    }

    public static void main(String[] args) {
        ProductDao p = new ProductDao();
        p.deleteProduct(1);

    }
}
