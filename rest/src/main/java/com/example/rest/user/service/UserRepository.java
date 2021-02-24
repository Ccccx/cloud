package com.example.rest.user.service;

import com.example.rest.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-01-26 14:06
 */
@RepositoryRestResource(collectionResourceRel = "user", path = "user")
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    /**
     * 根据用户昵称查询
     * @param pageable  分页参数
     * @param nickname 用户昵称
     * @return  结果
     */
    Page<User> findByNickname(Pageable pageable, @Param("nickname") String nickname);
}
