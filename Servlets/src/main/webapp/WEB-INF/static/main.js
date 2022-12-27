$(function(){

    $(document).on('click', '.confirm-button', function(){
        var link = $(this);
        var activityId = link.data('id');
        $.ajax({
            method: "PUT",
            url: "/active",
            data: activityId,
            success: function()
            {
                link.remove();
            },
            error: function(response)
            {
                if(response.status == 404) {
                    alert('Activity not found');
                }
            }
        });
        return false;
    });

    // //Adding book
    // $('#save-book').click(function()
    // {
    //     var data = $('#book-form form').serialize();
    //     console.log(data);
    //     $.ajax({
    //         method: "POST",
    //         url: '/books/',
    //         data: data,
    //         success: function(response)
    //         {
    //             $('#book-form').css('display', 'none');
    //             var book = {};
    //             book.id = response;
    //             var dataArray = $('#book-form form').serializeArray();
    //             for(i in dataArray) {
    //                 book[dataArray[i]['name']] = dataArray[i]['value'];
    //             }
    //             appendBook(book);
    //         }
    //     });
    //     return false;
    // });

});