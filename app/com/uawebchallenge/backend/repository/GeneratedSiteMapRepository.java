package com.uawebchallenge.backend.repository;

import com.uawebchallenge.backend.domain.GeneratedSiteMapEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by Anatoliy Papenko on 3/28/15.
 */
public interface GeneratedSiteMapRepository extends Repository<GeneratedSiteMapEntity, Long> {
    Optional<GeneratedSiteMapEntity> findBy(Long id);

    List<GeneratedSiteMapEntity> findAll(Sort sort);
}
