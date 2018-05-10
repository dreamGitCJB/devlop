<!doctype html>
<html>
<head>
<meta charset="UTF-8">
<title>管理后台登陆</title>
<link rel="stylesheet" type="text/css" href="css/admin_login.css">
</head>

<body class="login_wrap">
     <div class="content">
        <div class="logo"></div>
        <div class="login_bg">
         <div class="login_title">管理后台登陆</div>
         <form class="cy_login_form" action="/Admin?adminLogin" method="post">
           <input type="hidden" name="step" value="2">
           <input id="username" type="text" class="cy_username" placeholder="用户名" name="username" required>
           <input id="password" type="password"  class="cy_username" placeholder="密码" name="password" required>
           <!--<div class="right">
              <a href="#" class="forget_psw" >忘记密码</a>
           </div>-->
           <button type="button"  onclick="submitLogin();" class="cy_login_button">登陆</button>
         </form>
         
        </div>
       
     </div>
</body>
<script src="js/jquery/jQuery-2.2.0.min.js"></script>
<script src="js/admin_login.js"></script>
</html>
