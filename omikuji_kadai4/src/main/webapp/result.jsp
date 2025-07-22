<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String birthday = (String) request.getAttribute("birthday");


%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>おみくじ結果</title>
</head>
<body>
<!-- 値が取得できているかテスト -->
	<p>誕生日： <%=birthday %></p>
</body>
</html>