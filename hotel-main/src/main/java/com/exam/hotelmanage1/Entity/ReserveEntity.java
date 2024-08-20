package com.exam.hotelmanage1.Entity;

import com.exam.hotelmanage1.Constant.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = {"roomEntity", "userEntity"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "reserve")
@SequenceGenerator(
        name = "reserve_sql",
        sequenceName = "reserve_sql",
        initialValue = 1,
        allocationSize = 1)
public class ReserveEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "reserve_sql")
    private Long reserveNo;     //예약 id 번호

    @Enumerated(EnumType.STRING)
    private ReservationStatus status; // 예약 상태

    @Column(name="startDate")
    private LocalDate startDate;    // 체크인

    @Column(name="endDate")
    private LocalDate endDate;      // 체크 아웃

    @Column(name = "people")
    private Integer people;         // 예약 인원수

    @OneToMany(mappedBy = "reserveEntity", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<PaymentEntity> paymentEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_no")
    private RoomEntity roomEntity;      //객실과 조인

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private UserEntity userEntity;     //유저와 조인


}
