package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.Constant.ReservationStatus;
import com.exam.hotelmanage1.Constant.RoomType;
import com.exam.hotelmanage1.DTO.*;
import com.exam.hotelmanage1.Entity.*;
import com.exam.hotelmanage1.Repository.HotelRepository;
import com.exam.hotelmanage1.Repository.RoomImgRepository;
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
@Tag(name = "객실", description = "객실 관리 API")
@Log4j2
public class RoomController {
    private final RoomService roomService;
    private final HotelRepository hotelRepository;
    private final AuthService authService;
    private final AdminNoCheckService adminNoCheckService;
    private final CodeGroupService codeGroupService;
    private final RoomCodeService roomCodeService;
    private final CodeService codeService;
    private final RoomImgService roomImgService;
    private final ModelMapper modelMapper;
    private final FTPService ftpService;
    private final RoomImgRepository roomImgRepository;
    private final HotelService hotelService;
    private final MenuService menuService;

    // 모델에 속성을 추가하고 해당 속성을 자동으로 뷰에 전달
    @ModelAttribute("roomTypes")
    public RoomType[] roomTypes() {
        return RoomType.values();
    }


    @ModelAttribute("reservationStatus")
    public ReservationStatus[] reservationStatus() {
        return ReservationStatus.values();
    }

    @ModelAttribute("sido")
    public List<String> HotelSidos(Principal principal){
        List<String> sidos= hotelService.HotelSidos(principal);
        log.info(sidos);
        return sidos;
    }

    //등록
    @Operation(summary = "객실 등록 폼",
            description = "객실 등록 페이지로 이동")
    @GetMapping("/manager/hotel/room/register")
    public String registerForm(Model model,
                               CodeGroupDTO codeGroupDTO,
                               Principal principal) {
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        List<CodeGroupDTO> codeGroupDTOS = codeGroupService.getCodeGroupsByAdminNo(adminNo);
        //호텔 지역을 보내서 어느 호텔인지 찾을 수 있게 함
        model.addAttribute("codeGroups",codeGroupDTOS);

        return "manager/hotel/room/register";
    }

    @Operation(summary = "객실 등록 처리",
            description = "객실 등록 후 객실 목록 페이지로 이동")
    @PostMapping("/manager/hotel/room/register")
    public String registerProc(@ModelAttribute RoomDTO roomDTO,
                               @RequestParam("hotelNo") Long hotelNo,
                               @RequestParam List<Long> codeGroupNo,
                               @RequestParam List<Long> codeNo,
                               @RequestParam("file") MultipartFile file
                               ) throws Exception {

        // 방 등록
        Collections.sort(codeGroupNo);
        Collections.sort(codeNo);

        List<String> codeGroupName = codeGroupService.findNameByCodeNoIn(codeGroupNo);

        List<Integer> codeGroupOrder = codeGroupService.findOrderIdxByCodeNoIn(codeGroupNo);

        List<String> codeName = codeService.findNameByCodeNoIn(codeNo);

        List<String> fullCode = codeService.findFullCodeByCodeNoIn(codeNo);

        RoomEntity roomEntity = roomService.register(roomDTO);


        // RoomCodeDTO 리스트 생성
        List<RoomCodeDTO> roomCodes = new ArrayList<>();
        for (int i = 0; i < codeGroupNo.size(); i++) {
            RoomCodeDTO roomCodeDTO = RoomCodeDTO.builder()
                    .codeGroupNo(codeGroupNo.get(i))
                    .codeNo(codeNo.get(i))
                    .orderIdx(codeGroupOrder.get(i))
                    .codeGroupName(codeGroupName.get(i))
                    .codeName(codeName.get(i))
                    .fullCode(fullCode.get(i))
                    .build();
            roomCodes.add(roomCodeDTO);
        }
        log.info(roomCodes);

        // RoomCodeService를 통해 RoomCode 저장
        roomCodeService.saveRoomCodes(roomCodes,roomEntity);
        RoomDTO roomDTO1 = modelMapper.map(roomEntity, RoomDTO.class);
        roomImgService.saveRoomImg(roomDTO1,file);
        return "redirect:/manager/hotel/room/list";
    }


    //수정
    @Operation(summary = "객실 정보 수정폼",
            description = "객실 정보, 코드그룹 정보를 보낸 후 객실 정보 수정 페이지로 이동")
    @GetMapping("/manager/hotel/room/update/{roomNo}")
    public String updateForm(Model model,
                             @PathVariable("roomNo") Long roomNo,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        List<CodeGroupDTO> codeGroupDTOS = codeGroupService.getCodeGroupsByAdminNo(adminNo);
        //호텔 지역을 보내서 어느 호텔인지 찾을 수 있게 함
        model.addAttribute("codeGroups",codeGroupDTOS);
        //아이디 값으로 읽어온다
        RoomDTO roomDTO = roomService.read(roomNo);
        //읽어온 값을 보낸다
        model.addAttribute("roomDTO", roomDTO);
        //
        return "manager/hotel/room/update";
    }

