$(document).ready(function () {

    $(".users-listing-section .request-btn").on("click", function (event) {

        $(this).addClass("send");
        $(this).find(".request").addClass("d-none");
        $(this).find(".send").removeClass("d-none").addClass("d-block");
        return false;
    });


    // be studdyBuddy or find studybuddy:

    $(".yes-btn").on("click", function () {
        $(".about-being .yes-about").removeClass("d-none");
        $(".about-being").removeClass("invisible");
        $(".about-being .no-about").addClass("d-none");
        return false;
    });
    $(".no-btn").on("click", function () {
        $(".about-being").removeClass("invisible");
        $(".about-being .no-about").removeClass("d-none");
        $(".about-being .yes-about").addClass("d-none");
        return false;
    });


});