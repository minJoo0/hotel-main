package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.Repository.HotelRepository;
import com.exam.hotelmanage1.Repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@Getter
@Setter
@RequiredArgsConstructor
@Transactional
public class AdminNoCheckService {

        private final HotelRepository hotelRepository;
        private final StoreRepository storeRepository;

        public Long adminNocheck(Principal principal){
            String userid = principal.getName();
            return hotelRepository.findCompanyNoByUsername(userid);
        }
    public Long storeNocheck(Principal principal){
        String userid = principal.getName();
        return storeRepository.findStoreNoByUserid(userid);
    }


}
