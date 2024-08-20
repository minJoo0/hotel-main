package com.exam.hotelmanage1.DTO;

import com.exam.hotelmanage1.Constant.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private Long userNo;
    private String email; //이메일
    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;
    @NotBlank(message = "이름을 입력해 주세요")
    private String name;
    @NotBlank(message = "닉네임을 입력해주세요")
    private String nickName;
    @NotBlank(message = "성별을 입력해주세요")
    private String gender;
    @NotNull(message = "생년월일을 입력해주세요")
    private LocalDate birth;
    @NotBlank(message = "전화번호를 입력해주세요")
    private String phone;
    private String registerType;
    private RoleType roleType;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
