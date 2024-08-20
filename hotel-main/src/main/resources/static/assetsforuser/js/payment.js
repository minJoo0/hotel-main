document.addEventListener('DOMContentLoaded', function () {
    calculateTotal();
});

function calculateTotal() {
    let total = 0;
    // 모든 메뉴 가격을 찾아 총합을 계산
    document.querySelectorAll('.menu-price').forEach(function (item) {
        total += parseFloat(item.innerText.replace('$', '')); // 달러 기호를 제거하고 숫자로 변환
    });

    // 최종 총합 값을 설정
    document.getElementById('grandTotalPrice').innerText = total;
    document.getElementById('grandTotalPriceValue').value = total;

}

document.addEventListener('DOMContentLoaded', function () {
    document.querySelector('#myForm').addEventListener('submit', function (event) {
        var regionSelect = document.getElementById('regionSelect').value;
        var hotelSelect = document.getElementById('hotelSelect').value;
        var myModal = new bootstrap.Modal(document.getElementById('hotelAlertModal'), {
            keyboard: false // 모달이 열린 상태에서 키보드의 esc 키를 눌러도 닫히지 않도록 설정
        });


        // 지역은 선택되었으나 호텔이 선택되지 않은 경우
        if (regionSelect !== "" && hotelSelect === "") {
            event.preventDefault(); // 폼 제출 방지
            // 모달 표시를 위한 순수 JavaScript 코드
            myModal.show();
        }
    });
});

function loadHotelsByRegion() {
    var selectedRegion = document.getElementById("regionSelect").value;

    fetch(`/user/api/hotels?sido=${selectedRegion}`)
        .then(response => response.json())
        .then(hotels => {
            var hotelSelect = document.getElementById("hotelSelect");
            hotelSelect.innerHTML = '<option value="">Select a hotel</option>'; // 초기화

            hotels.forEach(hotel => {
                var option = document.createElement("option");
                option.value = hotel.hotelNo;
                option.text = hotel.name;
                option.name = "hotelNo"
                hotelSelect.appendChild(option);
            });
        });
}

function loadRoomsByHotel() {
    var selectedHotel = document.getElementById("hotelSelect").value;
    var pathArray = window.location.pathname.split('/');
    var userNo = pathArray[pathArray.length - 1]; // URL의 마지막 부분이 userNo임

    fetch(`/user/api/rooms?userNo=${userNo}&hotelNo=${selectedHotel}`)
        .then(response => response.json())
        .then(rooms => {
            var roomSelect = document.getElementById("roomSelect");
            roomSelect.innerHTML = '<option value="">Select a room</option>'; // 초기화

            rooms.forEach(room => {
                var option = document.createElement("option");
                option.value = room.reserveNo;
                option.text = `${room.roomName} / ${room.startDate} ~ ${room.endDate}`;
                option.name = "reserveNo"
                roomSelect.appendChild(option);
            });
        });
}

IMP.init("imp26863141"); //상점ID

function requestPay() {
    var productName = document.getElementById("menuName").value;
    var price = document.getElementById("grandTotalPriceValue").value;
    var customerName = document.getElementById("userName").value;
    var phoneNumber = document.getElementById("phone").value;
    var email = document.getElementById("Email").value;


    // 현재 시간을 이용해 고유한 merchant_uid 생성
    var merchantUid = 'merchant_' + new Date().getTime();
    console.log(merchantUid);
    IMP.request_pay({
        pg: "html5_inicis", //결재사
        pay_method: "card", //결재방식
        merchant_uid: merchantUid,
        name: productName, //"연습용상품",
        amount: price, //12000,
        popup: false,
        //아래 변수들 생략가능
        buyer_name: customerName, //"홍길동",
        buyer_tel: phoneNumber, //"010-3199-6910",
        buyer_email: email, //"test@gmail.com",
    }, function(response) {
        if (response.success) {
            alert("결제가 성공했습니다.");
            console.log(response);

            // 폼 제출
            document.getElementById("myForm").submit();
        } else {
            // 결제 실패 시 로직
            alert("결제 취소되었습니다: " + response.error_msg);
            console.log(response);
        }
    });
}
