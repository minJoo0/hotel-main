package com.exam.hotelmanage1.Entity;

import com.exam.hotelmanage1.Constant.RoleType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "lowcode")
@SequenceGenerator(name = "lowcode_sql", sequenceName = "lowcode_sql",
        initialValue = 1,allocationSize = 1)
public class LowCodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "lowcode_sql")
    @Column(name="lowCodeNo")
    private Long lowCodeNo;
    @Column(name = "codeType", unique = true)
    private String codeType;
    private String codeName;
    private int codeDisplayNo;
}
