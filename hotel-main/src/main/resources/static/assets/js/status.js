$(document).ready(function() {
    // 페이지 로드 시 자동으로 실행

    // 검색 버튼 클릭 이벤트
    $("#search").click(function() {
        searchRooms();
    });

    // 상태 버튼 클릭 이벤트
    $('.status-button').click(function() {
        var selectedStatus = $(this).val(); // 클릭한 버튼의 값(상태)을 가져옴
        // 모든 방 버튼을 숨김
        $('.room-button').hide();
        // 선택된 상태와 일치하는 방 버튼만 표시
        $('.' + selectedStatus).show();
    });
    $(document).on('click', '.room-button', function() {
        var roomNo = $(this).val(); // 클릭한 버튼의 값(상태)을 가져옴
        console.log(roomNo);
        var selectedDate = $("#selectedDate").val();
        getRoomInfoByRoomNo(roomNo, selectedDate);
    });

    // 30초마다 자동으로 방 상태를 갱신
    setInterval(autoRefreshRooms, 30000);
});

function searchRooms() {
    var hotelNo = $("#hotelNoSelect").val();
    var selectedDate = $("#selectedDate").val();
    console.log(hotelNo, selectedDate);
    if (hotelNo) { // 호텔 번호가 비어있지 않은 경우
        getRoomsByHotelNo(hotelNo, selectedDate);
    }
}

function getRoomsByHotelNo(hotelNo, selectedDate) {
    $.ajax({
        type: 'GET',
        url: '/api/room',
        data: {
            hotelNo: hotelNo,
            date: selectedDate  // 날짜 값을 추가
        },
        contentType: 'application/json',
        success: function(roomDTOList) {
            console.log(roomDTOList);
            displayRooms(roomDTOList);
        },
        error: function(xhr, status, error) {
            console.log(error);
        }
    });
}
function getRoomInfoByRoomNo(roomNo,selectedDate) {
    $.ajax({
        type: 'GET',
        url: '/api/status',
        data: {
            roomNo: roomNo,
            date: selectedDate
        },
        contentType: 'application/json',
        success: function(roomDTO) {
            displayRoomInfo(roomDTO);
        },
        error: function(xhr, status, error) {
            console.log(error);
        }
    });
}

function autoRefreshRooms() {
    // 자동 갱신 로직. 여기서는 searchRooms 함수를 호출하여 구현합니다.
    searchRooms();
}
function displayRooms(roomDTOList) {
    var roomRow = $('.room-row');
    roomRow.empty(); // 기존 방 목록 지우기

    $.each(roomDTOList, function(index, room) {
        var roomButton = $('<button>')
            .attr('type', 'button')
            .attr('name', 'roomNo')
            .attr('value', `${room.roomNo}`)
            .addClass('room-button')
            .addClass('btn btn-outline-dark')
            .text(`${room.name}`) // 기본 버튼 텍스트 설정

        // `reserveDTOList`를 확인하여 상태 값에 따라 클래스 추가
        if (room.reserveDTOList && room.reserveDTOList.length > 0) {
            $.each(room.reserveDTOList, function(i, reserve) {
                if (reserve.status) {
                    roomButton.addClass(reserve.status); // 상태에 따른 클래스 추가
                }
            });
        }

        roomButton.appendTo(roomRow); // 버튼을 roomRow에 추가
    });
}
function displayRoomInfo(roomDTO) {
    // 각 정보를 표시할 스팬을 선택
    var roomNameSpan = $('.info-row .room-name');
    var reserveNameSpan = $('.info-row .reserve-name');
    var reserveEmailSpan = $('.info-row .reserve-email');
    var checkInSpan = $('.info-row .check-in');
    var checkOutSpan = $('.info-row .check-out');
    console.log(roomDTO.reserveDTOList);
    // roomDTO로부터 정보를 가져와서 스팬에 설정
    roomNameSpan.text(roomDTO.name); // 객실명 설정

    // 예약 정보가 있을 경우 첫 번째 예약 정보를 사용
    if (roomDTO.reserveDTOList && roomDTO.reserveDTOList.length > 0) {
        var firstReserve = roomDTO.reserveDTOList[0];
        reserveNameSpan.text(firstReserve.userName); // 예약자명 설정
        reserveEmailSpan.text(firstReserve.userEmail); // 예약자명 설정
        checkInSpan.text(firstReserve.startDate); // 체크인 날짜 설정
        checkOutSpan.text(firstReserve.endDate); // 체크아웃 날짜 설정
    } else {
        // 예약 정보가 없는 경우 스팬을 비움
        reserveNameSpan.text('예약 없음');
        reserveEmailSpan.text('예약 없음');
        checkInSpan.text('예약 없음');
        checkOutSpan.text('예약 없음');
    }
}
document.addEventListener("DOMContentLoaded", function() {
    var today = new Date();
    var year = today.getFullYear();
    var month = String(today.getMonth() + 1).padStart(2, '0');
    var day = String(today.getDate()).padStart(2, '0');
    var todayStr = year + '-' + month + '-' + day;
    document.getElementById('selectedDate').value = todayStr;
});