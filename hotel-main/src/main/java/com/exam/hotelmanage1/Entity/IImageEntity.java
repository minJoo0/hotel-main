package com.exam.hotelmanage1.Entity;

import jakarta.persistence.EntityListeners;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


public interface IImageEntity {

    String getImgName();
}
