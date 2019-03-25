// Initialize Firebase

firebase.auth().onAuthStateChanged(function (user) {
    if (user) {
        // User is signed in.
        document.getElementById("user_div").style.display = "block";
        document.getElementById("login_div").style.display = "none";
        document.getElementById("PlayGame").style.display = "none";
        var email_id = user.email;
        var tmp = "00001#" + user.uid + "#" + email_id;
        websocket.send(tmp);
    } else {
        // No user is signed in.
        document.getElementById("login_div").style.display = "block";
        document.getElementById("user_div").style.display = "none";
        document.getElementById("PlayGame").style.display = "none";
    }
});

function changePassword(){
    userEmail = document.getElementById("resetpsw_email_field").value;
    userOldPass = document.getElementById("resetpsw_oldpass_field").value;
    userNewPass = document.getElementById("resetpsw_newpass_field").value;
    var user = firebase.auth().currentUser;
    if(user.email === userEmail) {
        var credential = firebase.auth.EmailAuthProvider.credential(
            userEmail,
            userOldPass
        );
        user.reauthenticateAndRetrieveDataWithCredential(credential).then(function() {
            // User re-authenticated.
        }).catch(function(error) {
            // An error happened.
        });
        user.updatePassword(userNewPass).then(function () {
            alert("Success! You will stay logged in.");
        }).catch(function (error) {
            alert(error);
            // An error happened.
        });
    }
}


function signup() {
    userEmail = document.getElementById("signup_email_field").value;
    userPass = document.getElementById("signup_pass_field").value;
    console.log(userEmail);
    alert("This site requires anonymous cookies and various 3rd-party services to function properly. To continue using Escape, you must consent to our Cookie & Privacy policies. Hit OK to continue.");


    firebase.auth().createUserWithEmailAndPassword(userEmail, userPass).catch(function (error) {
        // Handle Errors here.
        var errorCode = error.code;
        var errorMessage = error.message;
        // [START_EXCLUDE]
        if (errorCode === 'auth/weak-password') {
            alert('The password is too weak.');
        } else {
            alert(errorMessage);
        }
        console.log(error);
        // [END_EXCLUDE]
    });

    document.getElementById('id01').style.display='none';
}

function login() {

    var userEmail = document.getElementById("email_field").value;
    var userPass = document.getElementById("password_field").value;

    firebase.auth().signInWithEmailAndPassword(userEmail, userPass).catch(function (error) {
        // Handle Errors here.
        var errorCode = error.code;
        var errorMessage = error.message;


        window.alert("Error : " + errorMessage);

        // ...
    });


}

function logout() {
    firebase.auth().signOut();
}
