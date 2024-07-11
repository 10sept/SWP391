package Dal;

import Model.*;
import java.beans.Statement;
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderDao extends DBContext {

    public List<Order> getAllOrdersByUserId(int userId) {
        Map<Integer, Order> orderMap = new LinkedHashMap<>(); // Using LinkedHashMap to maintain insertion order

        String sql = "SELECT "
                + "   O.id AS orderId, "
                + "   O.orderDate, "
                + "   O.total AS total, "
                + "   S.name AS status, "
                + "   P.name AS productName, "
                + "   P.image AS productImage, "
                + "   OD.price, "
                + "   OD.quantity, "
                + "   OD.total AS detailTotal, "
                + "   B.name AS brandName, "
                + "   C.name AS categoryName, "
                + "   U.fullName "
                + "FROM "
                + "   Orders O "
                + "JOIN "
                + "   Status S ON O.statusId = S.id "
                + "JOIN "
                + "   OrderDetail OD ON OD.oid = O.id "
                + "JOIN "
                + "   Product P ON OD.pid = P.id "
                + "JOIN "
                + "   Brand B ON P.bid = B.id "
                + "JOIN "
                + "   Category C ON P.cid = C.id "
                + "JOIN "
                + "   Users U ON O.userId = U.id "
                + "WHERE "
                + "   O.userId = ? "
                + "ORDER BY "
                + "   O.id DESC"; // Ordering by orderId in descending order

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, userId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    int orderId = rs.getInt("orderId");
                    Order order = orderMap.get(orderId);

                    if (order == null) {
                        order = new Order();
                        order.setId(orderId);
                        order.setDate(rs.getDate("orderDate"));
                        order.setTotal(rs.getInt("total"));

                        Status statusObj = new Status();
                        statusObj.setStatus(rs.getString("status"));
                        order.setStatusid(statusObj);

                        User user = new User();
                        user.setId(userId);
                        user.setFullName(rs.getString("fullName"));
                        order.setUserId(user);

                        orderMap.put(orderId, order);
                    }

                    Brand brand = new Brand();
                    brand.setName(rs.getString("brandName"));

                    Category category = new Category();
                    category.setName(rs.getString("categoryName"));

                    Product product = new Product();
                    product.setName(rs.getString("productName"));
                    product.setImage(rs.getString("productImage"));
                    product.setPrice(rs.getInt("price"));
                    product.setBrand(brand);
                    product.setCategory(category);

                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setPrice(rs.getInt("price"));
                    orderDetail.setQuantity(rs.getInt("quantity"));
                    orderDetail.setTotal(rs.getInt("detailTotal"));
                    orderDetail.setPid(product);

                    order.addOrderDetail(orderDetail);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(orderMap.values());
    }

    public void addOrder(Order order) {
        String insertOrderSql = "INSERT INTO Orders (userId, name, phone, province, district, commune, detailedAddress, orderDate, total, statusId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertOrderDetailSql = "INSERT INTO OrderDetail (oid, pid, variantId, price, quantity, total) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            // Start a transaction
            connection.setAutoCommit(false);

            // Insert into Orders table
            PreparedStatement orderStmt = connection.prepareStatement(insertOrderSql, PreparedStatement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, order.getUserId().getId());
            orderStmt.setString(2, order.getName());
            orderStmt.setString(3, order.getPhone());
            orderStmt.setString(4, order.getProvince());
            orderStmt.setString(5, order.getDistrict());
            orderStmt.setString(6, order.getCommune());
            orderStmt.setString(7, order.getDetailedAddress());

            // Convert java.util.Date to java.sql.Timestamp
            java.util.Date utilDate = order.getDate();
            if (utilDate != null) {
                orderStmt.setTimestamp(8, new java.sql.Timestamp(utilDate.getTime()));
            } else {
                orderStmt.setTimestamp(8, null);
            }

            orderStmt.setDouble(9, order.getTotal());
            orderStmt.setInt(10, order.getStatusid().getId());

            int affectedRows = orderStmt.executeUpdate();

            if (affectedRows == 0) {
                connection.rollback();
                throw new SQLException("Creating order failed, no rows affected.");
            }

            // Get the generated order ID
            int orderId;
            try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                } else {
                    connection.rollback();
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }

            // Insert into OrderDetail table
            PreparedStatement orderDetailStmt = connection.prepareStatement(insertOrderDetailSql);
            for (OrderDetail detail : order.getOrderDetails()) {
                orderDetailStmt.setInt(1, orderId);
                orderDetailStmt.setInt(2, detail.getPid().getId());
                orderDetailStmt.setInt(3, detail.getVariantId().getId());
                orderDetailStmt.setDouble(4, detail.getPrice());
                orderDetailStmt.setInt(5, detail.getQuantity());
                orderDetailStmt.setDouble(6, detail.getTotal());

                orderDetailStmt.addBatch();
            }

            orderDetailStmt.executeBatch();

            // Commit the transaction
            connection.commit();
        } catch (SQLException e) {
            System.out.println("Error occurred while adding order: " + e.getMessage());
            e.printStackTrace(); // Print the full stack trace for debugging purposes
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Status getStatusById(int id) {
        String sql = "SELECT id, name FROM Status WHERE id = ?";
        Status status = null;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                status = new Status();
                status.setId(resultSet.getInt("id"));
                status.setStatus(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return status;
    }

    // Method to retrieve an order by orderId
    public Order getOrderByOrderId(int orderId) {
        Order order = null;
        String sql = "SELECT "
                + "   O.id AS orderId, "
                + "   O.orderDate, "
                + "   O.total AS total, "
                + "   S.name AS status, "
                + "   P.name AS productName, "
                + "   P.image AS productImage, "
                + "   OD.price, "
                + "   OD.quantity, "
                + "   OD.total AS detailTotal, "
                + "   B.name AS brandName, "
                + "   C.name AS categoryName, "
                + "   U.fullName, "
                + "   O.province, "
                + "   O.district, "
                + "   O.commune, "
                + "   O.detailedAddress "
                + "FROM "
                + "   Orders O "
                + "JOIN "
                + "   Status S ON O.statusId = S.id "
                + "JOIN "
                + "   OrderDetail OD ON OD.oid = O.id "
                + "JOIN "
                + "   Product P ON OD.pid = P.id "
                + "JOIN "
                + "   Brand B ON P.bid = B.id "
                + "JOIN "
                + "   Category C ON P.cid = C.id "
                + "JOIN "
                + "   Users U ON O.userId = U.id "
                + "WHERE "
                + "   O.id = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, orderId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    if (order == null) {
                        order = new Order();
                        order.setId(orderId);
                        order.setDate(rs.getDate("orderDate"));
                        order.setTotal(rs.getDouble("total"));

                        Status statusObj = new Status();
                        statusObj.setStatus(rs.getString("status"));
                        order.setStatusid(statusObj);

                        User user = new User();
                        user.setFullName(rs.getString("fullName"));
                        order.setUserId(user);

                        order.setProvince(rs.getString("province"));
                        order.setDistrict(rs.getString("district"));
                        order.setCommune(rs.getString("commune"));
                        order.setDetailedAddress(rs.getString("detailedAddress"));
                    }

                    // Create OrderDetail object and set its properties
                    Brand brand = new Brand();
                    brand.setName(rs.getString("brandName"));

                    Category category = new Category();
                    category.setName(rs.getString("categoryName"));

                    Product product = new Product();
                    product.setName(rs.getString("productName"));
                    product.setImage(rs.getString("productImage"));
                    product.setBrand(brand);
                    product.setCategory(category);

                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setPrice(rs.getInt("price"));
                    orderDetail.setQuantity(rs.getInt("quantity"));
                    orderDetail.setTotal(rs.getInt("detailTotal"));
                    orderDetail.setPid(product);

                    // Add OrderDetail to the Order's list of OrderDetails
                    order.addOrderDetail(orderDetail);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return order;
    }
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.id AS order_id, o.orderDate, o.total, o.name AS order_name, o.phone, o.province, o.district, o.commune, o.detailedAddress, "
                + "u.id AS user_id, u.fullName AS user_name, u.email AS user_email, "
                + "s.id AS status_id, s.name AS status_name "
                + "FROM Orders o "
                + "JOIN Users u ON o.userId = u.id "
                + "JOIN Status s ON o.statusId = s.id";

        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("user_id"));
                user.setFullName(rs.getString("user_name"));
                user.setEmail(rs.getString("user_email"));
                // Add other fields as necessary

                Status status = new Status();
                status.setId(rs.getInt("status_id"));
                status.setStatus(rs.getString("status_name"));
                // Add other fields as necessary

                Order order = new Order();
                order.setId(rs.getInt("order_id"));
                order.setUserId(user);
                order.setDate(rs.getTimestamp("orderDate"));
                order.setStatusid(status);
                order.setTotal(rs.getDouble("total"));
                order.setName(rs.getString("order_name"));
                order.setPhone(rs.getString("phone"));
                order.setProvince(rs.getString("province"));
                order.setDistrict(rs.getString("district"));
                order.setCommune(rs.getString("commune"));
                order.setDetailedAddress(rs.getString("detailedAddress"));

                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }
    
    public List<Status> getAllStatus() {
        List<Status> statuses = new ArrayList<>();
        String sql = "SELECT * FROM Status";

        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Status status = new Status();
                status.setId(rs.getInt("id"));
                status.setStatus(rs.getString("name"));
                statuses.add(status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return statuses;
    }

    public void updateOrderStatus(int orderId, int statusId) {
        String sql = "UPDATE Orders SET statusId = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, statusId);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int findLastOrderId(int userId) {
        String sql = "SELECT TOP 1 id FROM Orders WHERE userId = ? ORDER BY id DESC";
        int lastOrderId = -1;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                lastOrderId = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lastOrderId;
    }
    
    public List<Order> selectAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT orderDate, total FROM Orders ORDER BY orderDate";

        try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                Order order = new Order();
                order.setDate(rs.getTimestamp("orderDate"));
                order.setTotal(rs.getDouble("total"));
                list.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Order> selectOrdersByDateRange(Date startDate, Date endDate) {
        List<Order> list = new ArrayList<>();
        try {
            String sql = "SELECT CONVERT(date, orderDate) AS orderDate, SUM(total) AS total \n"
                    + "FROM Orders  \n"
                    + "GROUP BY CONVERT(date, orderDate) \n"
                    + "ORDER BY orderDate;";
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                while (rs.next()) {
                    Order order = new Order();
                     order.setDate(rs.getTimestamp("orderDate"));
                    order.setTotal(rs.getDouble("total"));
                    list.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void main(String[] args) {
        OrderDao orderdao = new OrderDao();
        List<Order> listOrder = orderdao.getAllOrdersByUserId(2);
        int oid = listOrder.size() + 1;
        System.out.println(oid);
        int id = orderdao.findLastOrderId(2);
        System.out.println(id);

//        OrderDao od = new OrderDao();
//        Order order = od.getOrderByOrderId(oid);
//
//        System.out.println(order.getDate());
    }
}
