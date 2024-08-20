package com.exam.hotelmanage1.DTO;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomCodeDTO {
    private Long roomCodeNo;
    private Long roomNo;
    private String codeGroupName;
    private String codeName;
    private int orderIdx;
    private String fullCode;
    private String fullCodes;
    private Long codeGroupNo;
    private Long codeNo;
}
