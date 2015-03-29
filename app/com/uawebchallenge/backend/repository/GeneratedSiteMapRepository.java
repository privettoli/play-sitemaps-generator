package com.uawebchallenge.backend.repository;

import com.uawebchallenge.backend.domain.GeneratedSiteMapEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Anatoliy Papenko on 3/28/15.
 */
public interface GeneratedSiteMapRepository extends CrudRepository<GeneratedSiteMapEntity, Long> {
    List<GeneratedSiteMapEntity> findAll(Sort sort);
}
