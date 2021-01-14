package com.fastcampus.common.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class AbstractDomainEntity implements Serializable {

  @LastModifiedDate
  @Column(name = "update_date")
  protected LocalDateTime updateDate;

  @CreatedDate
  @Column(name = "create_date", nullable = false, updatable = false)
  protected LocalDateTime createDate;
}

