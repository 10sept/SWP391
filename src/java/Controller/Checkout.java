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
import Model.OrderDetail;
import Model.ProductVariant;
import Model.User;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author ADMIN
 */
public class Checkout extends HttpServlet {

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
            out.println("<title>Servlet Checkout</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Checkout at " + request.getContextPath() + "</h1>");
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
        HttpSession session = request.getSession();
        User acc = (User) session.getAttribute("acc");
        if (acc == null) {
            response.sendRedirect("login.jsp");
        } else {
            CartDao c = new CartDao();
            long total = c.calculateTotalCartPrice(acc.getId());
            List<Cart> list = c.getCartByUid(acc.getId());
            request.setAttribute("listcart", list);
            request.setAttribute("total", total);
            request.getRequestDispatcher("checkout.jsp").forward(request, response);
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
        HttpSession session = request.getSession();
        User acc = (User) session.getAttribute("acc");

        if (acc == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String paymentMethod = request.getParameter("payment");
        CartDao cartDao = new CartDao();
        ProductDao productDao = new ProductDao();
        List<Cart> cartList = cartDao.getCartByUid(acc.getId());
        long total = cartDao.calculateTotalCartPrice(acc.getId());
        String name = request.getParameter("fullname");
        String phone = request.getParameter("phone");
        String city = request.getParameter("city");
        String district = request.getParameter("district");
        String commune = request.getParameter("commune");
        String address = request.getParameter("address");

        // Save data to session
        session.setAttribute("fullname", name);
        session.setAttribute("phone", phone);
        session.setAttribute("city", city);
        session.setAttribute("district", district);
        session.setAttribute("commune", commune);
        session.setAttribute("address", address);

        LocalDateTime currentDate = LocalDateTime.now(); // Adjust to get current date/time properly
        OrderDao orderDao = new OrderDao(); // Correct variable name from 'od' to 'orderDao'
        if (paymentMethod == null) {
            session.setAttribute("messpayment", "bạn cần phải chọn 1 trong 2 phương thức thanh toán");
            response.sendRedirect("checkout");
        }
        if ("cod".equals(paymentMethod)) {
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
            List<OrderDetail> orderDetails = new ArrayList<>();
            for (Cart cart : cartList) {
                OrderDetail detail = new OrderDetail();
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

            //get all order
            OrderDao orderdao = new OrderDao();
            List<Order> listOrder = orderdao.getAllOrdersByUserId(acc.getId());
            int oid = orderDao.findLastOrderId(acc.getId());
            Date date = orderdao.getOrderByOrderId(oid).getDate();

            PaymentDao paymentdao = new PaymentDao();

            paymentdao.insertPayment(oid, 1, (java.sql.Date) date, (int) total);
            // Send confirmation email
            try {
                sendEmail(acc.getEmail(), cartList, total);
            } catch (MessagingException | UnsupportedEncodingException e) {
                e.printStackTrace(); // Handle email sending exception properly
            }

            // Redirect to home page or another appropriate page
            response.sendRedirect("home.jsp");
        } else if ("vnpay".equals(paymentMethod)) {
            // For VNPAY, you might handle the payment process differently
            request.setAttribute("totalPriceAllProduct", total);
            request.getRequestDispatcher("vnpay_pay.jsp").forward(request, response);
        }
    }

    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    private void sendEmail(String to, List<Cart> cartList, long total) throws MessagingException, UnsupportedEncodingException {
        final String username = "HoLaTechSE1803@gmail.com";
        final String password = "xgdm ytoa shxw iwdk";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.mime.charset", "UTF-8");

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };

        Session session = Session.getInstance(props, auth);

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(MimeUtility.encodeText("Xác nhận đơn hàng", "UTF-8", "B"));

        // Create the email content
        StringBuilder emailContent = new StringBuilder();
        emailContent.append("Cảm ơn bạn đã mua sắm tại HoLaTech!!!\n Đây là đơn hàng chi tiết của bạn:\n\n");

        for (Cart cart : cartList) {
            emailContent.append("Tên sản phẩm: ").append(cart.getName())
                    .append("\nMàu sắc: ").append(cart.getColorName())
                    .append("\nSố lượng: ").append(cart.getQuantity())
                    .append("\nGiá: ").append(cart.getPrice())
                    .append("\nTổng tiền của sản phẩm: ").append(cart.getTotalOneProduct())
                    .append("\n\n");
        }

        emailContent.append("Tổng tiền đơn hàng: ").append(total).append("\n\n");
        emailContent.append("Bạn sẽ sớm nhận được đơn hàng của mình.");
        emailContent.append("Chúng tôi mong ràng bạn sẽ có những trải nghiệm tuyệt vời khi mua sắm tại HoLaTech!!!!");

        message.setContent(emailContent.toString(), "text/plain; charset=UTF-8");

        Transport.send(message);
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
