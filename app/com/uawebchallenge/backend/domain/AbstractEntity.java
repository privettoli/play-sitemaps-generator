package com.uawebchallenge.backend.domain;

import com.uawebchallenge.backend.converter.DateConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.ZonedDateTime;

import static javax.persistence.GenerationType.TABLE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter
@MappedSuperclass
@FieldDefaults(level = PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class AbstractEntity {
    @Id
    @GeneratedValue(strategy = TABLE)
    Long id;

    @CreatedDate
    @Convert(converter = DateConverter.class)
    ZonedDateTime createdDate;

    @LastModifiedDate
    @Convert(converter = DateConverter.class)
    ZonedDateTime modifiedDate;
}
