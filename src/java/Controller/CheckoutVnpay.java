/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import Dal.CartDao;
import Dal.OrderDao;
import Dal.PaymentDao;
import Dal.ProductDao;
import Model.Cart;
import Model.Order;
import Model.ProductVariant;
import Model.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class CheckoutVnpay extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet CheckoutVnpay</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CheckoutVnpay at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String status = request.getParameter("result");
        HttpSession session = request.getSession();
        User acc = (User) session.getAttribute("acc");

        if (acc == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        if (status.equalsIgnoreCase("00")) {
            CartDao cartDao = new CartDao();
            ProductDao productDao = new ProductDao();
            List<Cart> cartList = cartDao.getCartByUid(acc.getId());
            long total = cartDao.calculateTotalCartPrice(acc.getId());

            // Retrieve data from session
            String name = (String) session.getAttribute("fullname");
            String phone = (String) session.getAttribute("phone");
            String city = (String) session.getAttribute("city");
            String district = (String) session.getAttribute("district");
            String commune = (String) session.getAttribute("commune");
            String address = (String) session.getAttribute("address");

            LocalDateTime currentDate = LocalDateTime.now(); // Adjust to get current date/time properly
            OrderDao orderDao = new OrderDao(); // Correct variable name from 'od' to 'orderDao'

            // Handle cash on delivery payment
            Order order = new Order();
            order.setUserId(acc);
            order.setName(name);
            order.setPhone(phone);
            order.setProvince(city); // Assuming 'city' represents province in this context
            order.setDistrict(district);
            order.setCommune(commune);
            order.setDetailedAddress(address);
            order.setDate(Date.from(currentDate.atZone(ZoneId.systemDefault()).toInstant()));
            order.setTotal(total);
            order.setStatusid(orderDao.getStatusById(1)); // Assuming status ID 1 represents 'Pending' or similar

            // Add order details from cart items
            List<Model.OrderDetail> orderDetails = new ArrayList<>();
            for (Cart cart : cartList) {
                Model.OrderDetail detail = new Model.OrderDetail();
                detail.setPid(productDao.getProductById(cart.getPid())); // Assuming getPid() returns product ID

                // Retrieve and set ProductVariant
                ProductVariant variant = productDao.getProductVariantByID(cart.getVariantId());
                if (variant == null) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Product variant not found for cart item.");
                    return;
                }
                detail.setVariantId(variant);

                detail.setPrice(cart.getPrice());
                detail.setQuantity(cart.getQuantity());
                detail.setTotal(cart.getTotalOneProduct());
                orderDetails.add(detail);
            }
            order.setOrderDetails(orderDetails);

            // Perform necessary updates and actions
            for (Cart cart : cartList) {
                productDao.updateProductVariantStock(cart.getVariantId(), cart.getQuantity());
            }
            cartDao.clearCart(acc.getId());

            // Add the order to the database
            orderDao.addOrder(order);

            // Get all orders
            List<Order> listOrder = orderDao.getAllOrdersByUserId(acc.getId());
            int oid = orderDao.findLastOrderId(acc.getId());
            Date date = orderDao.getOrderByOrderId(oid).getDate();

            PaymentDao paymentDao = new PaymentDao();
            paymentDao.insertPayment(oid, 2, (java.sql.Date) date, (int) total);

            // Remove specific session attributes
            session.removeAttribute("fullname");
            session.removeAttribute("phone");
            session.removeAttribute("city");
            session.removeAttribute("district");
            session.removeAttribute("commune");
            session.removeAttribute("address");

            response.sendRedirect("home");
        }else{
            response.sendRedirect("home");
        }
            
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
