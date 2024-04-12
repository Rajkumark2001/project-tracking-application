<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registration Form</title>
    <link rel="stylesheet" href="LoginRegister.css">
    <script>
        function validateForm() {
            var username = document.getElementById("username").value;
            var password = document.getElementById("password").value;
            var confirmPassword = document.getElementById("confirmPassword").value;
            var email = document.getElementById("email").value;

         
            var emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailPattern.test(email)) {
                alert("Please enter a valid email address");
                return false;
            }
            if (username === "" || password === "" || confirmPassword === "") {
                alert("All fields must be filled out");
                return false;
            }

            if (password !== confirmPassword) {
                alert("Passwords do not match");
                return false;
            }

            return true;
        }
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

        
        function load(){
            let str='<%=(String)request.getAttribute("message") %>';
            if(str!='null'){
                alert(str);
            }
   
        }
    </script>
</head>
<body onload="load()">
    <div class="container">
        <h2>Register</h2>
        <form action="UserHandler" method="post" onsubmit="return validateForm()">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" required>

            <label for="email">Email:</label>
            <input type="text" id="email" name="email" required>
            <div class="password-container">
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required>
            <span class="toggle-password" id="toggleButton" onclick="togglePassword()">Show</span>

            <label for="confirmPassword">Confirm Password:</label>
            <input type="password" id="confirmPassword" name="confirmPassword" required>
            </div>
            <input type="hidden" name="action" value="register">
            <button type="submit">Register</button>
        </form>
        <p>Already have an account? <a href="index.jsp">Login here</a>.</p>
    </div>
</body>
</html>
