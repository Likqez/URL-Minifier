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
                jQuery('#outputImage').attr('src', 'data:image/png;base64,' + data.image);
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


    $("#printBtn").click(function () {

        const sizeQR = 500
        const nQR = $("#nQR").val();
        const srcQR = $("#outputImage").attr("src");
        const gap = 0

        $("body").append('<div id="printme"> </div>');
        const element = $("#printme")

        element.css({
            "position": "absolute",
            "display": "grid",
            "grid-template-columns": `repeat(${Math.floor(2480 / sizeQR)}, ${sizeQR}px)`,
            "grid-template-rows": "auto",
            "gap": "25px"
        });

        for (let i = 0; i < nQR; i++) {
            element.append(`<img class="border border-5" src="${srcQR}" width="${sizeQR}" height="${sizeQR}" alt="">`)
        }

        element.printThis({afterPrint: $("#printme").remove()});
    });


});
