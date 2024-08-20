package com.exam.hotelmanage1.Entity;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
/*================

작업명:공지사항관리crud
작업자:김민주
작업일:2024-04-09

===============*/

@Entity
@Getter
@Setter
@ToString(exclude = "hotelEntity")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "notice")
//순차처리가 필요한 경우 = AutoIncrement //DB삭제등으로 인해 순차가 바뀌지않게 ?
@SequenceGenerator(name = "notice_sql", sequenceName = "notice_sql",
        initialValue = 1,allocationSize = 1)
public class NoticeEntity extends BaseEntity{
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "notice_sql")
    private Long noticeNo;

    private String title;//공지제목

    @Column(name="content", columnDefinition="TEXT")
    private String content;//공지내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_no")
    private AdminEntity adminEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_no")
    @Nullable
    private HotelEntity hotelEntity;



}
