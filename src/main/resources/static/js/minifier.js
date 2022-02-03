$(document).ready(function () {

    $("#submitBtn").click(function (e) {
        e.preventDefault();
        $.ajax({
            type: "POST",
            url: "/api/v1/create",
            contentType: "application/json; charset=utf-8",
            dataType: 'json',
            data: JSON.stringify({url: $("#floatingInput").val()}),
            success: function (data) {
                jQuery('#outputUrl').val(data.url);
                jQuery('#outputImage').attr('src','data:image/png;base64,' + data.image);
                jQuery('#floatingInput').val('');
                jQuery('#responseModal').modal('show');
            }
        });

    });


    $("#btnToClipboard").click(function (e) {
        e.preventDefault();
        if (navigator && navigator.clipboard && navigator.clipboard.writeText)
            navigator.clipboard.writeText($("#outputUrl").val());
    });

});
