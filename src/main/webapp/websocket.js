var websocket = null;
//判断当前浏览器是否支持WebSocket
if ('WebSocket' in window) {
    websocket = new WebSocket("ws://localhost:8080/sws");
} else {
    alert(' Not support websocket');
}


//连接发生错误的回调方法
websocket.onerror = function () {
    setMessageInnerHTML("WebSocket error");
};

//连接成功建立的回调方法
websocket.onopen = function () {
    setMessageInnerHTML("WebSocket successfully connected");
};

//接收到消息的回调方法
websocket.onmessage = function (event) {
    setMessageInnerHTML(event.data);
};

//连接关闭的回调方法
websocket.onclose = function () {
    setMessageInnerHTML("WebSocket connection closed");
};

//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
window.onbeforeunload = function () {
    closeWebSocket();
};

//将消息显示在网页上
function setMessageInnerHTML(innerHTML) {
    document.getElementById('message').innerHTML += innerHTML + '<br/>';
}

//关闭WebSocket连接
function closeWebSocket() {
    websocket.close();
}

//发送消息
function send() {
    var message = document.getElementById('text').value;
    websocket.send(message);
}

function sleep() {
    websocket.send('00013');
    document.getElementById("login_div").style.display = "none";
    document.getElementById("user_div").style.display = "block";
    document.getElementById("PlayGame").style.display = "none";
}

function wake() {
    var message = '00012';
    websocket.send(message);
    document.getElementById("login_div").style.display = "none";
    document.getElementById("user_div").style.display = "none";
    document.getElementById("PlayGame").style.display = "block";
}
