function submitLogin() {
	var username = $("#username").val();
	var password = $("#password").val();
	if( username=="") {
		alert("请输入用户名");
		return;
	}
	if(password="") {
	   alert("请输入密码");
	   return;
	}
	$(".cy_login_form").submit();
	
}