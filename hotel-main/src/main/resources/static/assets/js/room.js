async function fetchAndFillSelect(url, selectElement, optionTextProperty, optionValueProperty) {
    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        const data = await response.json();
        data.forEach(function(item) {
            var option = document.createElement('option');
            option.text = item[optionTextProperty];
            option.value = item[optionValueProperty];
            selectElement.appendChild(option);
        });
    } catch (error) {
        console.error('There was a problem with the fetch operation:', error);
    }
}

function openModal() {
    var modal = document.getElementById('myModal');
    modal.style.display = 'block';

    // 외부 페이지 로드
    var xhr = new XMLHttpRequest();
    xhr.open('GET', '/manager/hotel/room/register', true);
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4 && xhr.status == 200) {
            document.getElementById('registerFormContainer').innerHTML = xhr.responseText;


            document.getElementById('addBtnContainer').addEventListener('change', function(e) {
                if(e.target && e.target.name === 'codeGroupNo') {
                    var codeGroupNo = e.target.value; // 선택된 상위 코드 ID
                    var subCodeSelect = e.target.closest('.row').querySelector('select[name="codeNo"]');
                    fetchSubCodes(codeGroupNo, subCodeSelect);
                    console.log(subCodeSelect);
                }
            });

            function fetchSubCodes(codeGroupNo, selectElement) {
                console.log(codeGroupNo);
                var url = "/api/codeGroups/subCodes?codeGroupNo=" + codeGroupNo;

                fetch(url)
                    .then(response => response.json())
                    .then(data => {
                        // 이전에 있던 하위 코드 옵션들을 초기화
                        selectElement.innerHTML = '<option selected>하위 코드 선택</option>';
                        // 서버로부터 받은 데이터로 하위 코드 옵션들을 추가
                        data.forEach(function(item) {
                            var option = document.createElement('option');
                            option.text = item.name; // 하위 코드 이름
                            option.value = item.codeNo; // 하위 코드 ID
                            selectElement.appendChild(option);
                        });
                    })
                    .catch(error => console.error('Error fetching sub codes:', error));
            }
            // '객실 타입' select 요소를 가져옵니다.

            // 동적으로 추가된 '하위 코드 선택' select 요소를 채우기 위한 로직을 추가할 수 있습니다.
            // 예를 들어, '상위 코드 선택'의 값이 변경될 때마다 '하위 코드 선택'의 옵션을 새로 불러오고 싶다면,
            // '상위 코드 선택'의 change 이벤트 리스너를 추가하고 해당 이벤트가 발생할 때마다 '하위 코드 선택'의 옵션을 업데이트하는 방식으로 구현할 수 있습니다.

            document.getElementById('addCodeBtn').addEventListener('click', function() {
                // 현재 페이지에 존재하는 카테고리 창의 수를 계산합니다.
                var currentCodeWindows = document.querySelectorAll('.row.mb-3').length;

                // 3개 이상의 카테고리 창이 이미 존재할 경우 추가하지 않고 경고창을 띄웁니다.
                if(currentCodeWindows >= 4) {
                    alert('더 이상 객실 타입 창을 추가할 수 없습니다.');
                    return; // 함수 실행을 여기서 중단합니다.
                }

                // 새 카테고리 창 추가 로직
                var newRow = document.createElement('div');
                newRow.classList.add('row', 'mb-3');

                newRow.innerHTML = `
    <div class="col-lg-2 col-form-label">객실 타입</div>
    <div class="col-lg-3">
        <select class="form-select" aria-label="Default select example" name="codeGroupNo" >
            <option selected>상위 코드 선택</option>
            <!-- 옵션은 서버로부터 동적으로 받거나, 정적으로 지정해야 합니다. -->

        </select>
    </div>
    <div class="col-lg-3">
        <select class="form-select" aria-label="Default select example" name="codeNo" >
            <option selected>하위 코드 선택</option>
            <!-- 옵션은 서버로부터 동적으로 받거나, 정적으로 지정해야 합니다. -->
        </select>
    </div>
    <div class="col-lg-3">
        <button type="button" class="btn btn-danger removeCodeBtn">-</button>   
    </div>
    `;

                document.getElementById('addBtnContainer').appendChild(newRow);
                var newCodeGroupSelect = newRow.querySelector("select[name='codeGroupNo']");
                var codeGroupUrl = "/api/codeGroups";
                fetchAndFillSelect(codeGroupUrl, newCodeGroupSelect, 'name', 'codeGroupNo');
            });
            document.addEventListener('click', function(e) {
                if (e.target && e.target.classList.contains('removeCodeBtn')) {
                    e.target.closest('.row').remove();
                }
            });

        }
    };
    xhr.send();
}

