/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import Model.Category;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TRONG TAI
 */
public class CategoryDao extends DBContext {

    public List<Category> getAllCategory() {
        List<Category> list = new ArrayList<>();

        try {
            String sql = "SELECT * FROM Category";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Category a = new Category();
                a.setId(rs.getInt(1));
                a.setName(rs.getString(2));
                list.add(a);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return list;

    }

    public Category getCategoryById(int id) {
        Category category = null;
        try {
            String sql = "Select * from Category WHERE cid = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                category = new Category();
                category.setId(rs.getInt("cid"));
                category.setName(rs.getString("name"));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return category;
    }

    public void insertCategory(String name) {
        String query = "INSERT INTO [dbo].[Category]\n"
                + "           ([name])\n"
                + "     VALUES (?)";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, name);
            ps.executeUpdate();
            System.out.println("Category inserted successfully.");
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    public void UpdateCategory(int cid, String name) {
        String sql = "UPDATE [dbo].[Category]\n"
                + "   SET [name] = ?\n"
                + " WHERE cid = ?";

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, name);
            st.setInt(2, cid);
            st.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public String deleteCategory(int cid) {
        String checkSql = "SELECT COUNT(*) FROM [dbo].[Product] WHERE cid = ?";
        String deleteSql = "DELETE FROM [dbo].[Category] WHERE cid = ?";
        String message = "";

        try {
            // Check for associated products
            PreparedStatement checkSt = connection.prepareStatement(checkSql);
            checkSt.setInt(1, cid);
            ResultSet rs = checkSt.executeQuery();

            if (rs.next()) {
                int productCount = rs.getInt(1);
                if (productCount > 0) {
                    message = "Không thể xóa danh mục này. Có " + productCount + " sản phẩm được liên kết với danh mục này. Vui lòng xóa hoặc chỉ định lại các sản phẩm này trước khi xóa danh mục.";
                    return message;
                }
            }

            // Proceed with deleting the category
            PreparedStatement deleteSt = connection.prepareStatement(deleteSql);
            deleteSt.setInt(1, cid);
            deleteSt.executeUpdate();
            message = "Xóa danh mục thành công.";

        } catch (SQLException e) {
            message = "SQL Error: " + e.getMessage();
        }
        return message;
    }

    public static void main(String[] args) {

        CategoryDao cd = new CategoryDao();
        cd.deleteCategory(1);
    }
}
