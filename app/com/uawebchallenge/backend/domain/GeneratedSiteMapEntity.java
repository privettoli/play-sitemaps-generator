package com.uawebchallenge.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.LazyCollection;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.Table;
import java.util.List;

import static javax.persistence.InheritanceType.JOINED;
import static lombok.AccessLevel.PRIVATE;
import static org.hibernate.annotations.LazyCollectionOption.TRUE;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "site_map")
@FieldDefaults(level = PRIVATE)
@Inheritance(strategy = JOINED)
@EqualsAndHashCode(callSuper = true)
public class GeneratedSiteMapEntity extends AbstractEntity {
    String url;
    @ElementCollection
    @LazyCollection(TRUE)
    List<byte[]> xml;
}
