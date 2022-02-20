$(document).ready(function () {
    var sortDef = [0, 'desc'];
    $('#transfer-table').DataTable({
        drawCallback: function (settings) {
            var pagination = $(this).closest('.dataTables_wrapper').find('.dataTables_paginate');
            pagination.toggle(this.api().page.info().pages > 1);
            var length = $(this).closest('.dataTables_wrapper').find('.dataTables_length');
            length.toggle(this.api().page.info().pages > 1);
            var info = $(this).closest('.dataTables_wrapper').find('.dataTables_info');
            info.toggle(this.api().page.info().pages > 1);
        },
        'order': [sortDef]
    });

    $('.radio').find('input').each(function (idx, radio) {
        var accountID = radio.value;
        radio.onclick = function () {
            window.location.href = '/?accID=' + accountID;
        };
    });
});