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
</head>
<body>
	<p><%=omikuji.disp()%></p><br>
	<!-- <button onclick="confirmAction()">おみくじを続けますか？</button>
	
	<script>
		function confirmAction(){
			/* はいいいえボタン */
			}
	</script> -->
	
</body>
</html>