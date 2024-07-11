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
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class UpdateRole extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
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
            out.println("<title>Servlet UpdateRole</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UpdateRole at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        AccountDao ad = new AccountDao();
        List<User> listUser = ad.getAllAccount();
        request.setAttribute("listUser", listUser);
        request.getRequestDispatcher("updateRole.jsp").forward(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String changeRoleStaffStr = request.getParameter("changeRoleStaff");
        String changeRoleAdminStr = request.getParameter("changeRoleAdmin");

        try {
            if (changeRoleStaffStr != null) {
                AccountDao ad = new AccountDao();
                int banId = Integer.parseInt(changeRoleStaffStr);
                ad.updateRoleById(banId, 3);
                // Redirection après avoir terminé toutes les opérations
                response.sendRedirect("updaterole");
                return; // Terminer la méthode doPost après la redirection
            } else if(changeRoleAdminStr != null ) {
                AccountDao ad = new AccountDao();
                int unBanId = Integer.parseInt(changeRoleAdminStr);
                ad.updateRoleById(unBanId, 1);
                // Redirection après avoir terminé toutes les opérations
                response.sendRedirect("updaterole");
                return; // Terminer la méthode doPost après la redirection
            }
        } catch (NumberFormatException e) {
            System.out.println("NumberFormatException: " + e.getMessage());
            // Gérer l'exception selon vos besoins
        }
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
