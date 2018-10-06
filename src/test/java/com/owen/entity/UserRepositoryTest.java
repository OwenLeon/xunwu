package com.owen.entity;

import com.owen.ApplicationTests;
import com.owen.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Administrator on 2018/7/9.
 */
public class UserRepositoryTest extends ApplicationTests{
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindOne(){
        User user = userRepository.findOne(1L);
        Assert.assertEquals("wali",user.getName());
    }
}
