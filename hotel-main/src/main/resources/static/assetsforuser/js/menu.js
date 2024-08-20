$(document).ready(function() {
    $(document).on('click', '.menuBtn', function(event) {
        var button = $(this);
        var menuNo = button.data('menu-no');
        var storeNo = button.data('store-no');
        var userNo = button.data('user-no');
        var menuName = button.data('menu-name');
        var imageId = '#menuImage' + menuNo;
        var imageUrl = $(imageId).attr('src');

        var modalHtml = `
    <div class="modal fade" id="cartModal${menuNo}" tabindex="-1" role="dialog" aria-labelledby="cartModalLabel${menuNo}" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="cartModalLabel${menuNo}">옵션 선택</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body d-flex">
                    <img id="modalImage${menuNo}" src="${imageUrl}" alt="" class="img-b img-fluid modal-image">
                    <div class="details">
                        <h5 id="modalMenuName${menuNo}">${menuName}</h5>
                        <select id="menuOptions${menuNo}" name="menuOptionNo" class="form-control">
                            <option value="" disabled selected>옵션을 선택하세요</option>
                        </select>
                        <div class="quantity-control mt-2 d-flex align-items-center">
                            <input type="number" id="quantity${menuNo}" class="form-control w-50" value="1" min="1">
                            <div class="btn-group ms-2" role="group">
                                <button type="button" class="btn btn-secondary minusBtn">-</button>
                                <button type="button" class="btn btn-secondary plusBtn">+</button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary addToCartBtn" data-menu-no="${menuNo}">장바구니 담기</button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                </div>
            </div>
        </div>
    </div>
`;



        $('body').append(modalHtml);
        $('#cartModal' + menuNo).modal('show');

        var menuOptionsId = '#menuOptions' + menuNo;

        $.ajax({
            url: '/user/api/menu',
            type: 'POST',
            data: { menuNo: menuNo },
            success: function(options) {
                var $menuOptions = $(menuOptionsId);
                $menuOptions.empty();
                $menuOptions.append('<option value="" disabled selected>옵션을 선택하세요</option>');
                console.log(options);
                options.forEach(function(option) {
                    console.log(option.menuOptionNo);
                    $menuOptions.append('<option name="menuOptionNo" value="' + option.menuOptionNo + '">' + option.name + ' / + ' + option.price + '원</option>');
                });
            },
            error: function(xhr, status, error) {
                console.error('옵션 가져오기 오류:', error);
            }
        });
        // 수량 조절 버튼 이벤트 핸들러 (모달 내부)
        var modalBody = $(`#cartModal${menuNo} .modal-body`);
        modalBody.on('click', '.minusBtn', function() {
            var quantityInput = $('#quantity' + menuNo);
            var currentQuantity = parseInt(quantityInput.val());
            if (currentQuantity > 1) {
                quantityInput.val(currentQuantity - 1);
            }
        });

        modalBody.on('click', '.plusBtn', function() {
            var quantityInput = $('#quantity' + menuNo);
            var currentQuantity = parseInt(quantityInput.val());
            quantityInput.val(currentQuantity + 1);
        });

        $('.addToCartBtn').click(function() {
            var menuNo = $(this).data('menu-no');
            var cartCount = $('#quantity' + menuNo).val(); // 수량 가져오기
            console.log(menuNo);
            $.ajax({
                url: '/user/menu/' + storeNo,
                type: 'POST',
                data: {
                    menuNo: menuNo,
                    userNo: userNo,
                    storeNo: storeNo,
                    menuOptionNo: $('#menuOptions' + menuNo).val(),
                    cartCount: cartCount
                },
                success: function(response) {
                    Swal.fire({
                        icon: 'success',
                        title: '장바구니 담기 성공',
                        text: '물품이 장바구니에 담겼습니다',
                        confirmButtonText: '확인'
                    }).then((result) => {
                        if (result.isConfirmed) {
                            $('#cartModal' + menuNo).modal('hide');
                        }
                    });
                },
                error: function(xhr, status, error) {
                    console.error('장바구니 담기 오류:', error);
                }
            });
        });
    });

    $('body').on('hidden.bs.modal', '.modal', function () {
        $(this).remove();
    });
});
$(document).ready(function() {
    $('.sub-category-checkbox').change(function() {
        var selectedCategory2 = $(this).val();
        var categoryContainer = $(this).closest('.row.border-black').find('.menu-container'); // 가장 가까운 상위 .row.border-black 요소 내 .menu-container 찾기
        var userNo = document.getElementById('userNo').value;
        $.ajax({
            url: '/user/api/category',
            type: 'GET',
            data: { category2: selectedCategory2 },
            success: function(data) {
                categoryContainer.empty(); // 기존 메뉴 내용 삭제

                if (data.length === 0) {
                    categoryContainer.append('<p>해당 카테고리에 메뉴가 없습니다.</p><input type="hidden" id="userNo" th:value="${userNo}" />');

                    return;
                }

                // 서버에서 받아온 데이터로 새로운 메뉴 생성
                $.each(data, function(index, menu) {
                    var menuHtml = `
                        <div class="menu-list col-md-4">
                          <div class="col-md-12 mb-4 menu-item">
                            <form th:action="@{/user/menu/{storeNo}(storeNo=${menu.storeNo})}" method="post"> 
                              <div class="card-box-b card-shadow news-box mb-lg-5">
                                <div class="img-box-b">
                                  <img id="menuImage${menu.menuNo}" src="${menu.imageUrl}" alt="" class="img-b img-fluid">
                                </div>
                                <div class="card-overlay">
                                  <div class="card-header-b">
                                    <div class="card-category-b">
                                      <a href="#" class="category-b">${menu.category1Name} - ${menu.category2Name}</a>
                                    </div>
                                    <div class="card-title-b">
                                      <h2 class="title-2">
                                        <span>${menu.name}</span><br>
                                        <span>${menu.price} 원</span>
                                      </h2>
                                    </div>
                                    <div>
                                      <button type="button" id="menuBtn${menu.menuNo}"
                                              data-menu-no="${menu.menuNo}"
                                              data-menu-name="${menu.name}"
                                              data-user-no="${userNo}"
                                              data-store-no="${menu.storeNo}"
                                              class="btn btn-b rounded-2 menuBtn">
                                              <input type="hidden" id="userNo" th:value="${userNo}" />
                                              옵션 선택</button>
                                    </div>
                                  </div>
                                </div>
                              </div>
                              </form>
                          </div>
                        </div>
                      `;
                    categoryContainer.append(menuHtml);
                });
            },
            error: function() {
                alert('메뉴를 불러오는 데 실패했습니다.');
            }
        });
    });
});