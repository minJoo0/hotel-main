package com.exam.hotelmanage1.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter @Setter @ToString
@EntityListeners(value = {AuditingEntityListener.class})
//참고용.개별사용불가능.
@MappedSuperclass
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false) //업데이트 불가
    private LocalDateTime regDate;

    @LastModifiedDate
    @Column
    private LocalDateTime modDate;





}
