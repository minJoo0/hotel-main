package com.exam.hotelmanage1.Entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString(exclude = {"menuEntity","menuOptionEntity","userEntity"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "cart")
@SequenceGenerator(name = "cart_sql", sequenceName = "cart_sql",
        initialValue = 1,allocationSize = 1)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "cartNo")
public class CartEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "cart_sql")
    @Column(name="cartNo")
    private Long cartNo;
    private int cartCount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="user_no")
    private UserEntity userEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="menu_option_no")
    private MenuOptionEntity menuOptionEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="menu_no")
    private MenuEntity menuEntity;

}
