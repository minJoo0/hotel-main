$(function() {

    // input 태그의 값을 기반으로 시작일과 종료일을 추출
    var dateRangeValue = $('#dateRangePicker').val();
    var dates = dateRangeValue.split(' ~ ');
    var startDate = dates[0] ? moment(dates[0], "YYYY-MM-DD") : moment(); // ISO 형식으로 날짜를 명시
    var endDate = dates.length > 1 && dates[1] ? moment(dates[1], "YYYY-MM-DD") : moment().add(1, 'days'); // ISO 형식으로 날짜를 명시

    $('#startDate').val(startDate.format('YYYY-MM-DD'));
    $('#endDate').val(endDate.format('YYYY-MM-DD'));

    $('#dateRangePicker').daterangepicker({
        locale: {
            format: 'YYYY-MM-DD', // 날짜 형식 지정
            separator: ' ~ ',
            applyLabel: '확인',
            cancelLabel: '취소',
            fromLabel: '시작일',
            toLabel: '종료일',
            customRangeLabel: '사용자 정의',
            weekLabel: 'W',
            daysOfWeek: ['일', '월', '화', '수', '목', '금', '토'],
            monthNames: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
        },
        startDate: startDate,
        endDate: endDate,
        minDate: moment(), // 최소 선택 가능한 날짜를 오늘로 설정
        drops: 'down'
    }, function(start, end, label) {
        // 날짜 선택 시, startDate와 endDate input 필드에 날짜 설정
        $('#startDate').val(start.format('YYYY-MM-DD'));
        $('#endDate').val(end.format('YYYY-MM-DD'));
    });
});

$(document).ready(function() {
    // 지역 검색창과 모달을 가져오기
    const hotelRegionInput = $("#hotelRegion");
    const regionModal = $("#regionModal");
    const regionList = $("#regionList");
    const sigunguList = $("#sigunguList"); // sigungu 목록을 표시할 컨테이너 추가

    // 서버에서 추천 지역 목록을 가져올 URL
    const apiUrl = "/user/api/region";

    // 추천 지역 목록을 가져와서 지역 목록을 업데이트하는 함수
    function updateRegionList(filter = "") {
        $.ajax({
            type: "GET",
            url: apiUrl,
            dataType: "json",
            success: function(data) {
                const recommendedRegions = data;
                updateSigunguList(filter, recommendedRegions); // 필터링된 시군구 목록 업데이트


                // 필터링된 추천 지역 목록
                const filteredRegions = recommendedRegions.filter(region =>
                    region.sido.includes(filter) || region.sigungu.includes(filter)
                );

                // 중복 제거된 sido 목록
                const uniqueSidos = Array.from(new Set(filteredRegions.map(region => region.sido)));

                // sido 목록 업데이트
                regionList.empty();
                for (let i = 0; i < uniqueSidos.length; i += 3) {
                    const row = $("<div>").addClass("row mb-1");
                    for (let j = i; j < i + 3 && j < uniqueSidos.length; j++) {
                        const sido = uniqueSidos[j];
                        const col = $("<div>").addClass("col-4 mb-2");
                        const button = $("<button>")
                            .attr("id", `destination_suggestion_card_${sido}`)
                            .attr("type", "button")
                            .addClass("btn w-100 p-1 d-flex align-items-center ad6c3-cursor-pointer border-0 rounded shadow-sm")
                            .css("height", "85px")
                            .css("background-color", "#ffffff")
                            .on("click", function() {
                                const sido = $(this).attr("id").split("_")[3];
                                hotelRegionInput.val(sido);
                                updateSigunguList(sido, recommendedRegions); // data를 인자로 전달
                            });

                        const descriptionContainer = $("<div>")
                            .addClass("flex-1 d-flex flex-column justify-content-center");

                        const sidoName = $("<p>")
                            .addClass("mb-0 fw-bold text-start")
                            .text(sido);

                        descriptionContainer.append(sidoName);
                        button.append(descriptionContainer);
                        col.append(button);
                        row.append(col);
                    }
                    regionList.append(row);
                }

                // 모달 표시 여부 결정
                if (regionList.children().length > 0) {
                    regionModal.removeClass("d-none");
                } else {
                    regionModal.addClass("d-none");
                }
            },
            error: function(xhr, status, error) {
                console.error("Error fetching recommended regions:", error);
            }
        });
    }

    // 선택된 sido에 해당하는 sigungu 목록을 업데이트하는 함수
    function updateSigunguList(query, regions) {
        let filteredSigungus = [];
        if (query) { // query가 있을 때만 필터링
            filteredSigungus = regions.filter(region => {
                return region.sido.includes(query) || region.sigungu.includes(query);
            });
        } else {
            sigunguList.empty(); // query가 없으면 sigunguList를 비우고 함수 종료
            return;
        }

        // 중복 제거된 sigungu 목록
        const uniqueSigungus = Array.from(new Set(filteredSigungus.map(region => region.sigungu)));

        sigunguList.empty(); // 기존 sigungu 목록 초기화
        for (let i = 0; i < uniqueSigungus.length; i += 3) {
            const row = $("<div>").addClass("row mb-1");
            for (let j = i; j < i + 3 && j < uniqueSigungus.length; j++) {
                const sigungu = uniqueSigungus[j];
                const sido = regions.find(region => region.sigungu === sigungu)?.sido;
                const col = $("<div>").addClass("col-4 mb-2");
                const button = $("<button>")
                    .attr("id", `destination_suggestion_card_${sigungu}`)
                    .attr("type", "button")
                    .addClass("btn w-100 p-1 d-flex align-items-center ad6c3-cursor-pointer border-0 rounded shadow-sm")
                    .css("height", "85px")
                    .css("background-color", "#ffffff")
                    .on("click", function() {
                        hotelRegionInput.val(sigungu); // sigungu 선택 시 입력창에 값 설정
                        regionModal.addClass("d-none");
                    });

                const descriptionContainer = $("<div>")
                    .addClass("flex-1 d-flex flex-column justify-content-center");

                const sidoName = $("<p>")
                    .addClass("mb-0 text-muted text-start")
                    .text(sido);

                const sigunguName = $("<p>")
                    .addClass("mb-0 fw-bold text-start")
                    .text(sigungu);
                descriptionContainer.append(sidoName);
                descriptionContainer.append(sigunguName);
                button.append(descriptionContainer);
                col.append(button);
                row.append(col);
            }
            sigunguList.append(row);
        }
    }

    // 지역 검색 입력 이벤트 핸들러
    hotelRegionInput.on("input", function() {
        const query = hotelRegionInput.val().trim();
        updateRegionList(query); // 입력값이 있을 때도 updateRegionList 호출
        if (query) {
            // updateRegionList 함수 내에서 처리하도록 변경
        } else {
            regionModal.addClass("d-none");
        }
    });

    // 모달창 밖을 클릭하면 모달을 숨김
    $(document).on("click", function(event) {
        if (!$(event.target).closest("#regionModal, #hotelRegion").length) {
            regionModal.addClass("d-none");
        }
    });

    // 검색창이 다시 포커스를 얻을 때 모달을 다시 표시
    hotelRegionInput.on("focus", function() {
        updateRegionList();
    });
});