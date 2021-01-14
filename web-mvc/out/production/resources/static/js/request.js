function loadData() {
    init();
    getUser();
    listUser();
}

function getUser() {
    $.get("/user", function (data) {
        $("#userName").val(data.userName);
    });
}

function listUser() {
    $.get("/user/listUser", function (data) {
        $(".user-list").remove();
        for (var i = 0, len = data.length; i < len; i++) {
            $("#user-list").append("<button type=\"button\" class=\"list-group-item user-list\" onclick=\"selectUser('" + data[i].userName + "')\">" + data[i].userName + "</button>");
        }
    });
}