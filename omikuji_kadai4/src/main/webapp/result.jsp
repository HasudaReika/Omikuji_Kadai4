<%@page import="omikuji4.Omikuji"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>おみくじ結果</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
	<div class="container">
		<div class="box">
		<p>今日の運勢は ${omikuji.getFortuneName()} です<br>
			願い事： ${omikuji.getNegaigoto()}<br>
			商い： ${omikuji.getAkinai()}<br>
			学問： ${omikuji.getGakumon()}
		</p>
		
		</div>
		<p>おみくじを続けますか？</p>
		<input type="button"
			onclick="location.href='http://localhost:8080/omikuji_kadai4/omikuji'"
			value="続ける" />
	</div>
</body>
</html>