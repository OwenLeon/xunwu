package com.owen.service;

/**
 * Created by Administrator on 2018/7/9.
 */
public interface ISmsService {
    /**
     * 发送验证码到指定的手机 并缓存验证码10分钟 及请求间隔1分钟
     */
    ServiceResult<String> sendSms(String telephone);

    /**
     * 获取缓存中的验证码
     */
    String getSmsCode(String telephone);

    /**
     * 移除指定手机号的验证码缓存
     */
    void remove(String telephone);
}
