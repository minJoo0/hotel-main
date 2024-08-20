function formatDate(date) {
    var d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2)
        month = '0' + month;
    if (day.length < 2)
        day = '0' + day;

    return [year, month, day].join('-');
}
$(document).ready(function() {
    var isSearchActive1 = false; // 검색 상태를 추적하는 변수
    var isSearchActive2 = false; // 검색 상태를 추적하는 변수
        function fetchCategories1(page = 1, size = 10) {
            var name = $('#category1').val(); // jQuery를 사용하여 값을 가져옵니다.
            var hotelNo = $('#hotelNo').val();
            var storeNo = $('#storeNo').val();
            if(isSearchActive1){
                console.log(isSearchActive1);
                $.ajax({
                    url: `/api/list?&hotelNo=${hotelNo}&storeNo=${storeNo}&name=${name}&size=${size}&search=`,
                    method: 'GET',
                    dataType: 'json', // 응답 데이터 타입
                    data:{
                        page: page
                    },
                    success: function(data) {
                        // 검색 결과와 페이징 정보 표시
                        console.log(data);
                        function loadNewCategories(page) {
                            $.ajax({
                                url: `/api/list?&hotelNo=${hotelNo}&storeNo=${storeNo}&name=${name}&size=${size}&search=`,
                                type: 'GET',
                                dataType: 'json',
                                data: {
                                    page: page
                                },
                                success: function(data) {
                                    console.log("page =" + data);
                                    var $newCategoriesBody = $('#newCategoriesBody');
                                    $newCategoriesBody.empty();
                                    var $categoriesBody = $('#categories-body');
                                    $categoriesBody.empty();

                                    if (data.dtoList && data.dtoList.length > 0) {
                                        $.each(data.dtoList, function(i, category) {
                                            var formattedDate = formatDate(new Date(category.regDate));
                                            $newCategoriesBody.append('<tr><td>' + (i + 1) + '</td><td>' + category.hotelName +
                                                '</td><td>' + category.storeName + '</td><td>' + category.name + '</td>' +
                                                '<td>'+formattedDate+'</td><td><button class="delete-button-category" data-category-id="' +
                                                category.categoryNo + '">삭제</button></td>' +
                                                '</tr>');
                                        });
                                    } else {
                                        $newCategoriesBody.append('<tr><td colspan="6">카테고리 정보가 없습니다.</td></tr>');
                                    }
                                },
                                error: function(jqXHR, textStatus, errorThrown) {
                                    console.error("AJAX 요청 실패: ", textStatus, errorThrown);
                                }
                            });
                        }

                        function loadNewPage(page) {
                            $.ajax({
                                url: `/api/list?&hotelNo=${hotelNo}&storeNo=${storeNo}&name=${name}&size=${size}&search=`,
                                type: 'GET',
                                data: {
                                    page: page
                                },
                                success: function(data) {
                                    var pagination = $('#newPagination1');
                                    pagination.empty(); // 페이징 버튼 초기화
                                    var pagination1 = $('#pagination1');
                                    pagination1.empty();

                                    if (data.prev) {
                                        pagination.append('<li class="page-item"><a class="page-link page-link-custom" href="#" data-page="1">처음으로</a></li>');
                                        pagination.append('<li class="page-item"><a class="page-link page-link-custom" href="#" data-page="' + (data.page - 1) + '">이전</a></li>');
                                    }

                                    for (var i = data.start; i <= data.end; i++) {
                                        if (i === data.page) {
                                            pagination.append('<li class="page-item active"><span class="page-link page-link-custom">' + i + '</span></li>');
                                        } else {
                                            pagination.append('<li class="page-item"><a class="page-link page-link-custom" href="#" data-page="' + i + '">' + i + '</a></li>');
                                        }
                                    }

                                    if (data.next) {
                                        pagination.append('<li class="page-item"><a class="page-link page-link-custom" href="#" data-page="' + (data.page + 1) + '">다음</a></li>');
                                        pagination.append('<li class="page-item"><a class="page-link page-link-custom" href="#" data-page="' + data.last + '">마지막</a></li>');
                                    }

                                    loadNewCategories(page); // 페이지네이션 버튼 클릭 후 해당 페이지 데이터 로드
                                },
                                error: function(err) {
                                    console.error('페이지 로드 중 에러 발생', err);
                                }
                            });
                            loadNewCategories(page);
                        }
                        $(document).on('click', '.page-link-custom', function(e) {
                            e.preventDefault();
                            var page = $(this).data('page');
                            loadNewPage(page);
                        });
                        loadNewCategories(page);
                        loadNewPage(page);
                    },
                    error: function(xhr, status, error) {
                        console.error('Error:', error);
                    }
                });}

        }
    function fetchCategories2(page = 1, size = 10) {
        var parentId = $('#name1').val(); // jQuery를 사용하여 값을 가져옵니다.
        var name = $('#name2').val()
        var hotelNo = $('#hotelNo2').val();
        var storeNo = $('#storeNo2').val();

        if(isSearchActive2){
            console.log(isSearchActive2);
            $.ajax({
                url: `/api/list?&hotelNo=${hotelNo}&storeNo=${storeNo}&parentId=${parentId}&size=${size}&search=`,
                method: 'GET',
                dataType: 'json', // 응답 데이터 타입
                data:{
                    page: page
                },
                success: function(data) {
                    // 검색 결과와 페이징 정보 표시
                    console.log(data);
                    function loadNewCategories(page) {
                        $.ajax({
                            url: `/api/list2?&hotelNo=${hotelNo}&storeNo=${storeNo}&parentId=${parentId}&size=${size}&search=`,
                            type: 'GET',
                            dataType: 'json',
                            data: {
                                page: page
                            },
                            success: function(data) {
                                console.log("page =" + data);
                                var $newCategoriesBody = $('#newCategoriesBody2');
                                $newCategoriesBody.empty();
                                var $categoriesBody = $('#categories-body2');
                                $categoriesBody.empty();

                                if (data.dtoList && data.dtoList.length > 0) {
                                    $.each(data.dtoList, function(i, category) {
                                        var formattedDate = formatDate(new Date(category.regDate));
                                        $newCategoriesBody.append('<tr><td>' + (i + 1) + '</td><td>' + category.storeName + '</td><td>' + category.pname + '</td><td>' + category.name + '</td>' +
                                            '<td>'+formattedDate+'</td><td><button class="delete-button-category" data-category-id="' + category.categoryNo + '">삭제</button></td>' +
                                            '</tr>');
                                    });
                                } else {
                                    $newCategoriesBody.append('<tr><td colspan="6">카테고리 정보가 없습니다.</td></tr>');
                                }
                            },
                            error: function(jqXHR, textStatus, errorThrown) {
                                console.error("AJAX 요청 실패: ", textStatus, errorThrown);
                            }
                        });
                    }

                    function loadNewPage(page) {
                        $.ajax({
                            url: `/api/list2?&hotelNo=${hotelNo}&storeNo=${storeNo}&name=${name}&size=${size}&search=`,
                            type: 'GET',
                            data: {
                                page: page
                            },
                            success: function(data) {
                                var pagination = $('#newPagination2');
                                pagination.empty(); // 페이징 버튼 초기화
                                var pagination1 = $('#pagination2');
                                pagination1.empty();

                                if (data.prev) {
                                    pagination.append('<li class="page-item"><a class="page-link page-link-custom2" href="#" data-page="1">처음으로</a></li>');
                                    pagination.append('<li class="page-item"><a class="page-link page-link-custom2" href="#" data-page="' + (data.page - 1) + '">이전</a></li>');
                                }

                                for (var i = data.start; i <= data.end; i++) {
                                    if (i === data.page) {
                                        pagination.append('<li class="page-item active"><span class="page-link page-link-custom2">' + i + '</span></li>');
                                    } else {
                                        pagination.append('<li class="page-item"><a class="page-link page-link-custom2" href="#" data-page="' + i + '">' + i + '</a></li>');
                                    }
                                }

                                if (data.next) {
                                    pagination.append('<li class="page-item"><a class="page-link page-link-custom2" href="#" data-page="' + (data.page + 1) + '">다음</a></li>');
                                    pagination.append('<li class="page-item"><a class="page-link page-link-custom2" href="#" data-page="' + data.last + '">마지막</a></li>');
                                }

                                loadNewCategories(page); // 페이지네이션 버튼 클릭 후 해당 페이지 데이터 로드
                            },
                            error: function(err) {
                                console.error('페이지 로드 중 에러 발생', err);12345
                            }
                        });
                        loadNewCategories(page);
                    }
                    $(document).on('click', '.page-link-custom2', function(e) {
                        e.preventDefault();
                        var page = $(this).data('page');
                        loadNewPage(page);
                    });
                    loadNewCategories(page);
                    loadNewPage(page);
                },
                error: function(xhr, status, error) {
                    console.error('Error:', error);
                }
            });}

    }
    $('#searchForm1').submit(function(e) {
        e.preventDefault();
        isSearchActive1 = true;
        fetchCategories1();
    });
    $('#searchForm2').submit(function(e) {
        e.preventDefault();
        isSearchActive2 = true;
        fetchCategories2();
    });

        function loadCategories(page) {
            console.log(isSearchActive1);
            $.ajax({
                url: '/api/categories',
                type: "GET",
                dataType: "json",
                data: {
                    page: page
                },
                success: function(data) {
                    var $categoriesBody = $('#categories-body');
                    $categoriesBody.empty();
                    var $newCategoriesBody = $('#newCategoriesBody');
                    $newCategoriesBody.empty();

                    if (data.dtoList && data.dtoList.length > 0) {
                        $.each(data.dtoList, function(i, category) {
                            var formattedDate = formatDate(new Date(category.regDate));
                            $categoriesBody.append('<tr><td>' + (i + 1) + '</td><td>' + category.hotelName + '</td><td>' + category.storeName + '</td><td>' + category.name + '</td>' +
                                '<td>'+formattedDate+'</td><td><button type="button" class="delete-button" data-category-id="' + category.categoryNo + '" ' +
                                'data-page="' + page + '">삭제</button></td>' +
                                '</tr>');
                        });
                    } else {
                        $categoriesBody.append('<tr><td colspan="6">카테고리 정보가 없습니다.</td></tr>');
                    }
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.error("AJAX 요청 실패: ", textStatus, errorThrown);
                }
            });
            }

    function loadCategories2(page2) {
        $.ajax({
            url: '/api/categories2',
            type: "GET",
            dataType: "json",
            data: {
                page: page2
            },
            success: function(data2) {
                var $categoriesBody2 = $('#categories-body2');
                $categoriesBody2.empty();
                var $newCategoriesBody = $('#newCategoriesBody2');
                $newCategoriesBody.empty();

                if (data2.dtoList && data2.dtoList.length > 0) {
                    $.each(data2.dtoList, function(i, category2) {
                        var formattedDate2 = formatDate(new Date(category2.regDate));
                        $categoriesBody2.append('<tr><td>' + (i + 1) + '</td><td>' + category2.storeName + '</td><td>' + category2.pname + '</td><td>' + category2.name + '</td>' +
                            '<td>'+formattedDate2+'</td><td><button type="button" class="delete-button-category2" data-category-id="' + category2.categoryNo + '"' +
                            ' data-page2="' + page2 + '">삭제</button></td>' +
                            '</tr>');
                    });
                } else {
                    $categoriesBody2.append('<tr><td colspan="6">카테고리 정보가 없습니다.</td></tr>');
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.error("AJAX 요청 실패: ", textStatus, errorThrown);
            }
        });
    }

    function loadPage(page) {
        var params = {
            page: page,
            // 추가적인 파라미터 설정
        };

        $.ajax({
            url: '/api/categories',
            type: 'GET',
            data: params,
            success: function(data) {
                var pagination1 = $('#pagination1');
                pagination1.empty(); // 페이징 버튼 초기화
                var pagination3 = $('#newPagination1');
                pagination3.empty(); // 페이징 버튼 초기화

                if (data.prev) {
                    // '처음으로' 링크에는 항상 1 페이지를 로드하도록 설정
                    pagination1.append('<li class="page-item"><a class="page-link page-link-custom" href="#" data-page="1">처음으로</a></li>');
                    // '이전' 링크에는 현재 페이지에서 1을 뺀 페이지를 로드하도록 설정
                    pagination1.append('<li class="page-item"><a class="page-link page-link-custom" href="#" data-page="' + (data.page - 1) + '">이전</a></li>');
                }

                for (var i = data.start; i <= data.end; i++) {
                    if (i === data.page) {
                        pagination1.append('<li class="page-item active"><span class="page-link page-link-custom">' + i + '</span></li>');
                    } else {
                        // 각 페이지 번호에 해당하는 링크에 data-page 속성을 설정
                        pagination1.append('<li class="page-item"><a class="page-link page-link-custom" href="#" data-page="' + i + '">' + i + '</a></li>');
                    }
                }

                if (data.next) {
                    // '다음' 링크에는 현재 페이지에서 1을 더한 페이지를 로드하도록 설정
                    pagination1.append('<li class="page-item"><a class="page-link page-link-custom" href="#" data-page="' + (data.page + 1) + '">다음</a></li>');
                    // '마지막' 링크에는 마지막 페이지 번호를 로드하도록 설정
                    pagination1.append('<li class="page-item"><a class="page-link page-link-custom" href="#" data-page="' + data.last + '">마지막</a></li>');
                }

                loadCategories(page); // 페이지네이션 버튼 클릭 후 해당 페이지 데이터 로드
            },
            error: function(err) {
                console.error('페이지 로드 중 에러 발생', err);
            }
        });
    }
    function loadPage2(page2) {
        var params2 = {
            page: page2,
            // 추가적인 파라미터 설정
        };

        $.ajax({
            url: '/api/categories2',
            type: 'GET',
            data: params2,
            success: function(data2) {
                var pagination2 = $('#pagination2');
                pagination2.empty(); // 페이징 버튼 초기화
                var pagination3 = $('#newPagination2');
                pagination3.empty(); // 페이징 버튼 초기화
                if (data2.prev) {
                    // '처음으로' 링크에는 항상 1 페이지를 로드하도록 설정
                    pagination2.append('<li class="page-item"><a class="page-link page-link-custom2" href="#" data-page2="1">처음으로</a></li>');
                    // '이전' 링크에는 현재 페이지에서 1을 뺀 페이지를 로드하도록 설정
                    pagination2.append('<li class="page-item"><a class="page-link page-link-custom2" href="#" data-page2="' + (data2.page - 1) + '">이전</a></li>');
                }

                for (var i = data2.start; i <= data2.end; i++) {
                    if (i === data2.page) {
                        pagination2.append('<li class="page-item active"><span class="page-link page-link-custom2">' + i + '</span></li>');
                    } else {
                        // 각 페이지 번호에 해당하는 링크에 data-page 속성을 설정
                        pagination2.append('<li class="page-item"><a class="page-link page-link-custom2" href="#" data-page2="' + i + '">' + i + '</a></li>');
                    }
                }

                if (data2.next) {
                    // '다음' 링크에는 현재 페이지에서 1을 더한 페이지를 로드하도록 설정
                    pagination2.append('<li class="page-item"><a class="page-link page-link-custom2" href="#" data-page2="' + (data2.page + 1) + '">다음</a></li>');
                    // '마지막' 링크에는 마지막 페이지 번호를 로드하도록 설정
                    pagination2.append('<li class="page-item"><a class="page-link page-link-custom2" href="#" data-page2="' + data2.last + '">마지막</a></li>');
                }

                loadCategories2(page2); // 페이지네이션 버튼 클릭 후 해당 페이지 데이터 로드
            },
            error: function(err) {
                console.error('페이지 로드 중 에러 발생', err);
            }
        });
    }
    $(document).on('click', '.delete-button-category2', function() {
        var categoryId = $(this).data('category-id'); // 버튼의 data-category-id 속성에서 카테고리 ID를 가져옵니다.
        var page2 = $(this).data('page2');
        $('#hiddenCodeNo').val(categoryId); // 모달 내부의 hidden input에 해당 카테고리 ID를 설정합니다.
        $('#page2').val(page2);
        console.log(page2);
        $('#deleteConfirmModal').modal('show'); // 모달 열기
    });

    $(document).on('click', '.delete-button', function() {
        var categoryId = $(this).data('category-id'); // 버튼의 data-category-id 속성에서 카테고리 ID를 가져옵니다.
        var page = $(this).data('page');
        $('#hiddenCodeNo').val(categoryId); // 모달 내부의 hidden input에 해당 카테고리 ID를 설정합니다.
        $('#page').val(page);
        console.log(page);
        $('#deleteConfirmModal').modal('show'); // 모달 열기
    });

    $('#deleteConfirmModal .btn-danger').on('click', function() {
        var categoryId = $('#hiddenCodeNo').val(); // hidden input에서 카테고리 ID를 가져옵니다.
        var page2 = $('#page2').val(); // hidden input에서 page2 값을 가져옵니다.
        var page = $('#page').val(); // hidden input에서 page 값을 가져옵니다.
        console.log("전달된" + page2);
        console.log("전달된" + page);

        function isValid(value) {
            return value !== null && value !== undefined && value !== '';
        }

        // AJAX 요청을 통해 서버에 카테고리 삭제를 요청합니다.
        $.ajax({
            url: '/manager/menu/category/delete/' + categoryId, // 실제 URL은 서버 API 설계에 따라 다를 수 있습니다.
            type: 'DELETE', // HTTP DELETE 메서드를 사용하여 해당 카테고리를 삭제합니다.
            success: function(result) {
                // 성공적으로 삭제되면, 페이지를 새로 고침하거나 변경된 데이터로 UI를 업데이트합니다.
                alert('카테고리가 성공적으로 삭제되었습니다.');
                $('#deleteConfirmModal').modal('hide'); // 모달 닫기

                // page가 유효한 경우 loadCategories 호출, 그렇지 않으면 page2 호출
                if (isValid(page)) {
                    loadCategories(page);
                } else if (isValid(page2)) {
                    loadCategories2(page2);
                } else {
                    alert('페이지 정보가 없습니다.');
                }
            },
            error: function(err) {
                console.error('카테고리 삭제 중 에러 발생', err);
                alert('카테고리 삭제 중 문제가 발생했습니다.');
            }
        });
    });

    $(document).on('click', '.page-link-custom', function(e) {
        e.preventDefault();
        var page = $(this).data('page');
        loadPage(page);
    });

    loadPage(1);

    $(document).on('click', '.page-link-custom2', function(e) {
        e.preventDefault();
        var page2 = $(this).data('page2');
        loadPage2(page2);
    });

    loadPage2(1);// 초기 페이지 로드

    $(document).ready(function() {
        // 초기화 버튼에 클릭 이벤트 리스너 추가
        $('#reset1').click(function() {
            // 페이지와 카테고리 초기화 함수 호출 (이전 답변에서 제공한 코드)
            loadPage(1);
            loadCategories(1);

            // 호텔명 선택 목록 초기화
            $('#hotelNo').val('');

            // 매장명 선택 목록 초기화
            $('#storeNo').val('');
            $('#name').val('');

            // 호텔명과 매장명 선택 목록 각각에 대해 초기화 후, 변경 사항을 UI에 반영하기 위해 change 이벤트를 강제로 발생시킴
            $('#hotelNo').trigger('change');
            $('#storeNo').trigger('change');
        });
        $('#reset2').click(function() {
            // 페이지와 카테고리 초기화 함수 호출 (이전 답변에서 제공한 코드)
            loadPage2(1);
            loadCategories2(1);

            // 호텔명 선택 목록 초기화
            $('#hotelNo2').val('');

            // 매장명 선택 목록 초기화
            $('#storeNo2').val('');
            $('#name1').val('');
            $('#name2').val('');

            // 호텔명과 매장명 선택 목록 각각에 대해 초기화 후, 변경 사항을 UI에 반영하기 위해 change 이벤트를 강제로 발생시킴
            $('#hotelNo2').trigger('change');
            $('#storeNo2').trigger('change');
            $('#name1').trigger('change');
        });
    });


});
function loadHotelsByRegion() {
    var selectedHotel = $("#hotelNo").val();

    $.ajax({
        url: `/api/menus?hotelNo=${selectedHotel}`,
        type: 'GET',
        dataType: 'json',
        success: function(menus) {
            console.log(menus);
            var storeSelect = $("#storeNo");
            storeSelect.empty(); // 매장 선택 초기화
            storeSelect.append('<option value="">매장 선택</option>');

            $.each(menus, function(i, menu) {
                storeSelect.append($('<option>', {
                    value: menu.storeNo,
                    text: menu.name
                }));
            });

            storeSelect.change(function() {
                $("#hiddenStoreNo").val($(this).val());
            });
        },
        error: function(xhr, status, error) {
            console.error("Error fetching menus:", status, error);
        }
    });
}

function loadCategoryByStoreNo() {
    var selectedStore = $("#storeNo").val();
    console.log(selectedStore);

    $.ajax({
        url: `/api/category?storeNo=${selectedStore}`,
        type: 'GET',
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
            category1.change(function() {
                $("#hiddenParentId").val($(this).val());
            });
        },
        error: function(xhr, status, error) {
            console.error("Error fetching categories:", status, error);
        }
    });
}
function loadHotelsByRegion2() {
    var selectedHotel = $("#hotelNo2").val();

    $.ajax({
        url: `/api/menus?hotelNo=${selectedHotel}`,
        type: 'GET',
        dataType: 'json',
        success: function(menus) {
            console.log(menus);
            var storeSelect = $("#storeNo2");
            storeSelect.empty(); // 매장 선택 초기화
            storeSelect.append('<option value="">매장 선택</option>');

            $.each(menus, function(i, menu) {
                storeSelect.append($('<option>', {
                    value: menu.storeNo,
                    text: menu.name
                }));
            });
        },
        error: function(xhr, status, error) {
            console.error("Error fetching menus:", status, error);
        }
    });
}
function loadCategoryByStoreNo2() {
    var selectedStore = $("#storeNo2").val();
    console.log(selectedStore);

    $.ajax({
        url: `/api/category?storeNo=${selectedStore}`,
        type: 'GET',
        dataType: 'json',
        success: function(categories) {
            console.log(categories);
            var category1 = $('#name1');
            category1.empty(); // 1차 카테고리 선택 초기화
            category1.append('<option value="">1차 카테고리 선택</option>');

            $.each(categories, function(i, category) {
                category1.append($('<option>', {
                    value: category.categoryNo,
                    text: category.name
                }));
            });
            category1.change(function() {
                $("#hiddenParentId").val($(this).val());
            });
        },
        error: function(xhr, status, error) {
            console.error("Error fetching categories:", status, error);
        }
    });
}


$(document).ready(function() {
    $("#hotelNo2").change(loadHotelsByRegion2);
    $("#storeNo2").change(loadCategoryByStoreNo2);
});

document.addEventListener('DOMContentLoaded', function() { // DOM이 완전히 로드되었을 때 실행
    var hotelSelect = document.getElementById('hotelNo'); // 'hotelNo' id를 가진 요소 선택
    hotelSelect.addEventListener('change', loadHotelsByRegion); // 'change' 이벤트에 대한 리스너 추가
});


