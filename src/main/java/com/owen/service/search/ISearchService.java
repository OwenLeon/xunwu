package com.owen.service.search;


import com.owen.service.ServiceMultiResult;
import com.owen.service.ServiceResult;
import com.owen.web.form.RentSearch;

import java.util.List;

/**
 * 检索接口
 * Created by 瓦力.
 */
public interface ISearchService {
    /**
     * 索引目标房源
     * @param houseId
     */
    void index(Long houseId);

    /**
     * 移除房源索引
     * @param houseId
     */
    void remove(Long houseId);


}
