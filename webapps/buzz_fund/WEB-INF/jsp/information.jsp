<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="jp.co.hottolink.buzzfund.web.servlet.util.HttpUtils" %>
<jsp:useBean id="prediction" class="java.util.HashMap" scope="request" />
<jsp:useBean id="retracementPeriod" class="java.util.HashMap" scope="request" />
<jsp:useBean id="retracement" class="java.util.HashMap" scope="request" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>buzz fund - 情報提供ページ</title>
<base href="<%= HttpUtils.getContextURL(request) %>/" />
<script type="text/javascript" src="js/information.js"></script>
<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/jquery.dimensions.js"></script>
<script type="text/javascript" src="js/ui.datepicker.js"></script>
<script type="text/javascript" src="js/input_ui.js"></script>
<link rel="stylesheet" type="text/css" href="js/datepicker/ui.datepicker.css" media="screen, print" />
</head>
<body onload="setSingleCalender('prediction_date'); setDoubleCalender('retracement_period', 'retracement_period_from', 'retracement_period_to');">
	<p>指数</p>
	<form name="predictionForm">
		<table><tr><td>日付</td>
		<td><input class="prediction_date" type="text" name="date" value="<fmt:formatDate value="${prediction.predictdate}" pattern="yyyy-MM-dd" />" readonly="readonly" /></td>
		<td><input type="button" name="button" value="表示" onclick="getPrediction(this.form, 'action/prediction');" /></td></tr></table>
	</form>

	<div id ="prediction">
		<table width="400"><tr><td colspan="2"><fmt:formatDate value="${prediction.predictdate}" pattern="yyyy-MM-dd" /></td></tr>
		<tr><td width="90">クチコミ指数</td><td width="310"><c:out value="${prediction.クチコミ指数}" /></td></tr>
		<tr><td>価格指数</td><td><c:out value="${prediction.価格指数}" /></td></tr>
		<tr><td>出来高指数</td><td><c:out value="${prediction.出来高指数}" /></td></tr>
		<tr><td>総合指数</td><td><c:out value="${prediction.総合指数}" /></td></tr>
		<tr><td>許容偏差</td><td><c:out value="${prediction.losscut}" /></td></tr>
		<tr><td colspan="2"><font color="#ff0000"><c:out value="${prediction.notice}" /><font color="#ff0000"></td></tr></table>
	</div>

	<p>節目</p>
	<form name="retracementForm">
		<table><tr><td>from</td><td><input id="retracement_period_from" class="retracement_period" type="text" name="from" value="<fmt:formatDate value="${retracementPeriod.from}" pattern="yyyy-MM-dd"  />" readonly="readonly" /></td>
		<td>to</td><td><input id="retracement_period_to" class="retracement_period" type="text" name="to" value="<fmt:formatDate value="${retracementPeriod.to}" pattern="yyyy-MM-dd" />" readonly="readonly" /></td>
		<td><input type="button" name="button" value="表示" onclick="getRetracement(this.form, 'action/retracement');"/></td><tr></table>
	</form>

	<div id ="retracement">
		<table><tr><td colspan="2"><fmt:formatDate value="${retracement.from}" pattern="yyyy-MM-dd" /> - <fmt:formatDate value="${retracement.to}" pattern="yyyy-MM-dd" /></td></tr>
		<tr><td>
			<table><c:forEach var="highToLow" items="${retracement.highToLow}">
			<tr><td><c:if test="${highToLow.ratio eq 0}">Max</c:if><c:if test="${highToLow.ratio ne 0}"><c:out value="${highToLow.label}" /></c:if></td>
			<td><c:out value="${highToLow.retracement}" /></td></tr>
			</c:forEach></table>
		</td><td>
			<table><c:forEach var="lowToHigh" items="${retracement.lowToHigh}">
			<tr><td><c:if test="${lowToHigh.ratio eq 0}">Min</c:if><c:if test="${lowToHigh.ratio ne 0}"><c:out value="${lowToHigh.label}" /></c:if></td>
			<td><c:out value="${lowToHigh.retracement}" /></td></tr>
			</c:forEach></table>
		</td></tr></table>
	</div>

</body>
</html>