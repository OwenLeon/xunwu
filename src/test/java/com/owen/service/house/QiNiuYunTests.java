package com.owen.service.house;

import com.owen.ApplicationTests;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * Created by Administrator on 2018/7/16.
 */
public class QiNiuYunTests  extends ApplicationTests{
    @Autowired
    private IQiNiuService qiNiuService;

    @Test
    public void  testUpload(){
        String fileName ="F:/picture/xunwu/temp/微信截图_20180615194528.png";
        File file=new File(fileName);
        Assert.assertTrue(file.exists());
        try {
            Response response = qiNiuService.uploadFile(file);
            Assert.assertTrue(response.isOK());
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }

    @Test
    public  void  deletePicture(){
        try {
            Response response = qiNiuService.delete("Fv1VhgbK7SNeMLIlQ2EyvQDW2r5A");
            Assert.assertTrue(response.isOK());
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }
}
