package com.charite.repository;

import com.charite.entity.Media;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends MongoRepository<Media, String> {
    List<Media> findByActionChariteId(Long actionChariteId);
}
