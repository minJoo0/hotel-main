package com.exam.hotelmanage1.AdminServiceTest;

import com.exam.hotelmanage1.Constant.HotelType;
import com.exam.hotelmanage1.Constant.StoreType;
import com.exam.hotelmanage1.DTO.HotelDTO;
import com.exam.hotelmanage1.DTO.StoreDTO;
import com.exam.hotelmanage1.Entity.HotelEntity;
import com.exam.hotelmanage1.Entity.StoreEntity;
import com.exam.hotelmanage1.Repository.HotelRepository;
import com.exam.hotelmanage1.Service.HotelService;
import com.exam.hotelmanage1.Service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class StoreServiceTest {

    @Autowired
    private HotelService hotelService;
    @Autowired
    private StoreService storeService;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private HotelRepository hotelRepository;

    private List<HotelEntity> hotels = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // 각 호텔 정보를 불러오는 로직 (가정)
        hotels = hotelRepository.findAll();
    }

    @Test
    void createStoreForEveryHotel() {
        LocalDateTime now = LocalDateTime.now();
        hotels.forEach(hotel -> {
            for (int i = 1; i <= 3; i++) {
                StoreDTO storeDTO = new StoreDTO();
                storeDTO.setHotelNo(hotel.getHotelNo());
                storeDTO.setAdminNo(hotel.getAdminEntity().getAdminNo());
                storeDTO.setName(hotel.getName() + " 스토어 " + i);
                storeDTO.setHotelName(hotel.getName());
                storeDTO.setAddress(hotel.getAddress());
                storeDTO.setBusinessHours("09:00 - 18:00");
                storeDTO.setTel("010-1234-5678");
                storeDTO.setStoreType(i % 2 == 0 ? StoreType.DIRECT : StoreType.FRANCHISEE);
                storeDTO.setRegDate(now);
                storeDTO.setModDate(now);

                storeService.insert(storeDTO);
            }
        });

        // 검증 로직
        // 생성된 스토어가 예상대로 3개씩 생성되었는지 확인합니다.
        // 예를 들어, assertThat(storeRepository.count()).isEqualTo(hotels.size() * 3);
    }
}
