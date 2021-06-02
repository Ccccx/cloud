function upload() {
    var formData = new FormData();
    formData.append('file', $('#file')[0].files[0]);
    $.ajax({
        url:'http://127.0.0.1:8080/uploadFile',
        type: 'POST',
        processData: false,
        contentType: false,
        data:   formData,
        xhr:function(){
            var xhr = new XMLHttpRequest();
            if(xhr.upload){
                xhr.upload.addEventListener('progress',function(e){
                    var loaded = e.loaded;//已经上传大小情况
                    var tot = e.total;//附件总大小
                    var process = loaded/tot
                    process = Math.floor(process*10000)/100;
                    //设置进度显示百分比
                    $("#progress").html( process +"%" );
                    //设置完成的进度条宽度
                    $("#progress").css("width" , process +"%");
                    // 设置样式
                    if (process <= 25) {
                        $("#progress").attr("class" , 'progress-bar progress-bar-striped active progress-bar-info');
                    } else if (process <= 50) {
                        $("#progress").attr("class" , 'progress-bar progress-bar-striped active progress-bar-warning');
                    } else if (process <= 75) {
                        $("#progress").attr("class" , 'progress-bar progress-bar-striped active progress-bar-danger');
                    } else {
                        $("#progress").attr("class" , 'progress-bar progress-bar-striped active progress-bar-success');
                    }
                }, false);
            }
            return xhr;
        },
        success:function(data){
            console.log(data);
            console.log("上传成功!!!!");
        },
        error:function(){
            console.log("上传失败！");
        }
    });
}