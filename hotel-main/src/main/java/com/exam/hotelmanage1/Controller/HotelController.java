package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.Constant.HotelType;
import com.exam.hotelmanage1.DTO.*;
import com.exam.hotelmanage1.Entity.HotelImgEntity;
import com.exam.hotelmanage1.Repository.HotelImgRepository;
import com.exam.hotelmanage1.Repository.HotelRepository;
import com.exam.hotelmanage1.Service.*;
import com.exam.hotelmanage1.Util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Tag(name = "호텔", description = "호텔 관리 API")
@Log4j2
public class HotelController {
    private final HotelService hotelService;
    private final StoreService storeService;
    private final AuthService authService;
    private final RoomService roomService;
    private final AdminNoCheckService adminNoCheckService;
    private final HotelImgService hotelImgService;
    private final FTPService ftpService;
    private final HotelImgRepository hotelImgRepository;
    @ModelAttribute("hotelTypes")
    public HotelType[] hotelTypes(){
        return HotelType.values();
    }
    @ModelAttribute("sido")
    public List<String> HotelSidos(Principal principal){
        List<String> sidos= hotelService.HotelSidos(principal);
        log.info(sidos);
        return sidos;
    }

    private final HotelRepository hotelRepository;


    // 삽입
    @Operation(summary = "호텔 등록폼",
            description = "회사번호 값을 보낸 후 호텔등록 페이지로 이동")
    @GetMapping("/manager/hotel/register")
    public String registerForm(Model model,Principal principal) {
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        model.addAttribute("adminNo",adminNo);
        return "manager/hotel/register";
    }

    @Operation(summary = "호텔 등록처리",
            description = "호텔 정보와 이미지를 등록 후 호텔 목록 페이지로 이동")
    @PostMapping("/manager/hotel/register")
    public String registerProc(@Valid HotelDTO hotelDTO,
                               @RequestParam("file") MultipartFile file)
            throws Exception
    {
        Long hotelNo = hotelService.insert(hotelDTO).getHotelNo();
        hotelDTO.setHotelNo(hotelNo);
        hotelImgService.saveHotelImg(hotelDTO,file);

        return "redirect:/manager/hotel/list";
    }


    // 수정
    @Operation(summary = "호텔 정보 수정폼",
            description = "수정 하려는 호텔, 호텔 목록, 이미지url 값을 보낸 후 호텔 수정페이지로 이동")
    @GetMapping("/manager/hotel/update/{hotelNo}")
    public String updateForm(@PathVariable("hotelNo") Long hotelNo,
                             Model model,
                             PageRequestDTO pageRequestDTO,
                             @RequestParam("imageUrl") String imageUrl) {
        log.info("호텔 수정 Form 이동...");
        HotelDTO hotelDTO = hotelService.read(hotelNo);
        PageResponseDTO<HotelDTO> responseDTO = hotelService.list(hotelDTO,pageRequestDTO);
        model.addAttribute("hotelDTO", hotelDTO);
        model.addAttribute("responseDTO",responseDTO);
        model.addAttribute("imageUrl",imageUrl);
        return "manager/hotel/update";
    }

    @Operation(summary = "호텔 정보 수정처리",
            description = "호텔 정보를 수정 후 호텔 목록페이지로 이동")
    @PostMapping("/manager/hotel/update")
    public String updateProc(@ModelAttribute HotelDTO hotelDTO,
                             @RequestParam(value = "file", required = false) MultipartFile file)
            throws Exception
    {
        log.info("호텔 수정 Proc 처리...");
        hotelService.update(hotelDTO);
        List<HotelImgEntity> hotelImgEntities = hotelImgService.findByHotelEntityHotelNo(hotelDTO.getHotelNo());
        if (file != null && !file.isEmpty()) {
            // 이미지 파일이 제공되었다면, 이미지 파일을 저장하고 관련 정보를 업데이트합니다.
            ftpService.deleteFiles(hotelImgEntities);
            hotelImgRepository.deleteAll(hotelImgEntities);
            hotelImgService.saveHotelImg(hotelDTO,file);
        }
        return "redirect:/manager/hotel/list";
    }


    // 삭제
    @Operation(summary = "호텔 정보 삭제 처리",
            description = "호텔 정보를 삭제 후 호텔 목록 페이지로 이동")
    @PostMapping("/manager/hotel/delete/{hotelNo}")
    public String deleteProc(@PathVariable("hotelNo") Long hotelNo,
                             RedirectAttributes redirectAttributes) {
        log.info("호텔 삭제 Proc 처리...");
        hotelService.delete(hotelNo);
        redirectAttributes.addFlashAttribute("processMessage", "삭제하였습니다.");
        return "redirect:/manager/hotel/list";
    }


    // 전체조회
    @Operation(summary = "호텔 목록",
            description = "페이징+검색 처리 된 호텔 목록 값을 보낸 후 호텔 목록 페이지로 이동")
    @GetMapping("/manager/hotel/list")
    public String selectForm(Model model,
                             HotelDTO hotelDTO,
                             PageRequestDTO pageRequestDTO,
                             Principal principal) {
        // 현재 접속한 계정의 adminNo를 확인
        Long adminNo = adminNoCheckService.adminNocheck(principal);

        if(authService.isAdmin()){  //최고 관리자인 경우
            hotelDTO.setAdminNo(null);
        }else {                     //최고 관리자가 아닌 경우 현재 접속한 사용자의 adminNo를 설정
            hotelDTO.setAdminNo(adminNo);
        }
        SecurityUtil.logCurrentUserAuthority();

        PageResponseDTO<HotelDTO> hotelDTOS = hotelService.list(hotelDTO,pageRequestDTO);
        model.addAttribute("list", hotelDTOS);
        log.info(hotelDTOS);

        return "manager/hotel/list";
    }


    // 상세조회
    @Operation(summary = "호텔 상세 정보",
            description = "호텔에 있는 매장 갯수, 객실 갯수 등의 값을 보낸 후 호텔 상세 정보 페이지로 이동")
    @GetMapping("/manager/hotel/read/{hotelNo}")
    public String readForm(@PathVariable(name = "hotelNo") Long hotelNo,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        HotelDTO hotelDTO = hotelService.read(hotelNo);
        //호텔에 매장이 몇개 있는지 카운트
        Long storeCount = storeService.countStoresByHotelNo(hotelNo);
        //호텔에 방이 몇개 있는지 카운트
        Long roomCount = roomService.countRoom(hotelNo);
        List<HotelImgEntity> hotelImgEntityList = hotelImgService.findByHotelEntityHotelNo(hotelNo);
        String imageUrl = "";
        if (!hotelImgEntityList.isEmpty()) {
            HotelImgEntity firstMenuImg = hotelImgEntityList.get(0);

            imageUrl = "http://woori-prn.iptime.org/project/data/" + firstMenuImg.getImgName(); // 웹 경로 수정

        }

        model.addAttribute("imageUrl", imageUrl);
        model.addAttribute("data", hotelDTO);
        model.addAttribute("storeCount", storeCount);
        model.addAttribute("roomCount", roomCount);
        return "manager/hotel/read";
    }


}
