package com.exam.hotelmanage1.Entity;


import jakarta.persistence.*;
import lombok.*;
/*================

작업명:FAQ관리crud
작업자:김민주
작업일:2024-04-12
작업내용:테이블생성

===============*/

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "faq")
//순차처리가 필요한 경우 = AutoIncrement //DB삭제등으로 인해 순차가 바뀌지않게 ?
@SequenceGenerator(name = "faq_sql", sequenceName = "faq_sql",
        initialValue = 1,allocationSize = 1)
public class FaqEntity extends BaseEntity{
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "faq_sql")
    private Long faqNo;

    private String category;//분류
    private String title;//공지제목

    @Column(name="content", columnDefinition="TEXT")
    private String content;//공지내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_no")
    private AdminEntity adminEntity;


}
