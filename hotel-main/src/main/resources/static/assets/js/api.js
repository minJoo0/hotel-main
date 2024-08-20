//우편번호찾기를 클릭시 처리되는 메소드
//daum에서 제공하는 기본 메소드
function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            console.log(data);

            //조회한 우편번호를 html에 id명이 postcode를 찾아 value에 값을 적용한다.
            document.getElementById('postcode').value = data.zonecode;

            document.getElementById('sido').value = data.sido;

            document.getElementById('sigungu').value = data.sigungu;
            //조회한 주소를 html에 id명이 address를 찾아 value에 값을 적용한다.
            document.getElementById("address").value = data.jibunAddress;

            document.getElementById("roadAddress").value = data.roadAddress;
            //html에 id명이 detailAddress 태그에 커서를 이동시킨다.
            document.getElementById("detailAddress").focus();

        }
    }).open(); //주소검색창 열기
}