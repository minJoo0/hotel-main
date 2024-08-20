package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.Constant.StoreType;
import com.exam.hotelmanage1.DTO.PageRequestDTO;
import com.exam.hotelmanage1.DTO.PageResponseDTO;
import com.exam.hotelmanage1.DTO.StoreDTO;
import com.exam.hotelmanage1.Entity.*;
import com.exam.hotelmanage1.Repository.HotelRepository;
import com.exam.hotelmanage1.Repository.StoreImgRepository;
import com.exam.hotelmanage1.Service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Tag(name = "매장", description = "매장 관리 API")
@Log4j2
public class StoreController {
    private final StoreService storeService;
    private final ModelMapper modelMapper;
    private final HotelService hotelService;
    private final HotelRepository hotelRepository;
    private final AuthService authService;
    private final AdminNoCheckService adminNoCheckService;
    private final StoreImgService storeImgService;
    private final FTPService ftpService;
    private final StoreImgRepository storeImgRepository;

    @ModelAttribute("storeTypes")
    public StoreType[] storeTypes(){
        return StoreType.values();
    }

    @ModelAttribute("sido")
    public List<String> HotelSidos(Principal principal){
        List<String> sidos= hotelService.HotelSidos(principal);
        log.info(sidos);
        return sidos;
    }

    // 삽입
    @Operation(summary = "매장 등록폼",
            description = "매장 등록 페이지로 이동")
    @GetMapping("/manager/store/register")
    public String registerForm(Model model) {

        return "manager/store/register";
    }

    @Operation(summary = "매장 등록 처리",
            description = "매장 등록 후 매장 목록 페이지로 이동")
    @PostMapping("/manager/store/register")
    public String registerProc(@RequestParam("hotelNo") Long hotelNo,
                               @ModelAttribute StoreDTO storeDTO,
                               @RequestParam("file") MultipartFile file
                               ) throws Exception {
        Long storeNo = storeService.insert(storeDTO).getStoreNo();
        storeDTO.setStoreNo(storeNo);
        storeImgService.saveStoreImg(storeDTO,file);
        return "redirect:/manager/store/list";
    }


    // 수정
    @Operation(summary = "매장 정보 수정폼",
            description = "수정 할 매장 정보를 보낸 후 매장 수정 페이지로 이동")
    @GetMapping("/manager/store/update/{storeNo}")
    public String updateForm(@PathVariable("storeNo") Long storeNo,
                             Model model,
                             PageRequestDTO pageRequestDTO) {
        log.info("매장 수정 Form 이동...");
        StoreDTO storeDTO = storeService.read(storeNo);
        PageResponseDTO<StoreDTO> responseDTO = storeService.list(storeDTO,pageRequestDTO);
        model.addAttribute("storeDTO",storeDTO);
        model.addAttribute("responseDTO", responseDTO);
        return "manager/store/update";
    }

    @Operation(summary = "매장 정보 수정 처리",
            description = "매장 정보 수정 후 매장 목록 페이지로 이동")
    @PostMapping("/manager/store/update")
    public String updateProc(@ModelAttribute StoreDTO storeDTO,
                             @RequestParam("file") MultipartFile file
                             ) throws Exception {
        log.info("매장 수정 Proc 처리...");
        storeService.update(storeDTO);
        List<StoreImgEntity> storeImgEntities = storeImgService.findByStoreEntityStoreNo(storeDTO.getStoreNo());
        if (file != null && !file.isEmpty()) {
            // 이미지 파일이 제공되었다면, 이미지 파일을 저장하고 관련 정보를 업데이트합니다.
            ftpService.deleteFiles(storeImgEntities);
            storeImgRepository.deleteAll(storeImgEntities);
            storeImgService.saveStoreImg(storeDTO,file);
        }
        return "redirect:/manager/store/list";
    }


    // 삭제
    @Operation(summary = "매장 삭제 처리",
            description = "매장 삭제 후 매장 목록 페이지로 이동")
    @PostMapping("/manager/store/delete/{storeNo}")
    public String deleteProc(@PathVariable("storeNo") Long storeNo,
                             RedirectAttributes redirectAttributes) {
        log.info("매장 삭제 Proc 처리...");
        storeService.delete(storeNo);
        redirectAttributes.addFlashAttribute("processMessage", "삭제하였습니다.");
        return "redirect:/manager/store/list";
    }


    // 전체조회
    @Operation(summary = "매장 목록",
            description = "매장 목록 정보를 보낸 후 매장 목록 페이지로 이동")
    @GetMapping("/manager/store/list")
    public String selectForm(Model model, StoreDTO storeDTO,
                             PageRequestDTO pageRequestDTO,
                             Principal principal) {
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        if(authService.isAdmin()){
            storeDTO.setAdminNo(null);
        }else {
            storeDTO.setAdminNo(adminNo);
        }
        PageResponseDTO<StoreDTO> storeDTOS = storeService.list(storeDTO,pageRequestDTO);
        model.addAttribute("list", storeDTOS);
        return "manager/store/list";
    }


    // 상세조회
    @Operation(summary = "매장 상세 정보",
            description = "매장의 정보를 보낸 후 매장 상세 정보 페이지로 이동")
    @GetMapping("/manager/store/read/{storeNo}")
    public String readForm(@PathVariable(name = "storeNo") Long storeNo,Model model) {
        log.info("호텔 상세조회 Form 이동...");
        List<StoreImgEntity> storeImgEntityList = storeImgService.findByStoreEntityStoreNo(storeNo);
        String imageUrl = "";
        if (!storeImgEntityList.isEmpty()) {
            StoreImgEntity firstMenuImg = storeImgEntityList.get(0);

            imageUrl = "http://woori-prn.iptime.org/project/data/" + firstMenuImg.getImgName(); // 웹 경로 수정

        }
        log.info(imageUrl);

        model.addAttribute("imageUrl", imageUrl);
        StoreDTO storeDTO = storeService.read(storeNo);
//        Long menuCount =
        model.addAttribute("data", storeDTO);
//        model.addAttribute("storeCount", menuCount);
        return "manager/store/read";
    }




}
