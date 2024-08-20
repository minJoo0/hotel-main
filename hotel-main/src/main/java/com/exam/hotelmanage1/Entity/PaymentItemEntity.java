package com.exam.hotelmanage1.Entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = {"paymentEntity","menuEntity","menuOptionEntity"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "payment_item")
@SequenceGenerator(name = "payment_item_sql", sequenceName = "payment_item_sql",
        initialValue = 1,allocationSize = 1)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "payment_itemNo")
public class PaymentItemEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "payment_item_sql")
    private Long paymentItemNo;
    private int paymentCount;
    private int unitPrice;
    private int totalPrice;
    private int optionPrice;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_no")
    private PaymentEntity paymentEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_no")
    private MenuEntity menuEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_option_no")
    private MenuOptionEntity menuOptionEntity;

    @OneToMany(mappedBy = "paymentItemEntity", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<RevenueEntity> revenueEntity;
}
