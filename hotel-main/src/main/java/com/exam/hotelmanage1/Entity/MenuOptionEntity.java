package com.exam.hotelmanage1.Entity;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = "menuEntity")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "menu_option")
@SequenceGenerator(name = "menu_option_sql", sequenceName = "menu_option_sql",
        initialValue = 1,allocationSize = 1)
public class MenuOptionEntity  extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "menu_option_sql")
    @Column(name="menu_option_no")
    private Long menuOptionNo;
    private String name;
    private int price;
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="menu_No")
    private MenuEntity menuEntity;
    @OneToMany(mappedBy = "menuOptionEntity", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<CartEntity> cartEntity;
    @OneToMany(mappedBy = "menuOptionEntity", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<PaymentItemEntity> paymentItemEntity;
}