    @Operation(summary = "객실정보 수정 처리",
            description = "객실 정보 수정 후 객실 목록 페이지로 이동")
    @PostMapping("/manager/hotel/room/update")
    public String updateProc(Model model, RoomDTO roomDTO,
                             @RequestParam List<Long> codeGroupNo,
                             @RequestParam List<Long> codeNo,
                             @RequestParam(value = "file", required = false) MultipartFile file
                             ) throws Exception {
        Collections.sort(codeGroupNo);
        Collections.sort(codeNo);
        log.info(roomDTO);
        List<String> codeGroupName = codeGroupService.findNameByCodeNoIn(codeGroupNo);
        List<Integer> codeGroupOrder = codeGroupService.findOrderIdxByCodeNoIn(codeGroupNo);
        List<String> codeName = codeService.findNameByCodeNoIn(codeNo);
        List<String> fullCode = codeService.findFullCodeByCodeNoIn(codeNo);
        RoomEntity roomEntity = roomService.update(roomDTO);

        // RoomCodeDTO 리스트 생성
        List<RoomCodeDTO> roomCodes = new ArrayList<>();
        for (int i = 0; i < codeGroupNo.size(); i++) {
            RoomCodeDTO roomCodeDTO = RoomCodeDTO.builder()
                    .roomNo(roomDTO.getRoomNo())
                    .codeGroupNo(codeGroupNo.get(i))
                    .codeNo(codeNo.get(i))
                    .orderIdx(codeGroupOrder.get(i))
                    .codeGroupName(codeGroupName.get(i))
                    .codeName(codeName.get(i))
                    .fullCode(fullCode.get(i))
                    .build();
            roomCodes.add(roomCodeDTO);
        }
        // RoomCodeService를 통해 RoomCode 저장
        roomCodeService.updateRoomCodes(roomCodes,roomDTO.getRoomNo(),roomEntity);
        List<RoomImgEntity> roomImgEntities = roomImgService.findByRoomEntityRoomNo(roomDTO.getRoomNo());
        if (file != null && !file.isEmpty()) {
            // 이미지 파일이 제공되었다면, 이미지 파일을 저장하고 관련 정보를 업데이트합니다.
            ftpService.deleteFiles(roomImgEntities);
            roomImgRepository.deleteAll(roomImgEntities);
            roomImgService.saveRoomImg(roomDTO, file);
        }

        return "redirect:/manager/hotel/room/list";
    }

    //삭제
    @Operation(summary = "객실 삭제 처리",
            description = "객실 삭제 후 객실 목록 페이지로 이동")
    @PostMapping("/manager/hotel/room/delete")
    public String deleteForm(Model model,
                             @RequestParam("roomNo1") Long roomNo) {
        roomService.delete(roomNo);
        return "redirect:/manager/hotel/room/list";
    }

    //읽기
    @Operation(summary = "객실 상세 정보",
            description = "객실 상세 정보를 보낸 후 객실 상세 정보 페이지로 이동")
    @GetMapping("/manager/hotel/room/read/{roomNo}")
    public String readForm(Model model,
                           @PathVariable(name = "roomNo") Long roomNo) {
        List<RoomImgEntity> roomImgEntities = roomImgService.findByRoomEntityRoomNo(roomNo);
        String imageUrl = "";
        if (!roomImgEntities.isEmpty()) {
            RoomImgEntity firstMenuImg = roomImgEntities.get(0);

            imageUrl = "http://woori-prn.iptime.org/project/data/" + firstMenuImg.getImgName(); // 웹 경로 수정

        }
        log.info(imageUrl);

        model.addAttribute("imageUrl", imageUrl);
        RoomDTO roomDTO = roomService.read(roomNo);
        model.addAttribute("roomDTO", roomDTO);
        return "manager/hotel/room/read";
    }


    //목록
    @Operation(summary = "객실 목록",
            description = "호텔의 객실 목록을 보낸 후 객실 목록 페이지로 이동")
    @GetMapping("/manager/hotel/room/list")
    public String listForm(Model model,
                           RoomDTO roomDTO,
                           PageRequestDTO pageRequestDTO,
                           Principal principal) {
        String userId = principal.getName();
        Long adminNo = hotelRepository.findCompanyNoByUsername(userId);
        if(authService.isAdmin()){
            roomDTO.setAdminNo(null);
        }else {
            roomDTO.setAdminNo(adminNo);
        }
        PageResponseDTO<RoomDTO> roomDTOPageResponseDTO = roomService.PageList(roomDTO, pageRequestDTO);
        model.addAttribute("list", roomDTOPageResponseDTO);
        return "manager/hotel/room/list";
    }

    @Operation(summary = "객실 상태",
            description = "객실의 현재 상태를 확인")
    @GetMapping("/manager/hotel/room/status")
    public String status(Model model,
                           RoomDTO roomDTO,
                           PageRequestDTO pageRequestDTO,
                           Principal principal) {
        Long adminNo = adminNoCheckService.adminNocheck(principal);
        List<HotelDTO> hotelDTOList = menuService.findHotelsByAdminNo(adminNo);
        model.addAttribute("hotelDTOList", hotelDTOList);
        return "manager/hotel/room/status";
    }
}
