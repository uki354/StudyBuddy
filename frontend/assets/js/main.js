$(document).ready(function () {

    $(".request-btn").on("click", function () {
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


    // https://app-studdy-buddy.herokuapp.com/api/


    // let xhttp = new XMLHttpRequest();
    //
    // xhttp.onreadystatechange = function (){
    //     if( this.readyState == 4 && this.status == 200){
    //
    //         console.log(this.responseText);
    //
    //     }
    // }
    //
    // xhttp.open("GET", "https://app-studdy-buddy.herokuapp.com/api/", true );
    // xhttp.send();


//     var jqxhr = $.ajax( "https://app-studdy-buddy.herokuapp.com/api/" )
//         .done(function() {
//             alert( "success" );
//         })
//         .fail(function() {
//             alert( "error" );
//         })
//         .always(function() {
//             alert( "complete" );
//         });
//
// // Perform other work here ...
//
// // Set another completion function for the request above
//     jqxhr.always(function() {
//         alert( "second complete" );
//     });


    // $.postJSON = function(url,data, callback) {
    //     return jQuery.ajax({
    //         headers: {
    //             'Accept': 'application/json',
    //             'Content-Type': 'application/json'
    //         },
    //         'type': 'POST',
    //         'url': url,
    //         'data': JSON.stringify(data),
    //         'dataType': 'json',
    //         'success': callback
    //     });
    // };


    // Registration
    $("#register-btn").on("click", function () {
        var email = $("#email").val();
        var name = $("#name").val();
        var lastname = $("#lastname").val();
        var university = $("#faculty").val();
        var birth = $("#start").val();
        var password = $("#password").val();


        var register = {
            "email": email,
            "name": name,
            "lastname": lastname,
            "university": university,
            "password": password,
            "birthdate": birth,
        }

        console.log(register);

        $.ajax({
            url: 'https://app-studdy-buddy.herokuapp.com/api/user/create',
            type: 'post',
            contentType: 'application/json',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(register),
            success: function (data) {
                console.log(data);
            }
        });
    })


    // Login

    $("#login-btn").on("click", function () {

        var email = $("#email").val();
        var password = $("#password").val();

        var login = {
            "email": email,
            "password": password
        }

        $.ajax({
            url: 'https://app-studdy-buddy.herokuapp.com/api/user/login',
            type: 'post',
            contentType: 'application/json',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(login),
            success: function (data) {
                console.log(data);
            }
        });

    })


});