package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.Constant.PaymentStatus;
import com.exam.hotelmanage1.Constant.PaymentType;
import com.exam.hotelmanage1.DTO.*;
import com.exam.hotelmanage1.Entity.*;
import com.exam.hotelmanage1.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class UserService {

    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final NoticeRepository noticeRepository;
    private final FaqRepository faqRepository;
    private final HotelImgService hotelImgService;
    private final CartRepository cartRepository;
    private final MenuImgService menuImgService;
    private final PaymentRepository paymentRepository;
    private final PaymentItemRepository paymentItemRepository;
    private final MenuOptionRepository menuOptionRepository;
    private final StoreImgService storeImgService;


    public PageResponseDTO<HotelDTO> HotelList(HotelDTO hotelDTO, PageRequestDTO pageRequestDTO, String searchText) {
        Pageable pageable = pageRequestDTO.getPageable("hotelNo");

        // Specification 생성
        Specification<HotelEntity> spec = Specification.where(null);

        if (hotelDTO.getName() != null) {
            spec = spec.and(HotelSpecification.equalsName(hotelDTO.getName()));
        }
        if (hotelDTO.getPostNumber() != null) {
            spec = spec.and(HotelSpecification.equalsPostNumber(hotelDTO.getPostNumber()));
        }
        if (hotelDTO.getTel() != null) {
            spec = spec.and(HotelSpecification.equalsTel(hotelDTO.getTel()));
        }
        if (hotelDTO.getHotelType() != null) {
            spec = spec.and(HotelSpecification.equalsType(hotelDTO.getHotelType()));
        }
        //날짜를 입력하고 예약 가능한 객실이 있는 호텔목록을 검색
        if (hotelDTO.getStartDate() != null && hotelDTO.getEndDate() != null) {
            spec = spec.and(HotelSpecification.hasAvailableRooms(hotelDTO.getStartDate(), hotelDTO.getEndDate()));
        }
        //검색어로 시/도, 시/군/구 검색
        if (searchText != null && !searchText.isEmpty()) {
            spec = spec.and(HotelSpecification.containsTextInSidoOrSigungu(searchText));
        }

        // Specification과 Pageable을 사용하여 페이징 처리된 결과 가져오기
        Page<HotelEntity> hotelEntityPage = hotelRepository.findAll(spec, pageable);

        // AdminEntity의 Stream을 AdminDTO로 변환 후 List로 수집
        List<HotelDTO> dtoList = hotelEntityPage.getContent().stream()
                .map(entity -> {
                    int lowestPrice = entity.getRoomEntity().stream()
                            .mapToInt(RoomEntity::getPrice)
                            .min()
                            .orElse(0);
                    List<HotelImgEntity> hotelImgEntityList = hotelImgService.findByHotelEntityHotelNo(entity.getHotelNo());
                    String imageUrl = "";
                    if (!hotelImgEntityList.isEmpty()) {
                        HotelImgEntity firstMenuImg = hotelImgEntityList.get(0);

                        imageUrl = "http://woori-prn.iptime.org/project/data/" + firstMenuImg.getImgName(); // 웹 경로 수정

                    }

                    return HotelDTO.builder()
                            .hotelNo(entity.getHotelNo())
                            .address(entity.getAddress())
                            .address2(entity.getAddress2())
                            .regDate(entity.getRegDate())
                            .modDate(entity.getModDate())
                            .postNumber(entity.getPostNumber())
                            .tel(entity.getTel())
                            .hotelType(entity.getHotelType())
                            .name(entity.getName())
                            .sido(entity.getSido())
                            .sigungu(entity.getSigungu())
                            .sido(entity.getSido())
                            .adminNo(entity.getAdminEntity().getAdminNo())
                            .rowPrice(lowestPrice) // 최소값이 존재하면 해당 값을, 그렇지 않으면 null을 rowPrice로 설정
                            .imageUrl(imageUrl)
                            .build();
                })
                .collect(Collectors.toList());

        // PageResponseDTO 생성
        return PageResponseDTO.<HotelDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) hotelEntityPage.getTotalElements())
                .build();
    }

    public void register(UserDTO userDTO){
        //존재하는 회원인지 확인
        Optional<UserEntity> userEntity = userRepository
                .findByEmail(userDTO.getEmail());
        if (userEntity.isPresent()){
            throw new IllegalStateException("이미 가입된 아이디 입니다");
        }

        //존재하지 않으면
        //1.암호화.복호화
        //2.DTO->Entity 변환
        UserEntity temp = modelMapper.map(userDTO, UserEntity.class);
        //3.DTO에 없는 부분을 Entity에 추가(복호화된암호,권한...)
        temp.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        temp.setRoleType(userDTO.getRoleType());
        //4.값저장
        userRepository.save(temp);

    }
    public void modify(UserDTO userDTO) {

        log.info(userDTO);

        Optional<UserEntity> result = userRepository.findById(userDTO.getUserNo());
        UserEntity userEntity = result.orElseThrow(); //기존에 존재하던 값

        //수정될값
        userEntity.setNickName(userDTO.getNickName());
        userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userEntity.setPhone(userDTO.getPhone());
        userRepository.save(userEntity);
    }

    public UserDTO read(Long userNo) {

        Optional<UserEntity> userEntity = userRepository.findById(userNo);

        UserEntity userEntity1 = userEntity.orElseThrow();

        return modelMapper.map(userEntity1, UserDTO.class);
    }

    /*==notice list 조회==*/
    public PageResponseDTO<NoticeDTO> noticeList(NoticeDTO noticeDTO, PageRequestDTO pageRequestDTO){
        Pageable pageable = pageRequestDTO.getPageable("noticeNo");
        // Specification 생성
        Specification<NoticeEntity> spec = Specification.where(null);
        if (noticeDTO.getTitle() != null){
            spec = spec.and(NoticeSpecification.equalsTitle(noticeDTO.getTitle()));
        }
        if (noticeDTO.getContent() != null){
            spec = spec.and(NoticeSpecification.equalsContent(noticeDTO.getContent()));
        }
        if (noticeDTO.getHotelNo() != null) {
            spec = spec.and(NoticeSpecification.equalsHotel(noticeDTO.getHotelNo()));
        }

        // Specification과 Pageable을 사용하여 페이징 처리된 결과 가져오기
        Page<NoticeEntity> noticeEntityPage = noticeRepository.findAll(spec,pageable);
        // NoticeEntity의 Stream을 NoticeDTO로 변환 후 List로 수집
        List<NoticeDTO> dtoList = noticeEntityPage.getContent().stream()
                .map(entity -> {
                    Long hotelNo = Optional.ofNullable(entity.getHotelEntity())
                            .map(HotelEntity::getHotelNo)
                            .orElse(null);
                    String hotelName = Optional.ofNullable(entity.getHotelEntity())
                            .map(HotelEntity::getName)
                            .orElse(null);
                    return NoticeDTO.builder()
                            .noticeNo(entity.getNoticeNo())
                            .hotelNo(hotelNo)
                            .regDate(entity.getRegDate())
                            .modDate(entity.getModDate())
                            .title(entity.getTitle())
                            .content(entity.getContent())
                            .hotelName(hotelName)
                            .build();
                })
                .collect(Collectors.toList());
        return PageResponseDTO.<NoticeDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)noticeEntityPage.getTotalElements())
                .build();

    }
    /*notice내 호텔목록*/
    public List<HotelDTO> noticeHotelList (){
        List<HotelEntity> hotelEntities = noticeRepository.findDistinctHotelNos();
        List<HotelDTO> hotelDTOS = Arrays.asList(modelMapper.map(hotelEntities, HotelDTO[].class));
        return hotelDTOS;
    }
    /*호텔no으로 notice조회*/
    public List<NoticeDTO> noticeByhotel(Long hotelNo){
        List<NoticeEntity> list = noticeRepository.findByHotelEntity_HotelNo(hotelNo);
        return Arrays.asList(modelMapper.map(list, NoticeDTO[].class));
    }
    /*FAQ list 조회*/
    //list조회
    public PageResponseDTO<FaqDTO> faqList(FaqDTO faqDTO, PageRequestDTO pageRequestDTO){
        Pageable pageable = pageRequestDTO.getPageable("faqNo");

        Specification<FaqEntity> spec = Specification.where(null);

        if (faqDTO.getTitle() != null){
            spec = spec.and(FaqSpecification.equalsTitle(faqDTO.getTitle()));
        }
        if (faqDTO.getContent() != null){/*title or content*/
            spec = spec.or(FaqSpecification.equalsContent(faqDTO.getContent()));
        }
        if (faqDTO.getCategory() != null){
            spec = spec.and(FaqSpecification.equalsCategory(faqDTO.getCategory()));
        }

        Page<FaqEntity> faqEntityPage = faqRepository.findAll(spec,pageable);
        List<FaqDTO> dtoList = faqEntityPage.getContent().stream()
                .map(entity -> FaqDTO.builder()
                        .faqNo(entity.getFaqNo())
                        .category(entity.getCategory())
                        .title(entity.getTitle())
                        .content(entity.getContent())
                        .regDate(entity.getRegDate())
                        .modDate(entity.getModDate())
                        .build())
                .collect(Collectors.toList());

        return PageResponseDTO.<FaqDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)faqEntityPage.getTotalElements())
                .build();

    }

    /*faq카테고리값추출*/
    public List<String> faqCategory (){
        List<String> faqCategoryList = faqRepository.findDistinctCategories();
        return faqCategoryList;
    }
    //=====END notie,faq=====//




    public void cartSave(CartDTO cartDTO){
        CartEntity temp = modelMapper.map(cartDTO, CartEntity.class);
        cartRepository.save(temp);

    }


    public PageResponseDTO<CartDTO> cartList(CartDTO cartDTO, PageRequestDTO pageRequestDTO){
        Pageable pageable = pageRequestDTO.getPageable("cartNo");
        // Specification 생성
        Specification<CartEntity> spec = Specification.where(null);
        if (cartDTO.getUserNo() != null){
            spec = spec.and(UserSpecification.equalsUserNo(cartDTO.getUserNo()));
        }

        // Specification과 Pageable을 사용하여 페이징 처리된 결과 가져오기
        Page<CartEntity> cartEntityPage = cartRepository.findAll(spec,pageable);
        // NoticeEntity의 Stream을 NoticeDTO로 변환 후 List로 수집
        List<CartDTO> dtoList = cartEntityPage.getContent().stream()
                .map(entity -> {
                    List<MenuImgEntity> menuImgEntities = menuImgService.findByMenuEntityMenuNo(entity.getMenuEntity().getMenuNo());
                    String imageUrl = "";
                    if (!menuImgEntities.isEmpty()) {
                        MenuImgEntity firstMenuImg = menuImgEntities.get(0);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        String dateString = "";
                        if (firstMenuImg.getRegDate() != null) {
                            dateString = firstMenuImg.getRegDate().format(formatter); // 'LocalDateTime'을 사용하여 포맷
                        }

                        if (!dateString.isEmpty()) {
                            imageUrl = "http://woori-prn.iptime.org/project/data/" + firstMenuImg.getImgName(); // 웹 경로 수정
                        }
                    }

                    Long menuOptionNo = null;
                    String menuOptionName = "";
                    Integer optionPrice = 0;

                    if (entity.getMenuOptionEntity() != null) {
                        menuOptionNo = entity.getMenuOptionEntity().getMenuOptionNo();
                        menuOptionName = entity.getMenuOptionEntity().getName();
                        optionPrice = entity.getMenuOptionEntity().getPrice();
                    }

                    return CartDTO.builder()
                            .cartNo(entity.getCartNo())
                            .hotelNo(entity.getMenuEntity().getCategory1().getStoreEntity().getHotelEntity().getHotelNo())
                            .menuNo(entity.getMenuEntity().getMenuNo())
                            .menuName(entity.getMenuEntity().getName())
                            .menuOptionNo(menuOptionNo)
                            .menuOptionName(menuOptionName)
                            .price(entity.getMenuEntity().getPrice())
                            .optionPrice(optionPrice)
                            .userNo(entity.getUserEntity().getUserNo())
                            .storeNo(entity.getMenuEntity().getCategory1().getStoreEntity().getStoreNo())
                            .storeName(entity.getMenuEntity().getCategory1().getStoreEntity().getName())
                            .regDate(entity.getRegDate())
                            .modDate(entity.getModDate())
                            .cartCount(entity.getCartCount())
                            .imageUrl(imageUrl)
                            .build();
                })
                .collect(Collectors.toList());
        return PageResponseDTO.<CartDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)cartEntityPage.getTotalElements())
                .build();
    }
    public PaymentEntity paymentSave(PaymentDTO paymentDTO){
        PaymentEntity temp = modelMapper.map(paymentDTO, PaymentEntity.class);
        paymentRepository.save(temp);

        return temp;

    }
    public List<PaymentItemDTO> paymentItemSaveList(List<PaymentItemDTO> paymentItemDTOList){
        // DTO 리스트를 Entity 리스트로 변환
        List<PaymentItemEntity> paymentItemEntityList = paymentItemDTOList.stream()
                .map(dto -> modelMapper.map(dto, PaymentItemEntity.class))
                .collect(Collectors.toList());

        // 변환된 Entity 리스트를 저장하고, 저장된 Entity 리스트를 반환
        paymentItemRepository.saveAll(paymentItemEntityList);

        List<PaymentItemDTO> paymentItemDTOList1 = paymentItemEntityList.stream()
                .map(dto -> modelMapper.map(dto, PaymentItemDTO.class))
                .toList();

        return paymentItemDTOList1;
    }
    public PaymentItemEntity paymentItemSave(PaymentItemDTO paymentItemDTO){
        // DTO 리스트를 Entity 리스트로 변환
        PaymentItemEntity paymentItemEntity = modelMapper.map(paymentItemDTO,PaymentItemEntity.class);

        // 변환된 Entity 리스트를 저장하고, 저장된 Entity 리스트를 반환
        return paymentItemRepository.save(paymentItemEntity);
    }
    public void paymentTotalPriceUpdate(Long paymentNo, int grandTotalPrice) {
        // userNo를 사용하여 해당 PaymentEntity를 찾음
        Optional<PaymentEntity> existingEntityOptional = paymentRepository.findByPaymentNo(paymentNo);

        if (existingEntityOptional.isPresent()) {
            // 기존 엔티티를 찾았으면, grandTotalPrice를 업데이트함
            PaymentEntity existingEntity = existingEntityOptional.get();
            existingEntity.setGrandTotalPrice(grandTotalPrice);
            paymentRepository.save(existingEntity); // 엔티티를 업데이트하여 저장함
        } else {
            // 해당 userNo로 PaymentEntity를 찾지 못했을 경우의 로직 처리
            // 예: 오류 로깅, 예외 발생 등
        }
    }

    public void cartDelete(Long cartNo) {
        Optional<CartEntity> cartEntity = cartRepository.findById(cartNo);

        if (cartEntity.isPresent()) {   //코드가 있으면 삭제
            cartRepository.deleteById(cartNo);
        }
    }

    public void paymentCartDelete(Long userNo) {
    cartRepository.deleteByUserEntity_UserNo(userNo);
    }


    public List<CartDTO> findByUserEntityUserNo(Long userNo) {
        List<CartEntity> cartEntityList = cartRepository.findByUserEntityUserNo(userNo);

        // cartEntityList를 CartDTO 리스트로 매핑
        List<CartDTO> cartDTOList = cartEntityList.stream()
                .map(entity -> CartDTO.builder()
                        .cartNo(entity.getCartNo())
                        .menuNo(entity.getMenuEntity().getMenuNo())
                        .menuName(entity.getMenuEntity().getName())
                        .menuOptionNo(entity.getMenuOptionEntity() != null ? entity.getMenuOptionEntity().getMenuOptionNo() : null)
                        .menuOptionName(entity.getMenuOptionEntity() != null ? entity.getMenuOptionEntity().getName() : "")
                        .optionPrice(entity.getMenuOptionEntity() != null ? entity.getMenuOptionEntity().getPrice() : 0) // 가격은 0으로 처리할 수 있음
                        .storeNo(entity.getMenuEntity().getCategory1().getStoreEntity().getStoreNo())
                        .price(entity.getMenuEntity().getPrice())
                        .cartCount(entity.getCartCount())
                        .userNo(entity.getUserEntity().getUserNo())
                        .regDate(entity.getRegDate())
                        .modDate(entity.getModDate())
                        .build())
                .collect(Collectors.toList());
        return cartDTOList;
    }


    public PageResponseDTO<PaymentDTO> paymentItemListUser(PaymentDTO paymentDTO, PageRequestDTO pageRequestDTO){
        Pageable pageable = pageRequestDTO.getPageable("paymentNo");
        // Specification 생성
        Specification<PaymentEntity> spec = Specification.where(UserSpecification.paymentTypeIsItem(PaymentType.ITEM));
        if (paymentDTO.getUserNo() != null){
            spec = spec.and(UserSpecification.equalsPaymentUserNo(paymentDTO.getUserNo()));
        }

        // Specification과 Pageable을 사용하여 페이징 처리된 결과 가져오기
        Page<PaymentEntity> paymentEntityPage = paymentRepository.findAll(spec,pageable);
        // NoticeEntity의 Stream을 NoticeDTO로 변환 후 List로 수집
        List<PaymentDTO> dtoList = paymentEntityPage.getContent().stream()
                .map(entity -> {
                    PaymentDTO paymentDTO1 = PaymentDTO.builder()
                            .adminNo(entity.getReserveEntity().getRoomEntity().getHotelEntity().getAdminEntity().getAdminNo())
                            .hotelNo(entity.getReserveEntity().getRoomEntity().getHotelEntity().getHotelNo())
                            .hotelName(entity.getReserveEntity().getRoomEntity().getHotelEntity().getName())
                            .userEmail(entity.getUserEntity().getEmail())
                            .userName(entity.getUserEntity().getName())
                            .userNickName(entity.getUserEntity().getNickName())
                            .roomNo(entity.getReserveEntity().getRoomEntity().getRoomNo())
                            .roomName(entity.getReserveEntity().getRoomEntity().getName())
                            .paymentNo(entity.getPaymentNo())
                            .grandTotalPrice(entity.getGrandTotalPrice())
                            .userNo(entity.getUserEntity().getUserNo())
                            .regDate(entity.getRegDate())
                            .modDate(entity.getModDate())
                            .paymentStatus(entity.getPaymentStatus())
                            .build();

                    List<PaymentItemDTO> paymentItemDTOs = paymentItemRepository.findByPaymentEntity_PaymentNo(entity.getPaymentNo())
                            .stream()
                            .map(paymentItemEntity -> {
                                // Optional을 사용하여 null 처리
                                String menuOptionName = Optional.ofNullable(paymentItemEntity.getMenuOptionEntity())
                                        .map(MenuOptionEntity::getName)
                                        .orElse("옵션이 없습니다");
                                // 상점 이미지를 찾기
                                List<StoreImgEntity> storeImgEntities = storeImgService.findByStoreEntityStoreNo(paymentItemEntity.getMenuEntity().getCategory1().getStoreEntity().getStoreNo());
                                String imageUrl = "";
                                if (!storeImgEntities.isEmpty()) {
                                    StoreImgEntity firstMenuImg = storeImgEntities.get(0);

                                    imageUrl = "http://woori-prn.iptime.org/project/data/" + firstMenuImg.getImgName(); // 웹 경로 수정

                                }
                                return PaymentItemDTO.builder()
                                        .paymentNo(paymentItemEntity.getPaymentEntity().getPaymentNo())
                                        .paymentCount(paymentItemEntity.getPaymentCount())
                                        .menuNo(paymentItemEntity.getMenuEntity().getMenuNo())
                                        .unitPrice(paymentItemEntity.getUnitPrice())
                                        .optionPrice(paymentItemEntity.getOptionPrice())
                                        .totalPrice(paymentItemEntity.getTotalPrice())
                                        .menuName(paymentItemEntity.getMenuEntity().getName())
                                        .menuOptionName(menuOptionName)  // null일 경우 빈 문자열 할당
                                        .storeNo(paymentItemEntity.getMenuEntity().getCategory1().getStoreEntity().getStoreNo())
                                        .storeName(paymentItemEntity.getMenuEntity().getCategory1().getStoreEntity().getName())
                                        .imageUrl(imageUrl)
                                        .regDate(paymentItemEntity.getRegDate())
                                        .modDate(paymentItemEntity.getModDate())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    paymentDTO1.setPaymentItemDTO(paymentItemDTOs);
                    log.info(paymentDTO1);
                    return paymentDTO1;
                })
                .collect(Collectors.toList());
        return PageResponseDTO.<PaymentDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)paymentEntityPage.getTotalElements())
                .build();
    }
    public void updateCartQuantity(Long cartNo, int cartCount) throws Exception {
        Optional<CartEntity> cartEntityOptional = cartRepository.findById(cartNo);
        if (cartEntityOptional.isPresent()) {
            CartEntity cartEntity = cartEntityOptional.get();
            if (cartCount > 0) {
                cartEntity.setCartCount(cartCount);
                cartRepository.save(cartEntity);
            } else {
                // 여기서 수량이 0 이하로 요청될 경우 삭제하거나 예외를 처리하는 로직을 추가할 수 있습니다.
                throw new IllegalArgumentException("갯수가 유효하지 않습니다.");
            }
        } else {
            throw new Exception("장바구니 항목을 찾을 수 없습니다.");
        }
    }

    public List<MenuOptionDTO> findAllByMenuNo(Long menuNo){
        List<MenuOptionEntity> menuOptionEntityList = menuOptionRepository.findAllByMenuNo(menuNo);

// cartEntityList를 CartDTO 리스트로 매핑
        List<MenuOptionDTO> menuOptionDTOList = menuOptionEntityList.stream()
                .map(entity -> {return MenuOptionDTO.builder()
                        .menuOptionNo(entity.getMenuOptionNo())
                        .menuNo(entity.getMenuEntity().getMenuNo())
                        .menuName(entity.getMenuEntity().getName())
                        .name(entity.getName())
                        .storeNo(entity.getMenuEntity().getCategory1().getStoreEntity().getStoreNo())
                        .price(entity.getPrice())
                        .regDate(entity.getRegDate())
                        .modDate(entity.getModDate())
                        .build();
                })
                .collect(Collectors.toList());
        return menuOptionDTOList;
    }

    public List<RegionDTO> findRegion(){
        List<HotelEntity> hotelEntityList = hotelRepository.findAll();

// cartEntityList를 CartDTO 리스트로 매핑
        List<RegionDTO> regionDTOList = hotelEntityList.stream()
                .map(entity -> {return RegionDTO.builder()
                        .sido(entity.getSido())
                        .sigungu(entity.getSigungu())
                        .hotelCount(0)
                        .build();
                })
                .collect(Collectors.toList());
        return regionDTOList;
    }


    /*main*/

    public PageResponseDTO<HotelDTO> nowAvail(HotelDTO hotelDTO, PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("hotelNo");

        // Specification 생성
        Specification<HotelEntity> spec = Specification.where(null);


        //날짜를 입력하고 예약 가능한 객실이 있는 호텔목록을 검색
        if (hotelDTO.getStartDate() != null && hotelDTO.getEndDate() != null) {
            spec = spec.and(HotelSpecification.hasAvailableRooms(LocalDate.now(), LocalDate.now().plusDays(1)));
        }

        // Specification과 Pageable을 사용하여 페이징 처리된 결과 가져오기
        Page<HotelEntity> hotelEntityPage = hotelRepository.findAll(spec, pageable);

        // AdminEntity의 Stream을 AdminDTO로 변환 후 List로 수집
        List<HotelDTO> dtoList = hotelEntityPage.getContent().stream()
                .map(entity -> {
                    int lowestPrice = entity.getRoomEntity().stream()
                            .mapToInt(RoomEntity::getPrice)
                            .min()
                            .orElse(0);
                    List<HotelImgEntity> hotelImgEntityList = hotelImgService.findByHotelEntityHotelNo(entity.getHotelNo());
                    String imageUrl = "";
                    if (!hotelImgEntityList.isEmpty()) {
                        HotelImgEntity firstMenuImg = hotelImgEntityList.get(0);

                        imageUrl = "http://woori-prn.iptime.org/project/data/" + firstMenuImg.getImgName(); // 웹 경로 수정

                    }

                    return HotelDTO.builder()
                            .hotelNo(entity.getHotelNo())
                            .address(entity.getAddress())
                            .address2(entity.getAddress2())
                            .regDate(entity.getRegDate())
                            .modDate(entity.getModDate())
                            .postNumber(entity.getPostNumber())
                            .tel(entity.getTel())
                            .hotelType(entity.getHotelType())
                            .name(entity.getName())
                            .sido(entity.getSido())
                            .sigungu(entity.getSigungu())
                            .sido(entity.getSido())
                            .adminNo(entity.getAdminEntity().getAdminNo())
                            .rowPrice(lowestPrice) // 최소값이 존재하면 해당 값을, 그렇지 않으면 null을 rowPrice로 설정
                            .imageUrl(imageUrl)
                            .build();
                })
                .collect(Collectors.toList());

        // PageResponseDTO 생성
        return PageResponseDTO.<HotelDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) hotelEntityPage.getTotalElements())
                .build();
    }

    public boolean isEmailExists(String email) { //현재 이메일이 DB예 중복되어 존재하는지 확인하는 서비스
        return userRepository.findByEmail(email).isPresent(); //존재하는지 존재하지 않는지를 true false 로 전달
    }





}