package com.uawebchallenge.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.InheritanceType.JOINED;
import static lombok.AccessLevel.PRIVATE;

/**
 * Created by Anatoliy Papenko on 3/30/15.
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "site_map_data")
@Inheritance(strategy = JOINED)
@FieldDefaults(level = PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class UrlSetData extends AbstractEntity {
    @Lob
    byte[] data;
}