// 모달 닫기 함수
function closeModal() {
    var modal = document.getElementById('myModal');
    modal.style.display = 'none';
}
function openModal1(roomNo, hotelNo) {
    var modal = document.getElementById('myModal1');
    modal.style.display = 'block';

    // 외부 페이지 로드
    var xhr = new XMLHttpRequest();
    xhr.open('GET', `/manager/hotel/room/update/${roomNo}`, true);
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4 && xhr.status == 200) {
            document.getElementById('updateFormContainer').innerHTML = xhr.responseText;
            document.getElementById('hiddenRoomNo').value = roomNo;
            document.getElementById('hiddenHotelNo1').value = hotelNo;


            document.getElementById('addBtnContainer').addEventListener('change', function(e) {
                if(e.target && e.target.name === 'codeGroupNo') {
                    var codeGroupNo = e.target.value; // 선택된 상위 코드 ID
                    var subCodeSelect = e.target.closest('.row').querySelector('select[name="codeNo"]');
                    fetchSubCodes(codeGroupNo, subCodeSelect);
                }
            });

            function fetchSubCodes(codeGroupNo, selectElement) {
                console.log(codeGroupNo);
                var url = "/api/codeGroups/subCodes?codeGroupNo=" + codeGroupNo;

                fetch(url)
                    .then(response => response.json())
                    .then(data => {
                        // 이전에 있던 하위 코드 옵션들을 초기화
                        selectElement.innerHTML = '<option selected>하위 코드 선택</option>';
                        // 서버로부터 받은 데이터로 하위 코드 옵션들을 추가
                        data.forEach(function(item) {
                            var option = document.createElement('option');
                            option.text = item.name; // 하위 코드 이름
                            option.value = item.codeNo; // 하위 코드 ID
                            selectElement.appendChild(option);
                        });
                    })
                    .catch(error => console.error('Error fetching sub codes:', error));
            }
            // '객실 타입' select 요소를 가져옵니다.

            // 동적으로 추가된 '하위 코드 선택' select 요소를 채우기 위한 로직을 추가할 수 있습니다.
            // 예를 들어, '상위 코드 선택'의 값이 변경될 때마다 '하위 코드 선택'의 옵션을 새로 불러오고 싶다면,
            // '상위 코드 선택'의 change 이벤트 리스너를 추가하고 해당 이벤트가 발생할 때마다 '하위 코드 선택'의 옵션을 업데이트하는 방식으로 구현할 수 있습니다.

            document.getElementById('addCodeBtn').addEventListener('click', function() {
                // 현재 페이지에 존재하는 카테고리 창의 수를 계산합니다.
                var currentCodeWindows = document.querySelectorAll('.row.mb-4').length;

                // 3개 이상의 카테고리 창이 이미 존재할 경우 추가하지 않고 경고창을 띄웁니다.
                if(currentCodeWindows >= 4) {
                    alert('더 이상 객실 타입 창을 추가할 수 없습니다.');
                    return; // 함수 실행을 여기서 중단합니다.
                }

                // 새 카테고리 창 추가 로직
                var newRow = document.createElement('div');
                newRow.classList.add('row', 'mb-4');

                newRow.innerHTML = `
    <div class="col-lg-2 col-form-label">객실 타입</div>
    <div class="col-lg-3">
        <select class="form-select" aria-label="Default select example" name="codeGroupNo" >
            <option selected>상위 코드 선택</option>
            <!-- 옵션은 서버로부터 동적으로 받거나, 정적으로 지정해야 합니다. -->

        </select>
    </div>
    <div class="col-lg-3">
        <select class="form-select" aria-label="Default select example" name="codeNo" >
            <option selected>하위 코드 선택</option>
            <!-- 옵션은 서버로부터 동적으로 받거나, 정적으로 지정해야 합니다. -->
        </select>
    </div>
    <div class="col-lg-3">
        <button type="button" class="btn btn-danger removeCodeBtn">-</button>   
    </div>
    `;

                document.getElementById('addBtnContainer').appendChild(newRow);
                var newCodeGroupSelect = newRow.querySelector("select[name='codeGroupNo']");
                var codeGroupUrl = "/api/codeGroups";
                fetchAndFillSelect(codeGroupUrl, newCodeGroupSelect, 'name', 'codeGroupNo');
            });
            document.addEventListener('click', function(e) {
                if (e.target && e.target.classList.contains('removeCodeBtn')) {
                    e.target.closest('.row').remove();
                }
            });

        }
    };
    xhr.send();
}
function openModal2(roomNo, hotelNo) {
    var modal = document.getElementById('myModal2');
    modal.style.display = 'block';

    // 외부 페이지 로드
    var xhr = new XMLHttpRequest();
    xhr.open('GET', `/manager/hotel/room/read/${roomNo}`, true);
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4 && xhr.status == 200) {
            document.getElementById('readFormContainer').innerHTML = xhr.responseText;

        }
    };
    xhr.send();
}

// 모달 닫기 함수
function closeModal1() {
    var modal = document.getElementById('myModal1');
    modal.style.display = 'none';
}

document.addEventListener('DOMContentLoaded', function() { // DOM이 완전히 로드되었을 때 실행
    var modalBtn = document.getElementById('modalBtn'); // 'hotelNo' id를 가진 요소 선택
    var modalBtn1 = document.getElementById('modalBtn1'); // 'hotelNo' id를 가진 요소 선택
    var modalBtn2 = document.getElementById('modalBtn2'); // 'hotelNo' id를 가진 요소 선택
    modalBtn.addEventListener('click', openModal); // 'change' 이벤트에 대한 리스너 추가
    modalBtn1.addEventListener('click', openModal1); // 'change' 이벤트에 대한 리스너 추가
    modalBtn2.addEventListener('click', openModal2); // 'change' 이벤트에 대한 리스너 추가

});

function setDeleteTarget(roomNo) {
    document.getElementById('hiddenRoomNo1').value = roomNo;
}






