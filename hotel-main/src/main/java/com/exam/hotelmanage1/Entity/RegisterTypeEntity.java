package com.exam.hotelmanage1.Entity;

import com.exam.hotelmanage1.Constant.RegisterType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString(exclude = "userEntity")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "registerType")
@SequenceGenerator(name = "registerType_sql", sequenceName = "registerType_sql",
        initialValue = 1,allocationSize = 1)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "registerTypeNo")
public class RegisterTypeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "registerType_sql")
    private Long registerTypeNo;
    @Enumerated(EnumType.STRING)
    private RegisterType registerType;
    private String userId;
    private Long snsUniqueId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private UserEntity userEntity;     //유저와 조인
}
