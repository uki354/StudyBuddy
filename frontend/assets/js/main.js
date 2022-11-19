$(document).ready(function () {

    $(".users-listing-section .request-btn").on("click", function () {
        $(this).addClass("send");
        $(this).find(".request").addClass("d-none");
        $(this).find(".send").removeClass("d-none").addClass("d-block");
    });


});