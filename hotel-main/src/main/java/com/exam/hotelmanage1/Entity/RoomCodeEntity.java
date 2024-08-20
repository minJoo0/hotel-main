package com.exam.hotelmanage1.Entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString(exclude = {"roomEntity", "codeGroupEntity"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "room_code")
@SequenceGenerator(
        name = "room_code_sql",
        sequenceName = "room_code_sql",
        initialValue = 1,
        allocationSize = 1)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "room_code")
public class RoomCodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "room_code_sql")
    private Long roomCodeNo;
    private Long codeNo;
    private String codeGroupName;
    private String codeName;
    private int orderIdx;
    private String fullCode;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="room_no")
    private RoomEntity roomEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="code_group_no")
    private CodeGroupEntity codeGroupEntity;
}
