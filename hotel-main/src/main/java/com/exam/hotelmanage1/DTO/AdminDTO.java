package com.exam.hotelmanage1.DTO;

import com.exam.hotelmanage1.Constant.RoleType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminDTO {
    private Long adminNo;
    private String userid;
    private String password;
    private String name;
    private String email;
    private String phone;
    private RoleType roleType;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
