document.addEventListener('DOMContentLoaded', function() { // DOM이 완전히 로드되었을 때 실행
    var hotelSelect = document.getElementById('hotelNo'); // 'hotelNo' id를 가진 요소 선택
    hotelSelect.addEventListener('change', loadHotelsByRegion);


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
$(document).ready(function() {
    let currentPage = 1;
    let pageSize = $('#size').val();

    function loadPage(page) {
        let hotelNo = $('#hotelNo').val();
        let storeNo = $('#storeNo').val();
        let startDate = $('#startDate').val();
        let endDate = $('#endDate').val();

        if (hotelNo === '' && storeNo === '' && startDate === '' && endDate === '') {
            let today = new Date();
            let tenDaysAgo = new Date(today.getTime() - (10 * 24 * 60 * 60 * 1000));
            startDate = tenDaysAgo.toISOString().slice(0, 10);
            endDate = today.toISOString().slice(0, 10);
            $('#searchInfo').text('날짜 선택을 하지 않은 경우, 최근 10일 정산이 표시됩니다.');
        }else {
            $('#searchInfo').text('');
        }

        // 페이지 데이터 로드
        $.ajax({
            url: '/api/revenue/daily',
            type: 'GET',
            data: {
                page: page,
                size: pageSize,
                hotelNo: hotelNo,
                storeNo: storeNo,
                startDate: startDate,
                endDate: endDate
            },
            success: function(response) {
                $('#dataRows').empty();
                currentPage = response.page;

                if (!response.dtoList || response.dtoList.length === 0) {
                    $('#dataRows').empty().append('<tr><td colspan="11">검색 결과가 없습니다.</td></tr>');
                } else {
                    $('#dataRows').empty();
                    let totalCount = (currentPage - 1) * pageSize + response.dtoList.length;
                    response.dtoList.forEach(result => {
                        $('#dataRows').append(`
                                        <tr>
                                            <td>${totalCount--}</td>
                                            <td>${result.revenueDate}</td>
                                            <td>${formatNumber(result.totalFee)}</td>
                                            <td>${formatNumber(result.totalDepositPrice)}</td>
                                            <td>${formatNumber(result.totalFee + result.totalDepositPrice)}</td>
                                            <td>${formatNumber(result.totalFeeWhereItem)}</td>
                                            <td>${formatNumber(result.totalDepositPriceWhereItem)}</td>
                                            <td>${formatNumber(result.totalFeeWhereItem + result.totalDepositPriceWhereItem)}</td>
                                            <td>${formatNumber(result.totalFeeWhereRoom)}</td>
                                            <td>${formatNumber(result.totalDepositPriceWhereRoom)}</td>
                                            <td>${formatNumber(result.totalFeeWhereRoom + result.totalDepositPriceWhereRoom)}</td>
                                        </tr>
                                    `);
                    });
                    updatePagination(response);
                }
            },
            error: function(error) {
                console.error('데이터 로드 중 오류 발생: ', error);
            }
        });

        // 총 정산 데이터 로드
        $.ajax({
            url: '/api/revenue/total',
            type: 'GET',
            data: {
                hotelNo: hotelNo,
                storeNo: storeNo,
                startDate: startDate,
                endDate: endDate
            },
            success: function(response) {
                if (response) {
                    $('#totalRevenue').empty();
                    $('#totalRevenue').append(`
                        <tr>
                            <td colspan="2"><strong>총 정산</strong></td>
                            <td><strong>${formatNumber(response.totalFee)}</strong></td>
                            <td><strong>${formatNumber(response.totalDepositPrice)}</strong></td>
                            <td><strong>${formatNumber(response.totalFee + response.totalDepositPrice)}</strong></td>
                            <td><strong>${formatNumber(response.totalFeeWhereItem)}</strong></td>
                            <td><strong>${formatNumber(response.totalDepositPriceWhereItem)}</strong></td>
                            <td><strong>${formatNumber(response.totalFeeWhereItem + response.totalDepositPriceWhereItem)}</strong></td>
                            <td><strong>${formatNumber(response.totalFeeWhereRoom)}</strong></td>
                            <td><strong>${formatNumber(response.totalDepositPriceWhereRoom)}</strong></td>
                            <td><strong>${formatNumber(response.totalFeeWhereRoom + response.totalDepositPriceWhereRoom)}</strong></td>
                        </tr>
                    `);
                }
            },
            error: function(error) {
                console.error('총 정산 데이터 로드 중 오류 발생: ', error);
            }
        });
    }
    $('#downloadExcel').on('click', function() {
        let hotelNo = $('#hotelNo').val();
        let storeNo = $('#storeNo').val();
        let startDate = $('#startDate').val();
        let endDate = $('#endDate').val();

        // 모든 데이터를 가져오기 위한 API 호출
        $.ajax({
            url: '/api/revenue/excel',
            type: 'GET',
            data: {
                startDate: startDate,
                endDate: endDate,
                hotelNo: hotelNo,
                storeNo: storeNo
            },
            success: function(response) {
                // 엑셀 데이터 생성
                let excelData = [['번호', '날짜', '수수료', '순수익', '총 매출', '매장 수수료', '매장 순수익', '매장 매출', '객실 수수료', '객실 순수익', '객실 매출']];
                let totalCount = response.length;

                // 총 합계를 계산하기 위한 변수들
                let totalFeeSum = 0;
                let totalDepositPriceSum = 0;
                let totalFeeWhereUserCountIsNullSum = 0;
                let totalDepositPriceWhereUserCountIsNullSum = 0;
                let totalFeeWhereUserCountIsNotNullSum = 0;
                let totalDepositPriceWhereUserCountIsNotNullSum = 0;

                // 데이터 행 처리
                response.forEach((result, index) => {
                    let rowData = [];
                    rowData.push(totalCount - index); // 번호를 내림차순으로
                    rowData.push(result[0].slice(2)); // revenueDate에서 년도의 앞자리 제거
                    rowData.push(formatNumber(result[1])); // totalFee
                    rowData.push(formatNumber(result[2])); // totalDepositPrice
                    rowData.push(formatNumber(result[1] + result[2])); // totalFee + totalDepositPrice
                    rowData.push(formatNumber(result[3])); // totalFeeWhereUserCountIsNull
                    rowData.push(formatNumber(result[4])); // totalDepositPriceWhereUserCountIsNull
                    rowData.push(formatNumber(result[3] + result[4])); // totalFeeWhereUserCountIsNull + totalDepositPriceWhereUserCountIsNull
                    rowData.push(formatNumber(result[5])); // totalFeeWhereUserCountIsNotNull
                    rowData.push(formatNumber(result[6])); // totalDepositPriceWhereUserCountIsNotNull
                    rowData.push(formatNumber(result[5] + result[6])); // totalFeeWhereUserCountIsNotNull + totalDepositPriceWhereUserCountIsNotNull
                    excelData.push(rowData);

                    // 총 합계를 계산
                    totalFeeSum += result[1];
                    totalDepositPriceSum += result[2];
                    totalFeeWhereUserCountIsNullSum += result[3];
                    totalDepositPriceWhereUserCountIsNullSum += result[4];
                    totalFeeWhereUserCountIsNotNullSum += result[5];
                    totalDepositPriceWhereUserCountIsNotNullSum += result[6];
                });

                // 총 정산 데이터 추가
                let totalRowData = [
                    '총 정산', '',
                    formatNumber(totalFeeSum),
                    formatNumber(totalDepositPriceSum),
                    formatNumber(totalFeeSum + totalDepositPriceSum),
                    formatNumber(totalFeeWhereUserCountIsNullSum),
                    formatNumber(totalDepositPriceWhereUserCountIsNullSum),
                    formatNumber(totalFeeWhereUserCountIsNullSum + totalDepositPriceWhereUserCountIsNullSum),
                    formatNumber(totalFeeWhereUserCountIsNotNullSum),
                    formatNumber(totalDepositPriceWhereUserCountIsNotNullSum),
                    formatNumber(totalFeeWhereUserCountIsNotNullSum + totalDepositPriceWhereUserCountIsNotNullSum)
                ];
                excelData.push(totalRowData);

                // 병합할 셀 정보 저장 (A열과 B열 병합)
                let mergeData = [{
                    range: 'A' + (excelData.length) + ':B' + (excelData.length),
                    value: "총 정산",
                    alignment: { horizontal: "center" }
                }];

                // 워크시트 생성
                let workbook = XLSX.utils.book_new();
                let worksheet = XLSX.utils.aoa_to_sheet(excelData);

                // 셀 병합
                mergeData.forEach(mergeInfo => {
                    if (!worksheet['!merges']) worksheet['!merges'] = [];
                    worksheet['!merges'].push(XLSX.utils.decode_range(mergeInfo.range));
                    worksheet[mergeInfo.range.split(":")[0]] = { t: 's', v: mergeInfo.value, s: { alignment: mergeInfo.alignment } };
                });

                // XLSX 라이브러리 사용하여 엑셀 파일 생성
                XLSX.utils.book_append_sheet(workbook, worksheet, '정산 정보');

                // 파일명 생성
                let fileName = `정산정보_${startDate}_${endDate}.xlsx`;

                // 엑셀 파일 다운로드
                XLSX.writeFile(workbook, fileName);
            },
            error: function(error) {
                console.error('모든 데이터 로드 중 오류 발생: ', error);
            }
        });
    });

    function formatNumber(number) {
        return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }
    function updatePagination(data) {
        console.log(data);
        let pagination = $('.pagination');
        pagination.empty();

        if (data.hasPrevious) {
            pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="1">처음</a></li>`);
            pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="${data.start - 1}">이전</a></li>`);
        }

        for (let i = data.start; i <= data.end; i++) {
            pagination.append(`<li class="page-item ${i === currentPage ? 'active' : ''}"><a class="page-link" href="#" data-page="${i}">${i}</a></li>`);
        }

        if (data.hasNext) {
            pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="${data.end + 1}">다음</a></li>`);
            pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="${data.last}">마지막</a></li>`);
        }
        $('.page-link').on('click', function(e) {
            e.preventDefault();
            let page = $(this).data('page');
            loadPage(page);
        });
    }

    $('#size').change(function() {
        pageSize = $(this).val();
        loadPage(1);
    });

    $('#searchForm').on('submit', function(e) {
        e.preventDefault();
        loadPage(1);
    });

    $('#reset').on('click', function() {
        $('#hotelNo').val('');
        $('#storeNo').val('');
        $('#startDate').val('');
        $('#endDate').val('');
        loadPage(1);
    });

    loadPage(currentPage);
});