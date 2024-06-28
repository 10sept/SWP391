/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import Model.Cart;
import Model.Product;
import Model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class CartDao extends DBContext {

    public void addToCart(User u, Product p, int num) {
        // Check if the product already exists in the cart
        String checkIfExistsSql = "SELECT * FROM [dbo].[Cart] WHERE userId = ? AND pid = ?";
        String updateQuantitySql = "UPDATE [dbo].[Cart] SET quantity = quantity + ? WHERE userId = ? AND pid = ?";
        String insertNewProductSql = "INSERT INTO [dbo].[Cart] ([userId], [pid], [quantity], [price]) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement checkIfExistsSt = connection.prepareStatement(checkIfExistsSql);
            checkIfExistsSt.setInt(1, u.getId());
            checkIfExistsSt.setInt(2, p.getId());
            ResultSet rs = checkIfExistsSt.executeQuery();

            if (rs.next()) {
                // Product already exists in the cart, update quantity
                int currentQuantity = rs.getInt("quantity");
                PreparedStatement updateSt = connection.prepareStatement(updateQuantitySql);
                updateSt.setInt(1, num);
                updateSt.setInt(2, u.getId());
                updateSt.setInt(3, p.getId());
                updateSt.executeUpdate();
            } else {
                // Product does not exist in the cart, insert new product
                PreparedStatement insertSt = connection.prepareStatement(insertNewProductSql);
                insertSt.setInt(1, u.getId());
                insertSt.setInt(2, p.getId());
                insertSt.setInt(3, num);
                insertSt.setLong(4, p.getPrice() * num);
                insertSt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public List<Cart> getCartByUid(int uid) {
        List<Cart> list = new ArrayList<>();
        String sql = "select c.id,c.userId,c.pid,c.quantity,c.price,p.image,p.name\n"
                + "from Cart c join Product p on c.pid = p.id where c.userId = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, uid);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Cart c = new Cart();
                c.setId(rs.getInt("id"));
                c.setUserId(rs.getInt("userId"));
                c.setPid(rs.getInt("pid"));
                c.setQuantity(rs.getInt("quantity"));
                c.setImage(rs.getString("image"));
                c.setPrice(rs.getLong("price"));
                c.setName(rs.getString("name"));
                list.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching cart items: " + e.getMessage());
        }
        return list;
    }

    public void deleteItemById(int cartId) {
        String deleteProductCartSql = "DELETE FROM [dbo].[ProductCart] WHERE cartId = ?";
        String deleteCartSql = "DELETE FROM [dbo].[Cart] WHERE id = ?";
        try {
            // Start a transaction
            connection.setAutoCommit(false);

            // Delete from ProductCart first
            try (PreparedStatement st = connection.prepareStatement(deleteProductCartSql)) {
                st.setInt(1, cartId);
                st.executeUpdate();
            }

            // Delete from Cart
            try (PreparedStatement st = connection.prepareStatement(deleteCartSql)) {
                st.setInt(1, cartId);
                st.executeUpdate();
            }

            // Commit transaction
            connection.commit();
        } catch (SQLException e) {
            System.out.println("Error deleting cart item: " + e.getMessage());
            try {
                // Rollback transaction if any error occurs
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.out.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
        } finally {
            try {
                // Restore default auto-commit behavior
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Error setting auto-commit back to true: " + ex.getMessage());
            }
        }
    }

    public long calculateTotalCartPrice(int userId) {
        long total = 0;
        String sql = "SELECT SUM(price) AS total FROM [dbo].[Cart] WHERE userId = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, userId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                total = rs.getLong("total");
            }
        } catch (SQLException e) {
            System.out.println("Error calculating total cart price: " + e.getMessage());
        }

        return total;
    }

    public static void main(String[] args) {
    AccountDao a = new AccountDao();
    User u = a.GetAccountById(2); // Assuming userId 1
    CartDao c = new CartDao();
    
    // Call the calculateTotalCartPrice method to get the total price of products in the cart
    long totalPrice = c.calculateTotalCartPrice(u.getId());
    
    System.out.println("Total Price of Cart: " + totalPrice);
}

}
