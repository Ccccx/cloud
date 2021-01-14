// 设置 STOMP 客户端
var stompClient = null;
// 设置 WebSocket 进入端点
var SOCKET_ENDPOINT = "/gs-guide-websocket";

// 设置订阅消息的请求前缀
var SUBSCRIBE_PREFIX = "/topic"
// 设置订阅消息的请求地址
var PUBLIC_SUBSCRIBE = SUBSCRIBE_PREFIX + "/public";
// 在线用户列表订阅地址
var ONL_USER = SUBSCRIBE_PREFIX + "/users"
// 用户私有订阅地址
var PRIVATE_SUBSCRIBE = "/user/queue/userMsg";

// 设置服务器端点，访问服务器中哪个接口
var SEND_ENDPOINT = "/app/test";

function init() {
    connect();
}


/* 进行连接 */
function connect() {
    // 设置 SOCKET
    var socket = new SockJS(SOCKET_ENDPOINT);
    // 配置 STOMP 客户端
    stompClient = Stomp.over(socket);
    // STOMP 客户端连接
    stompClient.connect({}, function (frame) {
        console.log("连接成功");
        subscribeSocket();
    });
}

/* 订阅公共信息 */
function subscribeSocket() {
    // 订阅公共消息
    stompClient.subscribe(PUBLIC_SUBSCRIBE, function (responseBody) {
        var receiveMessage = JSON.parse(responseBody.body);
        $("#public-information").append("<tr><td>" + receiveMessage.userName + " > " + receiveMessage.content + "</td></tr>");
    });

    // 订阅在线用户
    stompClient.subscribe(ONL_USER, function (responseBody) {
        var data = JSON.parse(responseBody.body);
        $(".user-list").remove();
        for (var i = 0, len = data.length; i < len; i++) {
            $("#user-list").append("<button type=\"button\" class=\"list-group-item user-list\" onclick=\"selectUser('" + data[i].userName + "')\">" + data[i].userName + "</button>");
        }
    });

    // 订阅用户私有信息
    stompClient.subscribe(PRIVATE_SUBSCRIBE, function (responseBody) {
        var receiveMessage = JSON.parse(responseBody.body);
        $("#private-information").append("<tr><td>" + receiveMessage.userName + " > " + receiveMessage.content + "</td><td/></tr>");
    });
    // 输出订阅地址
    console.log("公共地址：" + PUBLIC_SUBSCRIBE);
    console.log("在线用户: " + ONL_USER);
    console.log("私有订阅: " + PRIVATE_SUBSCRIBE);

    console.log("查看在线用户")
    sendConnect();
}

/* 断开连接 */
function disconnect() {
    stompClient.disconnect(function () {
        alert("断开连接");
    });
}

function selectUser(receiveUser) {
    console.log("selectUser : " + receiveUser);
    $("#receiveUser").val(receiveUser);
}

function sendConnect() {
    // 设置待发送的消息内容
    var message = '{"userName": "test"}';
    // 发送消息
    stompClient.send("/app/connect", {}, message);
}

/* 发送消息并指定目标地址（这里设置的目标地址为自身订阅消息的地址，当然也可以设置为其它地址） */
function sendMessageNoParameter() {
    var userName = $("#userName").val()
    var receiveUser = $("#receiveUser").val()
    var name = (receiveUser == null ? userName : receiveUser);
    // 设置发送的内容
    var sendContent = $("#content").val();
    // 设置待发送的消息内容
    var message = '{"destination": "' + PUBLIC_SUBSCRIBE + '", "content": "' + sendContent + '", "userName": "' + name + '"}';
    $("#private-information").append("<tr><td/><td>" + sendContent + " < " + userName + "</td></tr>");
    // 发送消息
    stompClient.send(SEND_ENDPOINT, {}, message);
}

