
function showMessage(event) {
    $('.alert-message').removeClass('hidden');
    var message = "";
    if (event.hasOwnProperty('error')) {
        message = event.error.message;
    } else if (event.hasOwnProperty('statusText')) {
        message = "Connection Refused! Unable to connect to the server.";
    }
    var alert = "<div class='alert alert-danger alert-dismissible fade show'><strong id='message-type'>Error! </strong>\n\
                    <label id='message-info'>" + message + "</label>\n\
                    <button type='button' class='close close-message' data-dismiss='alert'>&times;</button></div>";

    $('.alert-message').html(alert);
}

$(function ()
{
    $(document).on('click', '.close-message', function (e)
    {
        hideMessage();
    });
});

function hideMessage() {
    $('.alert-message').addClass('hidden');
}

$.ajaxSetup({
    beforeSend: function () {
        $('#loader').show();
    },
    complete: function () {
        $('#loader').hide();
    },
    success: function () {
        $('#loader').hide();
    },
    error: function () {
        $('#loader').hide();
    }
});