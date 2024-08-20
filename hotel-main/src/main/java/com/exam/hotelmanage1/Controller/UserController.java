package com.exam.hotelmanage1.Controller;

import com.exam.hotelmanage1.Config.CustomLoginSuccessHandlerForUser;
import com.exam.hotelmanage1.Config.CustomSecurityContextRepository;
import com.exam.hotelmanage1.Constant.PaymentStatus;
import com.exam.hotelmanage1.Constant.PaymentType;
import com.exam.hotelmanage1.Constant.RoleType;
import com.exam.hotelmanage1.DTO.NoticeDTO;
import com.exam.hotelmanage1.DTO.PageRequestDTO;
import com.exam.hotelmanage1.DTO.PageResponseDTO;
import com.exam.hotelmanage1.DTO.UserDTO;
import com.exam.hotelmanage1.Entity.UserEntity;
import com.exam.hotelmanage1.Service.UserService;
import com.exam.hotelmanage1.DTO.*;
import com.exam.hotelmanage1.Service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Tag(name = "고객", description = "고객이 이용하는 API")
@Log4j2
public class UserController {

    private final UserService userService;
    private final UserNoCheckService userNoCheckService;
    private final HotelImgService hotelImgService;
    private final HotelService hotelService;
    private final RoomService roomService;
    private final StoreService storeService;
    private final MenuService menuService;
    private final RevenueService revenueService;
    private final KakaoService kakaoService;
    private final UserLoginService userLoginService;
    private final CustomLoginSuccessHandlerForUser customLoginSuccessHandlerForUser;

    @ModelAttribute("paymentStatuses")
    public Map<String, String> paymentStatuses() {
        return Arrays.stream(PaymentStatus.values())
                .collect(Collectors.toMap(PaymentStatus::name, PaymentStatus::getValue));
    }

    @ModelAttribute("roleTypes")
    public RoleType[] roleTypes(){
        return RoleType.values();
    }

    @ModelAttribute("sido")
    public List<String> UserSidos(){
        List<String> sidos= hotelService.UserSidos();
        return sidos;
    }
    @ModelAttribute
    public void myPage(Principal principal, Model model) {
        // Principal 객체가 null인 경우, 즉 사용자가 로그아웃한 상태인 경우,
        // 이 메소드의 나머지 부분을 실행하지 않습니다.
        if (principal != null) {
            Long userNo = userNoCheckService.userNocheck(principal);
            model.addAttribute("userNo", userNo);
        }
    }

    @Operation(summary = "고객용 로그인",
            description = "로그인이 성공하면 고객용 메인 페이지로 이동, 아닐 경우 로그인 페이지로 이동")
    @GetMapping("/user/login")
    public String userLog(Model model,
                          @RequestParam(value="error", required = false) String error,
                          @RequestParam(value = "exception", required = false) String exception,
                          HttpServletRequest request,
                          HttpServletResponse response,
                          Authentication authentication
                          )throws Exception{
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
        model.addAttribute("kakaoApiKey", kakaoService.getRestApiKey());
        model.addAttribute("redirectUri", kakaoService.getRedirectUri());

        if (authentication != null && authentication.isAuthenticated()) {
            // 이미 인증된 사용자는 메인 페이지로 리다이렉트
            response.sendRedirect("/user/main");
        }

        return "user/login";
    }

    @Operation(summary = "고객용 메인 페이지",
            description = "고객용 메인 페이지로 이동")
    @GetMapping("/user/main")
    public String userMain(Model model,HotelDTO hotelDTO,PageRequestDTO pageRequestDTO){
        PageResponseDTO<HotelDTO> hotelDTOPageResponseDTO = userService.nowAvail(hotelDTO,pageRequestDTO);
        model.addAttribute("nowHotel",hotelDTOPageResponseDTO);
        return "user/main";
    }

    @Operation(summary = "고객용 회원가입",
            description = "고객용 회원가입 페이지로 이동")
    @GetMapping("/user/register")
    public String userRegi(@ModelAttribute("kakaoInfo") KakaoDTO kakaoDTO) {

        return "user/register";
    }

