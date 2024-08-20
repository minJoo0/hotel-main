document.addEventListener('DOMContentLoaded', function () {
    const confirmDeliveryModal = new bootstrap.Modal(document.getElementById('confirmDeliveryModal'));
    const confirmDeliveryBtn = document.getElementById('confirmDeliveryBtn');
    let selectedPaymentId = null;

    // 각 배송 확인 버튼에 이벤트 리스너를 추가
    document.querySelectorAll('.confirm-delivery-btn').forEach(button => {
        button.addEventListener('click', function (event) {
            event.preventDefault();
            selectedPaymentId = this.getAttribute('data-payment-id');
            confirmDeliveryModal.show();
        });
    });

    // 모달의 확인 버튼 클릭 시 처리
    confirmDeliveryBtn.addEventListener('click', function () {
        if (selectedPaymentId) {
            // 배송 완료 처리 로직을 여기에 추가하세요
            console.log(`배송 확인: 결제번호 ${selectedPaymentId}`);

// 예를 들어 AJAX로 배송 완료를 처리할 수 있습니다.
            fetch(`/api/completeDelivery`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ paymentNo: selectedPaymentId })
            })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    } else {
                        throw new Error('결제 상태를 확인해주세요');
                    }
                })
                .then(data => {
                    if (data.success) {
                        alert('배송이 완료되었습니다.');
                        location.reload(); // 페이지를 새로고침하여 변경사항을 반영
                    } else {
                        alert('배송 완료 처리에 실패했습니다.');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('결제 상태를 확인해주세요');
                });

            confirmDeliveryModal.hide(); // 모달을 닫습니다.
            selectedPaymentId = null; // 상태 초기화
        }
    });
});