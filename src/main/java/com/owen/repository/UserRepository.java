package com.owen.repository;

import com.owen.entity.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Administrator on 2018/7/9.
 */
public interface UserRepository extends CrudRepository<User,Long>{
    User findByName(String userName);
}
