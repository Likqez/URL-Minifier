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
        let nQR = $("#nQR").val();
        let srcQR = $("#outputImage").attr("src");
        const gap = 0

        $("body").append('<div id="printme"> </div>');

        this.css({
            "position": "absolute",
            "display": "grid",
            "grid-template-columns": `repeat(${Math.floor(2480 / sizeQR)}, ${sizeQR}px)`,
            "grid-template-rows": "auto",
            "gap": `${gap}px`
        });

        for (let i = 0; i < nQR; i++) {
            this.append(`<img class="border border-5" src="${srcQR}" width="${sizeQR}" height="${sizeQR}" alt="">`)
        }

        this.printThis({afterPrint: $("#printme").remove()});
    });

    let fileList = [];

    $("#overlayImg").on('change', async function () {
        fileList = this.files;
        let file64 = fileList[0] === undefined ? "" : await toBase64(fileList[0]);
        let color = $("#qrColor").val();
        console.log(file64);
        console.log(color);
        await refreshCode(file64, color);
    });

    $("#qrColor").on('change', async function () {
        let file64 = fileList[0] === undefined ? "" : await toBase64(fileList[0]);
        let color = $(this).val();
        console.log(file64);
        console.log(color);
        await refreshCode(file64, color);
    });

});

const toBase64 = file => new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result.split(',')[1]);
    reader.onerror = error => reject(error);
});

async function refreshCode(overlay64, color) {
    $.ajax({
        type: "POST",
        url: "/api/v1/qrcode",
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        data: JSON.stringify({file: overlay64, url: $("#outputUrl").val(), color: color}),
        success: function (data) {
            jQuery('#outputImage').attr('src', 'data:image/png;base64,' + data.image);
        }
    });
}
