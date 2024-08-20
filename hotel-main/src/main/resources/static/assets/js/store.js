document.addEventListener('DOMContentLoaded', function() {
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

    fetch(`/api/hotels?sido=${selectedRegion}`)
        .then(response => response.json())
        .then(hotels => {
            var hotelSelect = document.getElementById("hotelSelect");
            hotelSelect.innerHTML = '<option value="">호텔 선택</option>'; // 초기화

            hotels.forEach(hotel => {
                var option = document.createElement("option");
                option.value = hotel.hotelNo;
                option.text = hotel.name;
                option.name = "adminNo"
                hotelSelect.appendChild(option);
            });
            hotelSelect.onchange = function() {
                document.getElementById("hiddenHotelNo").value = this.value;
            }
        });
}