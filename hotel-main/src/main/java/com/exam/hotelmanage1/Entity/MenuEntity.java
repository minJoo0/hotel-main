package com.exam.hotelmanage1.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = "category1")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "menu")
@SequenceGenerator(name = "menu_sql", sequenceName = "menu_sql",
        initialValue = 1,allocationSize = 1)
public class MenuEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "menu_sql")
    @Column(name="menu_no")
    private Long menuNo;
    private String name;
    private int price;
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="category1")
    private CategoryEntity category1;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="category2")
    private CategoryEntity category2;
    @OneToMany(mappedBy = "menuEntity", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<MenuImgEntity> menuImgEntity;
    @OneToMany(mappedBy = "menuEntity", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<MenuOptionEntity> menuOptionEntity;
    @OneToMany(mappedBy = "menuEntity", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<CartEntity> cartEntity;
    @OneToMany(mappedBy = "menuEntity", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<PaymentItemEntity> paymentItemEntity;
}
