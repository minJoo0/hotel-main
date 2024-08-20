$(document).ready(function() { // DOM이 완전히 로드되었을 때 실행
    var $hotelSelect = $('#hotelNo'); // 'hotelNo' id를 가진 요소 선택
    var $storeSelect = $('#storeNo'); // 'storeNo' id를 가진 요소 선택
    var $category1Select = $('#category1');
    var $category2Select = $('#category2'); // 'category2' id를 가진 요소 선택

    $hotelSelect.on('change', loadStoresByHotel);
    $storeSelect.on('change', loadCategory1ByStoreNo);
    $category1Select.on('change', loadCategory2ByCategory1);
    $category2Select.on('change', loadMenuByCategory2);

    $(document).on('click', '.btn-danger', function() {
        var optionId = $(this).data('option-id'); // 버튼의 data-category-id 속성에서 카테고리 ID를 가져옵니다.
        $('#hiddenOptionNo').val(optionId); // 모달 내부의 hidden input에 해당 카테고리 ID를 설정합니다.
        $('#deleteConfirmModal').modal('show'); // 모달 열기
    });

    $('#deleteConfirmModal .btn-danger').on('click', function() {
        var optionId = $('#hiddenOptionNo').val(); // hidden input에서 카테고리 ID를 가져옵니다.

        function isValid(value) {
            return value !== null && value !== undefined && value !== '';
        }

        // AJAX 요청을 통해 서버에 카테고리 삭제를 요청합니다.
        $.ajax({
            url: '/manager/menu/option/delete/' + optionId, // 실제 URL은 서버 API 설계에 따라 다를 수 있습니다.
            type: 'DELETE', // HTTP DELETE 메서드를 사용하여 해당 카테고리를 삭제합니다.
            success: function(result) {
                // 성공적으로 삭제되면, 페이지를 새로 고침하거나 변경된 데이터로 UI를 업데이트합니다.
                alert('옵션이 성공적으로 삭제되었습니다.');
                $('#deleteConfirmModal').modal('hide'); // 모달 닫기
                location.reload();
            },
            error: function(err) {
                console.error('카테고리 삭제 중 에러 발생', err);
                alert('옵션 삭제 중 문제가 발생했습니다.');
            }
        });
    });
});
function loadStoresByHotel() {
    var hotelNo = $("#hotelNo").val();

    $.ajax({
        url: `/api/menus?hotelNo=${hotelNo}`,
        type: 'GET',
        dataType: 'json',
        success: function(menus) {
            console.log(menus);
            var storeNo = $("#storeNo");
            storeNo.empty(); // 매장 선택 초기화
            storeNo.append('<option value="">매장 선택</option>');

            $.each(menus, function(i, menu) {
                storeNo.append($('<option>', {
                    value: menu.storeNo,
                    text: menu.name
                }));
            });

            storeNo.change(function() {
                $("#hiddenStoreNo").val($(this).val());
            });
        },
        error: function(xhr, status, error) {
            console.error("Error fetching menus:", status, error);
        }
    });
}

function loadCategory1ByStoreNo() {
    var storeNo = $("#storeNo").val();
    console.log(storeNo);

    $.ajax({
        url: `/api/category?storeNo2=${storeNo}`,
        type: 'GET',
        data: {'storeNo':`${storeNo}`
        },
        dataType: 'json',
        success: function(categories) {
            console.log(categories);
            var category1 = $("#category1");
            category1.empty(); // 1차 카테고리 선택 초기화
            category1.append('<option value="">1차 카테고리 선택</option>');

            $.each(categories, function(i, category) {
                category1.append($('<option>', {
                    value: category.categoryNo,
                    text: category.name
                }));
            });
        },
        error: function(xhr, status, error) {
            console.error("Error fetching categories:", status, error);
        }
    });
}
function loadCategory2ByCategory1() {
    var category1 = $("#category1").val();

    $.ajax({
        url: `/api/category3?category1=${category1}`,
        type: 'GET',
        dataType: 'json',
        success: function(categories) {
            console.log(categories);
            var category2 = $("#category2");
            category2.empty(); // 매장 선택 초기화
            category2.append('<option value="">2차 카테고리 선택</option>');

            $.each(categories, function(i, category3) {
                category2.append($('<option>', {
                    value: category3.categoryNo,
                    text: category3.name
                }));
                console.log(category3.categoryNo);
            });
        },
        error: function(xhr, status, error) {
            console.error("Error fetching menus:", status, error);
        }
    });
}
function loadMenuByCategory2() {
    var category2 = $("#category2").val();
    console.log(category2);

    $.ajax({
        url: `/api/category4?category2=${category2}`,
        type: 'GET',
        dataType: 'json',
        success: function(categories) {
            console.log(categories);
            var category2 = $("#menuNo");
            category2.empty(); // 매장 선택 초기화
            category2.append('<option value="">메뉴 선택</option>');

            $.each(categories, function(i, category3) {
                category2.append($('<option>', {
                    value: category3.menuNo,
                    text: category3.name
                }));
            });
            category2.change(function() {
                $("#hiddenCategory2").val($(this).val());
                console.log( $($(this).val()));
            });

        },
        error: function(xhr, status, error) {
            console.error("Error fetching menus:", status, error);
        }
    });
}