
//호텔선택시 호텔정보가져와서 select목록 만들기
function handleGridRadios2Click() {

    var currentCodeWindows = document.querySelectorAll('.row.mb-3.new').length;


    // 1개 이상의 카테고리 창이 이미 존재할 경우 추가하지 않고 경고창을 띄웁니다.
    if(currentCodeWindows >= 1) {
        return; // 함수 실행을 여기서 중단합니다.
    }


    // 새로운 카테고리 창을 추가하는 로직입니다.
    var newRow = document.createElement('div');
    newRow.classList.add('row', 'mb-3','new');

    // 새로운 카테고리 창의 내용을 설정합니다.
    newRow.innerHTML =
        `
                <div class="col-lg-2 col-form-label">호텔선택</div>
                <div class="col-lg-3">
                    <select class="form-select" aria-label="Default select example" name="sido" >
                        <option selected>지역</option>
                        
                        <!-- 옵션은 서버로부터 동적으로 받거나, 정적으로 지정해야 합니다. -->
                    </select>
                </div>
                <div class="col-lg-3">
                    <select class="form-select" aria-label="Default select example" name="hotelNo" >
                        <option selected>호텔</option>
                        <!-- 옵션은 서버로부터 동적으로 받거나, 정적으로 지정해야 합니다. -->
                    </select>
                </div>
        `;



    document.getElementById('u').appendChild(newRow);
    var newRegionSelect = newRow.querySelector("select[name='sido']");


    var url = "/api/notice/regions"

        fetch(url)
            .then(response => response.json())
            .then(data => {
                // 이전에 있던 호텔 옵션들을 초기화
                newRegionSelect.innerHTML = '<option value="" selected>지역 선택</option>';
                // 서버로부터 받은 데이터로 호텔 옵션들을 추가
                data.forEach(function(item) {
                    var option = document.createElement('option');
                    option.text = item; //  이름
                    option.value = item; //  ID
                    newRegionSelect.appendChild(option);
                });
            })
            .catch(error => console.error('Error fetching 호텔:', error));






    document.getElementById('u').addEventListener('change', function(e) {
        if(e.target && e.target.name === 'sido') {
            var region = e.target.value; // 선택된 지역 ID
            var hotel = e.target.closest('.row').querySelector('select[name="hotelNo"]');
            fetchHotels(region,hotel);
        }
    });

    //현재 사용자 권한있는 호텔목록 불러오기
    function fetchHotels(sido, selectElement) {
        console.log(sido);
        var url = "/api/notice/hotels?sido=" + sido;

        fetch(url)
            .then(response => response.json())
            .then(data => {
                // 이전에 있던 호텔 옵션들을 초기화
                selectElement.innerHTML = '<option value="" selected>호텔 선택</option>';
                // 서버로부터 받은 데이터로 호텔 옵션들을 추가
                data.forEach(function(item) {
                    var option = document.createElement('option');
                    option.text = item.name; //  이름
                    option.value = item.hotelNo; //  ID
                    selectElement.appendChild(option);
                });
            })
            .catch(error => console.error('Error fetching sub codes:', error));
    }
// '지역호텔' select 요소를 가져옵니다.




}

function handleGridRadios1Click() {
    // 'id="u"' 안의 내용을 삭제합니다.
    document.getElementById('u').innerHTML = '';
}

//라디오버튼선택체크
function validateForm() {
    // 라디오 버튼 요소 가져오기
    var radioButtons = document.getElementsByName("group");
    var isChecked = false;

    // 라디오 버튼이 선택되었는지 확인
    for (var i = 0; i < radioButtons.length; i++) {
        if (radioButtons[i].checked) {
            isChecked = true;
            break;
        }
    }

    // 라디오 버튼이 선택되지 않았을 경우 경고창 표시
    if (!isChecked) {
        alert("구분을 선택 해 주세요");
        return false; // 폼 제출 방지
    }

    // 호텔 라디오 버튼이 선택되었는지 확인
    var hotelRadio = document.getElementById('gridRadios2');
    if (hotelRadio.checked) {
        // 호텔 라디오 버튼이 선택된 경우
        var hotelSelect = document.querySelector('select[name="hotelNo"]');
        var regionSelect = document.querySelector('select[name="sido"]');
        var selectedOption = hotelSelect.options[hotelSelect.selectedIndex];
        var selectedRegion = regionSelect.options[regionSelect.selectedIndex];

        // hotelNo가 null인 경우 경고창을 표시하고 폼 제출 방지
        if (selectedOption.value === "" || selectedRegion.value === "") {
            alert("호텔을 선택해주세요.");
            return false; // 폼 제출 방지
        }


    }

    // 라디오 버튼이 선택되었을 경우 폼 제출 허용
    return true;
}
