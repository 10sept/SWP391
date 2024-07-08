/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import Dal.AccountDao;
import Model.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ADMIN
 */
public class UpdateAccount extends HttpServlet {

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
            out.println("<title>Servlet UpdateAccount</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UpdateAccount at " + request.getContextPath() + "</h1>");
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
        int id = Integer.parseInt(request.getParameter("id"));
        AccountDao ad = new AccountDao();
        User u = ad.GetAccountById(id);
        HttpSession session = request.getSession();

        if (u == null) {
            session.setAttribute("mess2", "User not found.");
            response.sendRedirect("accountmanagerment");
            return;
        }

        request.setAttribute("user", u);
        request.setAttribute("mess2", session.getAttribute("mess2"));
        session.removeAttribute("mess2");  // Clear the message after setting it to the request
        request.getRequestDispatcher("updateAccount.jsp").forward(request, response);
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
        int id = Integer.parseInt(request.getParameter("id"));
        AccountDao ad = new AccountDao();
        User u = ad.GetAccountById(id);
        
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String gender = request.getParameter("gender");
        String dobStr = request.getParameter("dob");

        try {
            // Validate date of birth format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate = sdf.parse(dobStr);
            java.sql.Date dob = new java.sql.Date(parsedDate.getTime());

            // Validate if date of birth is before current date
            java.util.Date currentDate = new java.util.Date();
            if (parsedDate.after(currentDate)) {
                session.setAttribute("mess2", "Ngày sinh phải trước ngày hiện tại.");
                response.sendRedirect("updateaccount");
                return; // Important: return immediately after sendRedirect
            }

            // Validate phone number format (10 digits starting with 0)
            if (!phone.matches("0\\d{9}")) {
                session.setAttribute("mess2", "Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại có 10 số và bắt đầu bằng số 0.");
                response.sendRedirect("updateaccount");
                return; // Important: return immediately after sendRedirect
            }

            ad.UpdateProfileById(id, fullName, phone, address, dob, gender);

            // Update the session attribute with the new user information
            User updatedUser = ad.GetAccountById(id);
            session.setAttribute("acc", updatedUser);

            // Redirect back to the account management page after successful update
            response.sendRedirect("accountmanagerment");
        } catch (ParseException e) {
            session.setAttribute("mess2", "Invalid date format. Please use yyyy-MM-dd.");
            response.sendRedirect("updateaccount");
        } catch (Exception e) {
            session.setAttribute("mess2", "An error occurred while updating the account.");
            response.sendRedirect("updateaccount");
        }
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
