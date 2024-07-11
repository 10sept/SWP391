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

    public void addToCart(User u, Product p, int num, int variantId) {
        // Check if the product already exists in the cart
        String checkIfExistsSql = "SELECT * FROM [dbo].[Cart] WHERE userId = ? AND pid = ? AND variantId = ?";
        String updateQuantityAndPriceSql = "UPDATE [dbo].[Cart] SET quantity = quantity + ?, price = price + ? WHERE userId = ? AND pid = ? AND variantId = ?";
        String insertNewProductSql = "INSERT INTO [dbo].[Cart] ([userId], [pid], [quantity], [price], [variantId]) VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement checkIfExistsSt = connection.prepareStatement(checkIfExistsSql);
            checkIfExistsSt.setInt(1, u.getId());
            checkIfExistsSt.setInt(2, p.getId());
            checkIfExistsSt.setInt(3, variantId);
            ResultSet rs = checkIfExistsSt.executeQuery();

            if (rs.next()) {
                // Product already exists in the cart, update quantity and price
                int currentQuantity = rs.getInt("quantity");
                PreparedStatement updateSt = connection.prepareStatement(updateQuantityAndPriceSql);
                updateSt.setInt(1, num);
                updateSt.setLong(2, p.getPrice() * num);
                updateSt.setInt(3, u.getId());
                updateSt.setInt(4, p.getId());
                updateSt.setInt(5, variantId);
                updateSt.executeUpdate();
            } else {
                // Product does not exist in the cart, insert new product
                PreparedStatement insertSt = connection.prepareStatement(insertNewProductSql);
                insertSt.setInt(1, u.getId());
                insertSt.setInt(2, p.getId());
                insertSt.setInt(3, num);
                insertSt.setLong(4, p.getPrice() * num);
                insertSt.setInt(5, variantId);
                insertSt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public List<Cart> getCartByUid(int uid) {
        List<Cart> list = new ArrayList<>();
        String sql = "SELECT c.id, c.userId, c.pid, c.variantId, c.quantity, c.price as totalOneProduct, "
                + "p.price as productPrice, p.image, p.name, co.id AS colorId, co.colorName "
                + "FROM Cart c "
                + "JOIN Product p ON c.pid = p.id "
                + "JOIN ProductVariant pv ON c.variantId = pv.id "
                + "JOIN Color co ON pv.colorId = co.id "
                + "WHERE c.userId = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, uid);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Cart c = new Cart();
                c.setId(rs.getInt("id"));
                c.setUserId(rs.getInt("userId"));
                c.setPid(rs.getInt("pid"));
                c.setVariantId(rs.getInt("variantId"));
                c.setQuantity(rs.getInt("quantity"));
                c.setTotalOneProduct(rs.getInt("totalOneProduct"));
                c.setPrice(rs.getInt("productPrice"));
                c.setImage(rs.getString("image"));
                c.setName(rs.getString("name"));
                // Set color information
                c.setColorId(rs.getInt("colorId"));
                c.setColorName(rs.getString("colorName"));
                list.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching cart items: " + e.getMessage());
        }
        return list;
    }

    public void deleteItemById(int cartId) {
        String deleteCartSql = "DELETE FROM [dbo].[Cart] WHERE id = ?";
        try {
            // Start a transaction
            connection.setAutoCommit(false);

            // Delete from ProductCart first
//            try (PreparedStatement st = connection.prepareStatement(deleteProductCartSql)) {
//                st.setInt(1, cartId);
//                st.executeUpdate();
//            }
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

    public void incrementQuantity(int userId, int pid) {
        String sql = "UPDATE [dbo].[Cart] SET quantity = quantity + 1, price = price + (SELECT price FROM Product WHERE id = ?) WHERE userId = ? AND pid = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, pid);
            st.setInt(2, userId);
            st.setInt(3, pid);
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error incrementing quantity: " + e.getMessage());
        }
    }

    public void decrementQuantity(int userId, int pid) {
        // If quantity is 1, remove the item
        String sql = "DELETE FROM [dbo].[Cart] WHERE userId = ? AND pid = ? AND quantity = 1";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, userId);
            st.setInt(2, pid);
            int rowsAffected = st.executeUpdate();
            if (rowsAffected == 0) {
                // If no rows were deleted, decrement quantity
                sql = "UPDATE [dbo].[Cart] SET quantity = quantity - 1, price = price - (SELECT price FROM Product WHERE id = ?) WHERE userId = ? AND pid = ?";
                try (PreparedStatement st2 = connection.prepareStatement(sql)) {
                    st2.setInt(1, pid);
                    st2.setInt(2, userId);
                    st2.setInt(3, pid);
                    st2.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error decrementing quantity: " + e.getMessage());
        }
    }
    public void clearCart(int userId) {
        String query = "DELETE FROM Cart WHERE userId = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        CartDao cartDao = new CartDao();

        int userIdToTest = 2; // Replace with an actual user ID from your database
        int productIdToTest = 2; // Replace with an actual product ID from your database

        // Test incrementQuantity
//        System.out.println("Incrementing quantity for user ID: " + userIdToTest + " and product ID: " + productIdToTest);
//        cartDao.incrementQuantity(userIdToTest, productIdToTest);
//
//        // Verify the incremented quantity
//        List<Cart> cartListAfterIncrement = cartDao.getCartByUid(userIdToTest); 
//        for (Cart cart : cartListAfterIncrement) {
//            if (cart.getPid() == productIdToTest) {
//                System.out.println("After increment - Cart ID: " + cart.getId() + ", Product ID: " + cart.getPid() + ", Quantity: " + cart.getQuantity() + ", Total Price: " + cart.getTotalOneProduct());
//            }
//        }
        // Test decrementQuantity
        System.out.println("Decrementing quantity for user ID: " + userIdToTest + " and product ID: " + productIdToTest);
        cartDao.decrementQuantity(userIdToTest, productIdToTest);

        // Verify the decremented quantity
        List<Cart> cartListAfterDecrement = cartDao.getCartByUid(userIdToTest);
        for (Cart cart : cartListAfterDecrement) {
            if (cart.getPid() == productIdToTest) {
                System.out.println("After decrement - Cart ID: " + cart.getId() + ", Product ID: " + cart.getPid() + ", Quantity: " + cart.getQuantity() + ", Total Price: " + cart.getTotalOneProduct());
            }
        }
    }

}
