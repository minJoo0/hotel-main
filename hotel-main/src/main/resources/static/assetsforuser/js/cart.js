$(document).ready(function() {
    // 페이지가 준비되었을 때 각 항목과 전체 합계 업데이트
    updateAllItemTotals();
    updateGrandTotal();

    $('.increase, .decrease').click(function() {
        var $input = $(this).parent().siblings('input.quantity-amount');
        var currentQuantity = parseInt($input.val());
        var isIncrease = $(this).hasClass('increase');
        var newQuantity = isIncrease ? currentQuantity + 1 : currentQuantity - 1;

        if (newQuantity < 1 || newQuantity > 99) {
            // 상품 수량이 1 미만이거나 99 초과인 경우 변경하지 않음
            return;
        }

        var cartNo = $input.attr('data-cartNo');

        $.ajax({
            url: '/user/api/updateQuantity',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ cartNo: cartNo, cartCount: newQuantity }),
            success: function(response) {
                $input.val(newQuantity); // 입력 필드에 새로운 수량을 반영
                updateItemTotal($input); // 항목 합계 업데이트
                updateGrandTotal(); // 전체 합계 업데이트

                // 숨김 입력 필드에 새로운 수량을 반영
                $("input[name='paymentCount'][data-cartNo='" + cartNo + "']").val(newQuantity);
            },
            error: function(err) {
                // 에러 처리 로직...
            }
        });
    });
});

function updateItemTotal($input) {
    var pricePerItem = parseInt($input.data('price'));
    var optionPrice = parseInt($input.data('option-price'));
    var newQuantity = $input.val();
    var totalPrice = (pricePerItem + optionPrice) * newQuantity;
    var index = $input.attr('id').split('-')[1];
    $('#total-' + index).text(totalPrice + ' 원');
}

// 모든 아이템의 합계를 업데이트하는 함수
function updateAllItemTotals() {
    $('input.quantity-amount').each(function() {
        updateItemTotal($(this));
    });
}

// 전체 합계 업데이트 함수
function updateGrandTotal() {
    console.log("updateGrandTotal 함수 시작"); // 디버깅을 위한 콘솔 로그
    var grandTotal = 0;
    $('.total-price').each(function() {
        var itemTotal = parseInt($(this).text().replace(' 원', ''));
        grandTotal += itemTotal;
    });

    // 전체 합계를 페이지에 업데이트
    console.log(`계산된 grandTotal: ${grandTotal}`);
    $('.grand-total strong').text(grandTotal + ' 원');
}
$(document).ready(function() {
    $('.delete-btn').click(function() {
        var cartNo = $(this).data('cartno'); // data-cartno 속성을 이용해 장바구니 번호 획득

        $.ajax({
            url: '/user/cart/delete', // 요청을 보낼 URL
            type: 'POST',
            data: {cartNo: cartNo}, // 보내질 데이터
            success: function(response) {
                // 요청이 성공했을 때의 동작
                // 예를 들어 페이지를 새로고침하거나 삭제된 아이템을 DOM에서 직접 제거할 수 있습니다.
                location.reload(true); // 간단하게 페이지를 새로고침하여 변경사항 반영
            },
            error: function(error) {
                // 에러 처리 로직
                alert('삭제 처리 중 에러가 발생했습니다.');
            }
        });
    });
});

$(document).ready(function() {
    $("#cartForm").submit(function(event) {
        event.preventDefault(); // 기본 폼 제출 방지

        var pathArray = window.location.pathname.split('/');
        var userNo = pathArray[pathArray.length - 1];

        // AJAX 요청을 통해 서버에 'cartDTOList'가 비어 있는지 확인
        $.ajax({
            url: '/user/api/' + userNo,
            type: 'GET',
            success: function(isEmpty) {
                if(isEmpty) {
                    alert("장바구니가 비어 있습니다.");
                } else {
                    // 'cartDTOList'가 비어 있지 않으면 폼을 수동으로 제출
                    $("#cartForm").unbind('submit').submit();
                }
            },
            error: function() {
                alert("요청 처리 중 오류가 발생했습니다.");
            }
        });
    });
});