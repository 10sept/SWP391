<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Order Detail</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.bundle.min.js"></script>
    <style>
        body {
            min-height: 100vh;
            background-size: cover;
            font-family: 'Lato', sans-serif;
            color: rgba(116, 116, 116, 0.667);
            background: linear-gradient(140deg, #fff, 50%, #BA68C8);
        }
        .container {
            margin-top: 50px;
        }
        .card {
            box-shadow: 2px 2px 10px 0px rgb(190, 108, 170);
        }
        .product-image {
            width: 100px;
        }
        .product-info {
            margin-top: 10px;
        }
        .order-info {
            margin-top: 20px;
        }
        .product-category,
        .product-brand {
            font-size: 0.9em;
            color: #555;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="card">
            <div class="card-header bg-white">
                <h5 class="card-title text-center">Order Detail</h5>
            </div>
            <div class="card-body">
                <div class="order-info">
                    <p><strong>Ngày đặt hàng:</strong> ${orders.date}</p>
                    <p><strong>Trạng thái đơn hàng:</strong> ${orders.statusid.status}</p>
                    <p><strong>Tên người nhận:</strong> ${orders.userId.fullName}</p> <!-- Assuming this is the user's full name -->
                    <!-- Add more order information as needed -->
                    <p><strong>Địa chỉ giao hàng:</strong> ${orders.detailedAddress}, ${orders.commune}, ${orders.district}, ${orders.province}</p>
                </div>
                <hr>
                <table class="table">
                    <thead>
                        <tr>
                            <th>Hình ảnh</th>
                            <th>Tên sản phẩm</th>
                            <th>Loại</th>
                            <th>Nhãn hàng</th>
                            <th>Giá</th>
                            <th>Số lượng</th>
                            <th>Tổng tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="orderDetail" items="${orders.orderDetails}">
                            <tr class="product-row" data-product-id="${orderDetail.pid.id}">
                                <td><img src="${orderDetail.pid.image}" alt="${orderDetail.pid.name}" class="product-image"></td>
                                <td>
                                    ${orderDetail.pid.name}
                                    
                                    
                                </td>
                                <td><div class="product-category">${orderDetail.pid.category.name}</div></td>
                                <td><div class="product-brand">${orderDetail.pid.brand.name}</div></td>
                                <td><fmt:formatNumber value="${orderDetail.price}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                                <td>${orderDetail.quantity}</td>
                                <td><fmt:formatNumber value="${orderDetail.total}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <div class="text-center">
                    <a href="myorder" class="btn btn-primary">Quay về</a>
                </div>
            </div>
        </div>
    </div>

    <script>
        $(document).ready(function() {
            // Show product info when clicking on a product row
            $('.product-row').click(function() {
                // Hide all product info rows
                $('.product-info-row').hide();
                
                // Get product id from data attribute
                var productId = $(this).data('product-id');
                
                // Show product info row for the clicked product
                $('#product-info-' + productId).show();
            });
        });
    </script>
</body>
</html>
