package com.exam.hotelmanage1.Service;

import com.exam.hotelmanage1.DTO.KakaoDTO;
import com.exam.hotelmanage1.DTO.UserDTO;
import com.exam.hotelmanage1.Entity.UserEntity;
import com.exam.hotelmanage1.Repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
@Getter
public class KakaoService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Value("${kakao.restApiKey}")
    private String restApiKey;

    @Value("${kakao.redirectUri}")
    private String redirectUri;

    public KakaoDTO getKakaoUserInfo(String accessToken) {
        try {
            // 카카오 API 호출
            String apiUrl = "https://kapi.kakao.com/v2/user/me";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            // API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl, HttpMethod.POST, request, String.class);
            // 응답 데이터를 JSON 문자열로 받습니다.
            String responseBody = response.getBody();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            Long kakaoNo = jsonNode.get("id").asLong();
            String connected_at = jsonNode.get("connected_at").asText();
            String email = jsonNode.get("kakao_account").get("email").asText();
            String nickname = jsonNode.get("properties")
                    .get("nickname").asText();

// KakaoDTO 객체 생성
            KakaoDTO kakaoDTO = KakaoDTO.builder()
                    .kakaoNo(kakaoNo)
                    .connectedAt(connected_at)
                    .kakaoNickname(nickname)
                    .registerType("KAKAO")
                    .kakaoEmail(email)
                    .build();

            log.info(kakaoDTO);

            return kakaoDTO;


        } catch (Exception e) {
            // 에러 처리
            throw new RuntimeException("카카오 사용자 정보 조회 실패");
        }
    }

    public String getAccessToken(String code) {
        try {
            String apiUrl = "https://kauth.kakao.com/oauth/token";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", restApiKey);
            params.add("redirect_uri", redirectUri); // 카카오 개발자 사이트에서 설정한 redirect URI
            params.add("code", code);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);

            return response.getBody().get("access_token").toString();
        } catch (Exception e) {
            throw new RuntimeException("access token 요청 실패");
        }
    }

    public void logout(String accessToken) {
        try {
            String apiUrl = "https://kapi.kakao.com/v1/user/logout";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("카카오 로그아웃 성공");
            } else {
                throw new RuntimeException("카카오 로그아웃 실패");
            }
        } catch (Exception e) {
            throw new RuntimeException("카카오 로그아웃 요청 실패", e);
        }
    }

    public void storeAccessToken(HttpServletRequest request , String accessToken) {
        HttpSession session = request.getSession();
        session.setAttribute("kakaoAccessToken", accessToken);
    }

    public UserEntity ifNeedKakaoInfo (KakaoDTO kakaoDTO) {
        // DB에 중복되는 email이 있는지 확인
        String kakaoEmail = kakaoDTO.getKakaoEmail();
        Optional<UserEntity> kakaoMember = userRepository.findByEmail(kakaoEmail);
        return kakaoMember.orElse(null);



    }


}
