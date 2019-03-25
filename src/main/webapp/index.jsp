<html>
<head>
    <script type="text/javascript" src="paper-full.min.js"></script>
    <title>WebSocket Test</title>
    <link href="https://fonts.googleapis.com/css?family=Nunito:400,600,700" rel="stylesheet">
    <link rel="stylesheet" href="style.css"/>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
</head>
<body>


<div id="login_div" style="position: absolute" class="main-div">
    <h3 style="color: white">Welcome to Escape</h3>
    <p style="color: white">Please login</p>
    <input class="input" type="email" placeholder="Email..." id="email_field"/>
    <input class="input" type="password" placeholder="Password..." id="password_field"/>
    <button class="button" onclick="login()">Login to Account</button>
    <button class="button" onclick="document.getElementById('id01').style.display='block'">Sign Up</button>
</div>

<div id="user_div" style="width: 100%; height: 100%; margin: auto; position: absolute" class="container">
    <div class="row clearfix">
        <div class="col-md-12 column">
            <ul>
                <li><a href="#How to play">How to play</a></li>
                <li><a href="#Setting">Setting</a></li>
                <li><a href="#contact us">Contact us</a></li>
                <li><a href="#about us">About</a></li>
                <li style="float:right;"><a href="#logout" onclick="logout()">Logout</a></li>
            </ul>
        </div>
    </div>

    <div class="row" style="width: 100%">

        <div class="col-sm-4">
            <h1 style="color: white" class="welcomeMessage" style="font-style:oblique;">Welcome to Get Out!</h1>
            <br>
            <h4 style="color: white" id="user_para">Loading...</h4>

        </div>

        <div class="col-sm-5">

        </div>


        <div class="col-sm-3"
             style="filter:alpha(Opacity=80);-moz-opacity:0.5;opacity: 0.5;z-index:100;">
            <table class="table table-dark">
                <thead>
                <tr>
                    <th style="color: darkgray" scope="col">#</th>
                    <th style="color: darkgray" scope="col">Name</th>
                    <th style="color: darkgray" scope="col">Score</th>
                    <!-- <th scope="col">Handle</th> -->
                </tr>
                </thead>
                <tbody>
                <tr>
                    <th style="color: darkgray" scope="row">1</th>
                    <td style="color: gold" id="hscoren1">Loading</td>
                    <td style="color: burlywood" id="hscores1">Loading</td>
                </tr>
                <tr>
                    <th style="color: darkgray" scope="row">2</th>
                    <td style="color: silver" id="hscoren2">Loading</td>
                    <td style="color: burlywood" id="hscores2">Loading</td>
                </tr>
                <tr>
                    <th style="color: darkgray" scope="row">3</th>
                    <td style="color: saddlebrown" id="hscoren3">Loading</td>
                    <td style="color: burlywood" id="hscores3">Loading</td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>

    <div class="row clearfix">
        <div class="col-md-3 column">
            <h3 style="color: white" id="hi">Your avatar's current status: </h3>
            <h4 style="color: beige" id="user_level">Level: Loading</h4>
            <h4 style="color: beige" id="user_current_score">Current score: Loading</h4>
            <h4 style="color: beige" id="user_highest_score">Highest score: Loading</h4>
            <br>
            <h4 style="color: white" id="user_current_health">Your current health: Loading</h4>
            <h4 style="color: white" id="user_room_reference">Your are in room: Loading</h4>
            <h4 style="color: greenyellow">Your current equipments:</h4>
            <h5 style="color: white" id="user_current_equipment_slot_1">&nbsp &nbsp &nbsp L: Loading</h5>
            <h5 style="color: white" id="user_current_equipment_slot_2">&nbsp &nbsp &nbsp R: Loading</h5>
        </div>
        <div class="col-md-3 column" style="float:right;">
            <h3 style="color: black" id="resetpsw1">Reset password here: </h3>
            <input class="input" type="text" id="resetpsw_email_field" placeholder="Email..." type="text"/>
            <input class="input" type="password" id="resetpsw_oldpass_field" placeholder="Old password..." type="text"/>
            <input class="input" type="password" id="resetpsw_newpass_field" placeholder="New password..."/>
            <button class="startButton"
                    style="{display: inline-block;width: 200px;padding: 15px 25px;font-size: 24px;cursor: pointer;text-align: center;text-decoration: none;outline: none;color: #fff;background-color: #FFA07A;border: none;border-radius: 15px;box-shadow: 0 9px #999}"
                    onclick="changePassword()">Change password!
            </button>
        </div>
        <div class="col-md-12 column" class="blockquote-reverse">
            <button class="startButton"
                    style="{display: inline-block;width: 200px;padding: 15px 25px;font-size: 24px;cursor: pointer;text-align: center;text-decoration: none;outline: none;color: #fff;background-color: #FFA07A;border: none;border-radius: 15px;box-shadow: 0 9px #999}"
                    onclick="wake()">Wake up!
            </button>
        </div>
    </div>
