<%@page import="omikuji4.Omikuji"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
Omikuji omikuji = (Omikuji) request.getAttribute("omikuji");

%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>おみくじ結果</title>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<div class="container">
	<p><%=omikuji.disp().replace("\n", "<br>")%></p>
	<p>おみくじを続けますか？</p>
	<input type="button"
		onclick="location.href='http://localhost:8080/omikuji_kadai4/result'"
		value="続ける" />
</div>
</body>
</html>