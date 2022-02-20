$(document).ready(function () {

    $('#account-table').find('a').each(function (idx, a) {
        $(a).click(function () {
            handleSelectAccount(a.innerText)
        });
    });

    var table = $('#account-table').DataTable({
        drawCallback: function (settings) {
            var pagination = $(this).closest('.dataTables_wrapper').find('.dataTables_paginate');
            pagination.toggle(this.api().page.info().pages > 1);
            var length = $(this).closest('.dataTables_wrapper').find('.dataTables_length');
            length.toggle(this.api().page.info().pages > 1);
            var info = $(this).closest('.dataTables_wrapper').find('.dataTables_info');
            info.toggle(this.api().page.info().pages > 1);
        }
    });

    $('#div-find-account-modal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var recipient = button.data('input');
        // Reset search
        table.search('').columns().search('').draw();

        window.handleSelectAccount = function (accountID) {
            $('#' + recipient).val(accountID);
            $('#div-find-account-modal').modal('hide');
        }
    });
});