    @Operation(summary = "카카오 로그인 페이지로 이동",
            description = "")
    @GetMapping("/user/register/kakao")
    public String kakaoLoginCallback(@RequestParam("code") String code,
                                     RedirectAttributes redirectAttributes,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        try {
            // 카카오 서버에서 access token 요청
            String accessToken = kakaoService.getAccessToken(code);

            // 사용자 정보 조회
            KakaoDTO kakaoInfo = kakaoService.getKakaoUserInfo(accessToken);

            // 사용자 정보를 처리 (예: DB 저장)
            // 카카오에서 가져온 사용자 정보의 email을 조회 후 해당 email과 겹치는 email이 존재할경우
            UserEntity kakaoUser = kakaoService.ifNeedKakaoInfo(kakaoInfo);
            log.info(kakaoUser);
            if (kakaoUser != null) {
                // 자동 로그인 처리
                UserDetails userDetails = userLoginService.loadUserByUsername(kakaoUser.getEmail());
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authToken);
                SecurityContextHolder.setContext(securityContext);

                // SecurityContext를 세션에 저장
                SecurityContextRepository securityContextRepository = new CustomSecurityContextRepository();
                securityContextRepository.saveContext(securityContext, request, response);

                // CustomLoginSuccessHandlerForUser를 사용하여 로그인 성공 처리
                customLoginSuccessHandlerForUser.onAuthenticationSuccess(request, response, authToken);

                return null; // 로그인 성공 후 리다이렉트
            }

            // RedirectAttributes를 사용하여 데이터 전달
            redirectAttributes.addFlashAttribute("kakaoInfo", kakaoInfo);

            return "redirect:/user/register";
        } catch (Exception e) {
            return "user/main";
        }
    }


    @Operation(summary = "고객용 회원가입 처리",
            description = "회원가입 후 로그인 페이지로 이동")
    @PostMapping("/user/register")
    public String userRegiPost(@Valid UserDTO userDTO,KakaoDTO kakaoDTO){
        userDTO.setRoleType(RoleType.USER);
        if(kakaoDTO.getRegisterType().equals("KAKAO")){
            userDTO.setRegisterType(kakaoDTO.getRegisterType());
        }

        userService.register(userDTO);
        return "redirect:/user/login";
    }

    @Operation(summary = "고객 정보",
            description = "로그인 한 고객계정의 정보를 보내고 고객 정보 페이지로 이동")
    @GetMapping("user/info/{userNo}")
    public String userMyinfo(@PathVariable(name = "userNo") Long userNo,
                             Model model, Principal principal,
                             RedirectAttributes redirectAttributes) {
        Long userNo1 = userNoCheckService.userNocheck(principal);
        if (!Objects.equals(userNo, userNo1)) {
            // 다른 사용자의 정보를 조회하려 할 때
            return "redirect:/user/info/" + userNo1;
        }

        // 해당 사용자의 정보를 조회
        UserDTO userDTO = userService.read(userNo);
        model.addAttribute("userNo1", userNo1);
        model.addAttribute("userData", userDTO);
        return "user/info";
    }

    @Operation(summary = "고객 정보 수정폼",
            description = "고객 정보 수정 페이지로 이동")
    @GetMapping("/user/update")
    public String userUpdate(){
        return "user/register";
    }


    @Operation(summary = "고객 정보 수정처리",
            description = "고객 정보 수정 후 고객 정보 페이지로 이동")
    @PostMapping("/user/update/{userNo}")
    public String userUpdatePost(@PathVariable(name = "userNo") Long userNo,UserDTO userDTO){
        userDTO.setRoleType(RoleType.USER);
        userService.modify(userDTO);
        return "redirect:/user/info/{userNo}";
    }

    @Operation(summary = "고객 장바구니",
            description = "장바구니 목록을 보낸 후 장바구니 페이지로 이동")
    @GetMapping("/user/cart/{userNo}")
    public String userCart(@PathVariable(name = "userNo") Long userNo,
                           CartDTO cartDTO,
                           PageRequestDTO pageRequestDTO,
                           Model model){
        cartDTO.setUserNo(userNo);
        PageResponseDTO<CartDTO> cartDTOS = userService.cartList(cartDTO,pageRequestDTO);
        log.info(cartDTOS);
        model.addAttribute("list", cartDTOS);

        return "user/cart";
    }

    @Operation(summary = "고객 장바구니 처리",
            description = "결제 페이지로 이동")
    @PostMapping("/user/cart/{userNo}")
    public String userCartPost(@PathVariable(name = "userNo") Long userNo){
        return "redirect:/user/payment/" + userNo;
    }

    @Operation(summary = "장바구니 삭제 처리",
            description = "장바구니 삭제 후 장바구니 페이지로 이동")
    @PostMapping("/user/cart/delete")
    public String userCartDeletePost(@RequestParam(name = "cartNo") Long cartNo,
                                     Principal principal){
        userService.cartDelete(cartNo);
        Long userNo = userNoCheckService.userNocheck(principal);

        return "redirect:/user/cart/" + userNo;
    }


    //호텔 검색 페이지
    //+ 메인 화면에서 검색 후 이동 페이지
    @Operation(summary = "고객용 호텔 목록",
            description = "검색 조건에 맞는 호텔 목록을 보낸 후 호텔 목록 검색 페이지로 이동")
    @GetMapping("user/search")
    public String userSearch(Model model,
                             HotelDTO hotelDTO,
                             PageRequestDTO pageRequestDTO,
                             @RequestParam(required = false) String searchText) {
        log.info(hotelDTO);
        PageResponseDTO<HotelDTO> hotelDTOS = userService.HotelList(hotelDTO,pageRequestDTO,searchText);
        model.addAttribute("list", hotelDTOS);
        log.info(hotelDTOS);

        return "user/search";
    }

    @Operation(summary = "고객용 호텔 상세 정보",
            description = "호텔, 호텔의 객실, 호텔의 공지사항 정보를 보낸 후 호텔 상세 정보 페이지로 이동")
    @GetMapping("user/read/{hotelNo}")
    public String userRead(@PathVariable(name = "hotelNo") Long hotelNo,
                           @RequestParam(name = "startDate", required = false)
                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                           @RequestParam(name = "endDate", required = false)
                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                           Model model,PageRequestDTO pageRequestDTO,RoomDTO roomDTO){
        HotelDTO hotelDTO = hotelService.read(hotelNo);
        roomDTO.setStartDate(startDate);
        roomDTO.setEndDate(endDate);
        PageResponseDTO<RoomDTO> roomDTOS = roomService.PageList(roomDTO,pageRequestDTO);
        log.info(roomDTO);
        /*notice*/
        List<NoticeDTO> noticeDTOS = userService.noticeByhotel(hotelNo);

        log.info("read의이미지----"+hotelDTO.getImageUrl());

        model.addAttribute("rooms", roomDTOS);
        model.addAttribute("data", hotelDTO);
        model.addAttribute("notice",noticeDTOS);
        return "user/read";
    }


    //객실 상세 정보 페이지
    @Operation(summary = "고객용 객실 상세 정보",
            description = "객실 상세 정보를 보낸 후 고객용 객실 상세 정보 페이지로 이동")
    @GetMapping("/user/roomRead/{roomNo}")
    public String readForm(Model model,
                           @PathVariable(name = "roomNo") Long roomNo) {
        RoomDTO roomDTO = roomService.read(roomNo);


        log.info("read의이미지----"+roomDTO.getImageUrl());

        model.addAttribute("roomDTO", roomDTO);
        return "user/room-read";
    }

    @Operation(summary = "결제폼",
            description = "장바구니의 정보를 보낸 후 결제 페이지로 이동")
    @GetMapping("user/payment/{userNo}")
    public String userPaymentGet(@PathVariable(name = "userNo") Long userNo,Model model){
        List<CartDTO> cartDTOList = userService.findByUserEntityUserNo(userNo);
        model.addAttribute("cartEntityList",cartDTOList);
        return "user/payment";
    }
    @Operation(summary = "결제 처리",
            description = "결제 후 주문 내역 페이지로 이동")
    @PostMapping("user/payment/{userNo}")
    public String userPaymentPost(@PathVariable(name = "userNo") Long userNo,PaymentDTO paymentDTO,
                                  @RequestParam List<Long> menuNo,
                                  @RequestParam List<Integer> menuPrice,
                                  @RequestParam List<Integer> paymentCount,
                                  @RequestParam List<Integer> menuTotalPrice,
                                  @RequestParam List<Long> storeNo,
                                  @RequestParam List<Long> menuOptionNo,
                                  @RequestParam Integer grandTotalPrice,
                                  @RequestParam List<Integer> optionPrice
                                  ){
        log.info(paymentDTO);
        paymentDTO.setPaymentType(PaymentType.ITEM);

        Long paymentNo = userService.paymentSave(paymentDTO).getPaymentNo();
        List<PaymentItemDTO> paymentItemDTOList = new ArrayList<>();

        for (int i = 0; i < menuNo.size(); i++) {
            PaymentItemDTO paymentItemDTO = new PaymentItemDTO();
            paymentItemDTO.setPaymentNo(paymentNo);
            paymentItemDTO.setMenuNo(menuNo.get(i));
            paymentItemDTO.setUnitPrice(menuPrice.get(i));
            paymentItemDTO.setPaymentCount(paymentCount.get(i));
            paymentItemDTO.setTotalPrice(menuTotalPrice.get(i));
            if (menuNo.size() == menuOptionNo.size()) {
                paymentItemDTO.setMenuOptionNo(menuOptionNo.get(i));
            } else {
                // menuOptionNo가 null인 경우 처리
                paymentItemDTO.setMenuOptionNo(null); // 또는 기본값 설정
            }
            paymentItemDTO.setOptionPrice(optionPrice.get(i));

            paymentItemDTOList.add(paymentItemDTO);
        }
        List<PaymentItemDTO> paymentItemDTOList1 = userService.paymentItemSaveList(paymentItemDTOList);

        // PaymentItemDTOList1에서 paymentItemNo만 추출해 List<Long>으로 반환합니다.
        List<Long> paymentItemNoList = paymentItemDTOList1.stream()
                .map(PaymentItemDTO::getPaymentItemNo) // PaymentItemDTO에서 paymentItemNo를 추출
                .toList(); // 추출된 paymentItemNo를 List에 수집

        log.info(paymentItemNoList);


        List<RevenueDTO> revenueDTOList = new ArrayList<>();
        for (int i = 0; i < paymentItemDTOList.size(); i++) {
            RevenueDTO revenueDTO = new RevenueDTO();
            revenueDTO.setPaymentItemNo(paymentItemNoList.get(i));
            revenueDTO.setUnitPrice(menuPrice.get(i));
            revenueDTO.setTotalPrice(menuTotalPrice.get(i));
            revenueDTO.setPaymentCount(paymentCount.get(i));
            revenueDTO.setDepositPrice(menuTotalPrice.get(i));

            Integer fee = (int) (paymentItemDTOList.get(i).getTotalPrice() * 0.1); // PaymentItemDTO로부터 fee 가져오기
            Integer depositPrice = menuTotalPrice.get(i) - fee; // menuTotalPrice에서 fee를 빼서 depositPrice 계산
            revenueDTO.setDepositPrice(depositPrice);
            revenueDTO.setFee(fee);
            revenueDTOList.add(revenueDTO);

        }
        log.info(revenueDTOList);


        userService.paymentTotalPriceUpdate(paymentNo,grandTotalPrice);
        userService.paymentCartDelete(userNo);
        if(paymentDTO.getPaymentStatus() == PaymentStatus.COMPLETE){
            revenueService.insertItem(revenueDTOList);
        }


        return  "redirect:/user/orderlist/" + userNo;
    }

    @Operation(summary = "주문 내역",
            description = "결제 목록을 보낸 후 주문 내역 페이지로 이동")
    @GetMapping("user/orderlist/{userNo}")
    public String userOrder(@PathVariable(name = "userNo") Long userNo,
                            PaymentDTO paymentDTO,
                            PaymentItemDTO paymentItemDTO,
                            PageRequestDTO pageRequestDTO,
                            Model model) {

        PageResponseDTO<PaymentDTO> paymentListUser = userService.paymentItemListUser(paymentDTO,pageRequestDTO);

        model.addAttribute("paymentListUser",paymentListUser);
        model.addAttribute("status",PaymentStatus.values());


        return "user/orderlist";
    }

    @Operation(summary = "고객용 매장 메뉴 주문폼",
            description = "매장 정보를 보낸 후 매장의 메뉴 페이지로 이동")
    @GetMapping("user/menu/{storeNo}")
    public String userMenu(@PathVariable(name = "storeNo") Long storeNo,
                           Model model,
                           PageRequestDTO pageRequestDTO,
                           CategoryDTO categoryDTO){
        PageResponseDTO<CategoryDTO> responseDTO = menuService.UserList(categoryDTO,pageRequestDTO);
        StoreDTO storeDTO = storeService.getStoreAndHotelName(storeNo);
        model.addAttribute("list",responseDTO);
        model.addAttribute("storeDTO",storeDTO);
        return "user/menu";
    }

    @Operation(summary = "고객용 매장 메뉴 주문처리",
            description = "주문한 메뉴 정보를 장바구니에 담음")
    @PostMapping("/user/menu/{storeNo}")
    public String userMenuPost(CartDTO cartDTO){
        log.info(cartDTO);
        userService.cartSave(cartDTO);

        return "redirect:/user/menu/" + cartDTO.getStoreNo();
    }


    /*모든 매장 목록*/
    @Operation(summary = "고객용 매장 목록",
            description = "매장 목록을 보낸 후 고객용 매장 목록 페이지로 이동")
    @GetMapping("user/store")
    public String userStore(Model model, StoreDTO storeDTO, PageRequestDTO pageRequestDTO){
        PageResponseDTO<StoreDTO> storeDTOS = storeService.list(storeDTO,pageRequestDTO);
        model.addAttribute("list", storeDTOS);
        log.info(storeDTOS);
        return "user/store";
    }

    /*예약된 호텔의 매장 목록 (룸서비스 이용가능 )*/
    @Operation(summary = "룸서비스",
            description = "매장중 이용 가능한 목록을 보낸 후 룸서비스 페이지로 이동")
    @GetMapping("user/roomservice")
    public String userRoomservice(Model model,
                                  StoreDTO storeDTO,
                                  PageRequestDTO pageRequestDTO,
                                  Principal principal){
        //로그인 된 정보
        Long userNo = userNoCheckService.userNocheck(principal);
        storeDTO.setUserNo(userNo);
        //예약 정보로 필터링 된 매장목록
        PageResponseDTO<StoreDTO> storeDTOS = storeService.list(storeDTO,pageRequestDTO);
        model.addAttribute("list", storeDTOS);
        log.info(storeDTOS);
        return "user/roomService";
    }


    /*공지사항조회*/
    @Operation(summary = "고객용 공지사항",
            description = "공지사항 목록, 호텔 목록을 보낸 후 공지사항 페이지로 이동")
    @GetMapping("user/notice")
    public String noticeListGet(NoticeDTO noticeDTO,
                                FaqDTO faqDTO,
                                PageRequestDTO pageRequestDTO,
                                Model model) {
        PageResponseDTO<NoticeDTO> noticeDTOS = userService.noticeList(noticeDTO,pageRequestDTO);
        model.addAttribute("noticeList",noticeDTOS);

        List<HotelDTO> hotelDTOS = userService.noticeHotelList();
        model.addAttribute("hotelDTOS",hotelDTOS);

        return "user/notice";
    }

    @Operation(summary = "고객용 FAQ",
            description = "FAQ 목록을 보낸후 FAQ 페이지로 이동")
    @GetMapping("user/faq")
    public String faqListGet(NoticeDTO noticeDTO,
                             FaqDTO faqDTO,
                             PageRequestDTO pageRequestDTO,
                             Model model) {
        PageResponseDTO<FaqDTO> FaqDTOS = userService.faqList(faqDTO,pageRequestDTO);
        model.addAttribute("faqList",FaqDTOS);
        List<String> faqCategory = userService.faqCategory();
        model.addAttribute("faqCategory",faqCategory);
        return  "user/faq";

    }


    @GetMapping("user/test-slide")
    public String testt(){
        return "user/test-slide";
    }


}