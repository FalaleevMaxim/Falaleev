
$(document).ready(function() {
    $('#easy').click(function () {
        $("#tr1").val('10');
        $("#td1").val('10');
        $("#bomb").val('10');
        $("#tr1").attr("disabled", "disabled");
        $("#td1").attr("disabled", "disabled");
        $("#bomb").attr("disabled", "disabled");
    });
    $('#normal').click(function () {
        $("#tr1").val('16');
        $("#td1").val('16');
        $("#bomb").val('40');
        $("#tr1").attr("disabled", "disabled");
        $("#td1").attr("disabled", "disabled");
        $("#bomb").attr("disabled", "disabled");
    });
    $('#hard').click(function () {
        $("#tr1").val('16');
        $("#td1").val('30');
        $("#bomb").val('99');
        $("#tr1").attr("disabled", "disabled");
        $("#td1").attr("disabled", "disabled");
        $("#bomb").attr("disabled", "disabled");
    });
    $('#custom').click(function () {
        $("#tr1").removeAttr("disabled");
        $("#td1").removeAttr("disabled");
        $("#bomb").removeAttr("disabled");
    });
    $('#frm').submit(function (e) {
        $("#tr1").removeAttr("disabled");
        $("#td1").removeAttr("disabled");
        $("#bomb").removeAttr("disabled");
    })
});

