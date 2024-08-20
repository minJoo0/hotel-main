package com.exam.hotelmanage1.Entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Getter
@Setter
@ToString(exclude = {"paymentItemEntity"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "revenue")
@SequenceGenerator(
        name = "revenue_sql",
        sequenceName = "revenue_sql",
        initialValue = 1,
        allocationSize = 1)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "revenue_no")
public class RevenueEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "revenue_sql")
    private Long revenueNo;
    private Integer fee;
    private Integer depositPrice;
    private Integer userCount;

    @CreatedDate
    private LocalDate paymentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="payment_item_no")
    private PaymentItemEntity paymentItemEntity;

}
