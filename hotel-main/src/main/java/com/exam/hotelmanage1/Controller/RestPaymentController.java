package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.Service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Log4j2
@Tag(name = "Rest 결제", description = "결제 Rest API")
@RestController
public class RestPaymentController {

    private final PaymentService paymentService;
    @Operation(summary = "결제 완료된 item 의 배송 상태를 설정하는 API",
            description = "")
    @PostMapping("/api/completeDelivery")
    public ResponseEntity<Map<String, Object>> completeDelivery(@RequestBody Map<String, String> request) {
        // request에서 paymentNo를 가져와서 Long 타입으로 변환합니다.
        Long paymentNo = Long.parseLong(request.get("paymentNo"));
        Map<String, Object> response = new HashMap<>();

        try {
            // paymentService의 updatePaymentStatusToDelivery 메소드를 호출하여 결제 상태를 배송 완료로 업데이트합니다.
            // 이 메소드의 반환 타입이 boolean이라고 가정합니다.
            boolean success = paymentService.updatePaymentStatusToDelivery(paymentNo);

            response.put("success", success);
            if (success) {
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "배송 완료 처리에 실패했습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (NumberFormatException e) {
            // paymentNo 변환 중 예외가 발생했을 경우, 적절한 에러 메시지와 함께 응답합니다.
            response.put("success", false);
            response.put("message", "잘못된 결제 번호 형식입니다.");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            // 기타 예외 처리
            response.put("success", false);
            response.put("message", "배송 완료 처리 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}