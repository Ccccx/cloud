<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="ISO-8859-1">
    <title>Insert title here</title>
</head>
<body>
<p>
    Hello World! Time is <%= new java.util.Date() %>
</p>

<p>
    We are running on  <%= application.getServerInfo() %>!!!
</p>

</body>
</html>