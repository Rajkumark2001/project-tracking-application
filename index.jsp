<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login Form</title>
    <link rel="stylesheet" href="LoginRegister.css">
    <script src="script.js"> </script>
    <style>
        .password-container {
            position: relative;
            width: 100%;
        }

        .password-input {
            width: 90%;
            padding: 8px;
            margin-bottom: 16px;
        }

        .toggle-password {
            position: absolute;
            right: 5px;
            top: 50%;
            transform: translateY(-50%);
            cursor: pointer;
        }
    </style>
    <script>
        
        function togglePassword() {
            var passwordInput = document.getElementById("password");
            var toggleButton = document.getElementById("toggleButton");

            if (passwordInput.type === "password") {
                passwordInput.type = "text";
                toggleButton.innerHTML = "Hide";
            } else {
                passwordInput.type = "password";
                toggleButton.innerHTML = "Show";
            }
        }
        
    </script>
</head>
<body onload="load()">
    <script>
        function load(){
            let str='<%=(String)request.getAttribute("message") %>';
            if(str!='null'){
                alert(str);
            }
        }
    </script>
    <div class="container">
        <h2>Login</h2>
        <form action="UserHandler" method="post">
           
            <label for="username">Email:</label>
            <input type="text" id="username" name="username" required>
            <div class="password-container">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" class="password-input" required>
                <span class="toggle-password" id="toggleButton" onclick="togglePassword()">Show</span>
            </div>
         <input type="hidden" name="action" value="login">
            <button type="submit">Login</button>
        </form>
        <p>Don't have an account? <a href="Register.jsp">Register here</a>.</p>
    </div>
</body>
</html>