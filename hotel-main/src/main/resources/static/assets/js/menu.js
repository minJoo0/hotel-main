document.addEventListener('DOMContentLoaded', function() {
    document.querySelector('#myForm').addEventListener('submit', function (event) {
        var hotelNo = document.getElementById('hotelNo').value;
        var storeNo = document.getElementById('storeNo').value;
        var category1 = document.getElementById('category1').value;
        var category2 = document.getElementById('category2').value;
        var myModal = new bootstrap.Modal(document.getElementById('hotelAlertModal'), {
            keyboard: false // 모달이 열린 상태에서 키보드의 esc 키를 눌러도 닫히지 않도록 설정
        });


        // 지역은 선택되었으나 호텔이 선택되지 않은 경우
        if (hotelNo === "호텔 선택") {
            event.preventDefault(); // 폼 제출 방지
            // 모달 표시를 위한 순수 JavaScript 코드
            myModal.show();
        }
        if (storeNo === "매장 선택") {
            event.preventDefault(); // 폼 제출 방지
            // 모달 표시를 위한 순수 JavaScript 코드
            myModal.show();
        }
        if (category1 === "1차 카테고리 선택") {
            event.preventDefault(); // 폼 제출 방지
            // 모달 표시를 위한 순수 JavaScript 코드
            myModal.show();
        }
        if (category2 === "2차 카테고리 선택") {
            event.preventDefault(); // 폼 제출 방지
            // 모달 표시를 위한 순수 JavaScript 코드
            myModal.show();
        }
    });
});



document.addEventListener('DOMContentLoaded', function() { // DOM이 완전히 로드되었을 때 실행
    var hotelSelect = document.getElementById('hotelNo'); // 'hotelNo' id를 가진 요소 선택
    var storeSelect = document.getElementById('storeNo'); // 'hotelNo' id를 가진 요소 선택
    var category1Select = document.getElementById('category1'); // 'hotelNo' id를 가진 요소 선택
    hotelSelect.addEventListener('change', loadHotelsByRegion);
    storeSelect.addEventListener('change', loadCategory1ByStoreNo);
    category1Select.addEventListener('change', loadCategory2ByCategory1);

});
function loadHotelsByRegion() {
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