</div>


<div id="PlayGame" style="position: absolute; display: none; width: 100%; height: 100%" class="container">
    Welcome<br/><input id="text" type="text"/>
    <button onclick="send()">Send Command</button>
    <hr/>
    <button onclick="closeWebSocket()">Disconnect from the server</button>
    <button onclick="sleep()">Sleep</button>
    <hr/>
    <div id="message"></div>
    <div>
        <canvas id="canvasHex" width="800" height="600"></canvas>
        <canvas id="canvasCoordinates" width="800" height="600"></canvas>
    </div>

</div>


<div id="id01" class="modal">
    <span onclick="document.getElementById('id01').style.display='none'" class="close"
          title="Close Modal">&times;</span>
    <div class="modal-content">
        <div class="container">
            <%--@declare id="email"--%><%--@declare id="psw"--%><%--@declare id="psw-repeat"--%><h1>Sign Up</h1>
            <p>Please fill in this form to create an account.</p>
            <hr>
            <label id="email"><b>Email</b></label>
            <input class="input" type="text" placeholder="Enter Email" id="signup_email_field" name="email" required>

            <label for="psw"><b>Password</b></label>
            <input class="input" type="password" placeholder="Enter Password" id="signup_pass_field" name="psw"
                   required>

            <label for="psw-repeat"><b>Repeat Password</b></label>
            <input class="input" type="password" placeholder="Repeat Password" name="psw-repeat" required>

            <label>
                <input class="input" type="checkbox" checked="checked" name="remember" style="margin-bottom:15px">
                Remember me
            </label>

            <!-- <p>By creating an account you agree to our <a href="#" style="color:dodgerblue">Terms & Privacy</a>.</p> -->

            <div class="clearfix">
                <button class="button" type="button" onclick="document.getElementById('id01').style.display='none'"
                        class="cancelbtn">Cancel
                </button>
                <button class="button" type="submit" class="signupbtn" onclick="signup()">Sign Up</button>
            </div>
        </div>
    </div>
</div>

<script>
    // Get the modal
    var modal = document.getElementById('id01');

    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function (event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }
</script>

<script src="https://www.gstatic.com/firebasejs/5.8.6/firebase.js"></script>
<script>
    // Initialize Firebase
    var config = {
        apiKey: "AIzaSyDZ1bM0znAMeH4EjeAuiHky0nXh6Oic_rQ",
        authDomain: "escape-14d86.firebaseapp.com",
        databaseURL: "https://escape-14d86.firebaseio.com",
        projectId: "escape-14d86",
        storageBucket: "escape-14d86.appspot.com",
        messagingSenderId: "239461510231"
    };
    firebase.initializeApp(config);
</script>
<script src="websocket.js"></script>
<script src="account.js"></script>
<script type="text/javascript" src="scripto.js"></script>

</body>
</html>