<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Orders</title>
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
            .container-fluid {
                margin-top: 50px;
            }
            .card-1 {
                box-shadow: 2px 2px 10px 0px rgb(190, 108, 170);
            }
            .status-bar {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-top: 20px;
                font-size: 14px;
                font-weight: 400;
                color: #666;
            }
            .status-item {
                flex: 1;
                text-align: center;
                position: relative;
            }
            .status-text {
                margin-top: 5px;
                color: #999;
                font-size: 12px;
                font-weight: 300;
            }
            .status-dot {
                width: 12px;
                height: 12px;
                border-radius: 50%;
                background-color: #ddd;
                margin: 0 auto;
            }
            .status-dot.active {
                background-color: rgb(252, 103, 49);
            }
        </style>
    </head>
    <body>
        <div class="container-fluid my-5 d-flex justify-content-center">
            <div class="card card-1">
                <div class="card-header bg-white">
                    <div class="media flex-sm-row flex-column-reverse justify-content-between">
                        <div class="col-auto text-center my-auto pl-0 pt-sm-4">
                            <a href="home">
                                <img class="img-fluid my-auto align-items-center mb-0 pt-3" src="./img/logo2.png" width="115" height="115" >
                            </a>
                            <p class="mb-4 pt-0 Glasses"></p>
                        </div>
                    </div>
                </div>
                <div class="card-body">
                    <h6 class="color-1 mb-0 change-color">Receipts</h6>
                    <table class="table">
                        <thead>
                            <tr style="text-align: center">
                                <th scope="col">Hình ảnh</th>
                                <th scope="col">Tên sản phẩm</th>
                                <th scope="col">Loại</th>
                                <th scope="col">Nhãn hàng</th>
                                <th scope="col">Số lượng</th>
                                <th scope="col">Giá tiền</th>
                                <th scope="col">Tổng tiền</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="order" items="${orders}">
                                <c:forEach var="detail" items="${order.orderDetails}">
                                    <tr style="text-align: center">
                                        <td>
                                            <img style="width: 100px" src="${detail.pid.image}" alt="${detail.pid.name}" />
                                        </td>
                                        <td style="width: 200px">${detail.pid.name}</td>
                                        <td>${detail.pid.category.name}</td>
                                        <td>${detail.pid.brand.name}</td>
                                        <td>${detail.quantity}</td>
                                        <td><fmt:formatNumber value="${detail.pid.price}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                                        <td><fmt:formatNumber value="${detail.total}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                                    </tr>
                                </c:forEach>
                                <tr style="background-color: #f8f9fa;">
                                    <td colspan="14" style="text-align: right; font-weight: bolder;">
                                        <h5>Thành tiền: <a style="color: red; font-size: 30px " ><fmt:formatNumber value="${order.total}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></a></h5>
                                    </td>
                                </tr>
                                <tr style="background-color: #f8f9fa;">
                                    <td colspan="14" style="text-align: right; font-weight: bolder;">
                                        <a href="orderdetail?orderId=${order.id}" class="btn btn-primary">Xem chi tiết</a>                                     
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </body>
</html>
