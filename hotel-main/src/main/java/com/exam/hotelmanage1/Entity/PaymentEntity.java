package com.exam.hotelmanage1.Entity;

import com.exam.hotelmanage1.Constant.*;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = {"userEntity","reserveEntity","storeEntity"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "payment")
@SequenceGenerator(name = "payment_sql", sequenceName = "payment_sql",
        initialValue = 1,allocationSize = 1)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "paymentNo")
public class PaymentEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "payment_sql")
    private Long paymentNo;
    private int GrandTotalPrice;
    @OneToMany(mappedBy = "paymentEntity", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<PaymentItemEntity> paymentItemEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private UserEntity userEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserve_no")
    private ReserveEntity reserveEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_no")
    private StoreEntity storeEntity;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
}
