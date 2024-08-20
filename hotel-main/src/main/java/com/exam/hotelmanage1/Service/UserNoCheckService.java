package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.Repository.HotelRepository;
import com.exam.hotelmanage1.Repository.UserRepository;
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
public class UserNoCheckService {

        private final UserRepository userRepository;

        public Long userNocheck(Principal principal){
            String email = principal.getName();
            return userRepository.findUserNoByEmail(email);
        }
}
