package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.Service.MenuOptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Log4j2
@Tag(name = "Rest 메뉴 옵션", description = "메뉴 옵션 Rest API")
@RestController
public class RestMenuOptionController {

    private final MenuOptionService menuOptionService;

    @Operation(summary = "메뉴 옵션 삭제",
            description = "메뉴 옵션을 삭제하고 결과를 반환한다.")
    @DeleteMapping("/manager/menu/option/delete/{menuOptionNo}")
    public ResponseEntity<?> deleteMenuOption(@PathVariable("menuOptionNo") Long menuOptionNo) {
        log.info("메뉴 옵션 삭제 처리...");
        try {
            menuOptionService.delete(menuOptionNo);
            return ResponseEntity.ok().body("메뉴 옵션 삭제 성공");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("메뉴 옵션 삭제 실패");
        }
    }
}
