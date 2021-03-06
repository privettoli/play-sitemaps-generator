package com.uawebchallenge.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.InheritanceType.JOINED;
import static lombok.AccessLevel.PRIVATE;

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

    @OneToMany(fetch = EAGER, cascade = ALL)
    List<UrlSetData> urlSets;
}
