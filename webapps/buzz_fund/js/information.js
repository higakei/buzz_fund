function getPrediction(form, action) {

	form.button.disabled = true;

	try {
		var params = new Object();
		params["date"] = form.date.value;

		$("#prediction").load(action, params
			,function(responseText, status, xmlhttp) {
				if (status != "success") {
					if (xmlhttp.status == 500) {
						alert(responseText);
					} else if (xmlhttp.status == 503) {
						alert("サーバー停止中です");
					} else {
						alert("エラーが発生しました" + "(" + xmlhttp.status + ")");
					}
				}
				form.button.disabled = false;
			}
		);
	} catch (exception) {
		form.button.disabled = false;
	}
}

function getRetracement(form, action) {

	form.button.disabled = true;

	try {
		var params = new Object();
		params["from"] = form.from.value;
		params["to"] = form.to.value;

		$("#retracement").load(action, params
			,function(responseText, status, xmlhttp) {
				if (status != "success") {
					if (xmlhttp.status == 500) {
						alert(responseText);
					} else if (xmlhttp.status == 503) {
						alert("サーバー停止中です");
					} else {
						alert("エラーが発生しました");
					}
				}
				form.button.disabled = false;
			}
		);
	} catch (exception) {
		form.button.disabled = false;
	}
}
