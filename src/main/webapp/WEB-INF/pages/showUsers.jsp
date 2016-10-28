
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html >
<html>
<head>

<title>Insert title here</title>
</head>
<body>
<h1>showUsers</h1>
${users.size()}

<select>
	<c:forEach var="user" items="${users}">
		<option value ="${user.id}">${user.firstName}</option>
	</c:forEach>
</select>

</body>
</html>