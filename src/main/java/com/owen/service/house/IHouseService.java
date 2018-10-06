package com.owen.service.house;

import com.owen.base.HouseSubscribeStatus;
import com.owen.service.ServiceMultiResult;
import com.owen.service.ServiceResult;
import com.owen.web.dto.HouseDTO;
import com.owen.web.dto.HouseSubscribeDTO;
import com.owen.web.dto.SubwayDTO;
import com.owen.web.form.DatatableSearch;
import com.owen.web.form.HouseForm;
import com.owen.web.form.MapSearch;
import com.owen.web.form.RentSearch;
import org.springframework.data.util.Pair;

import java.util.Date;

/**
 * 房屋管理服务接口
 * Created by 瓦力.
 */
public interface IHouseService {
    /**
     * 新增
     * @param houseForm
     * @return
     */
    ServiceResult<HouseDTO> save(HouseForm houseForm);


    ServiceMultiResult<HouseDTO> adminQuery(DatatableSearch searchBody);

    /**
     * 查询完整房源信息
     * @param id
     * @return
     */
    ServiceResult<HouseDTO> findCompleteOne(Long id);

    ServiceResult update(HouseForm houseForm);

    /**
     * 新增标签
     * @param houseId
     * @param tag
     * @return
     */
    ServiceResult addTag(Long houseId, String tag);
    /**
     * 移除标签
     * @param houseId
     * @param tag
     * @return
     */
    ServiceResult removeTag(Long houseId, String tag);


    /**
     * 移除图片
     * @param id
     * @return
     */
    ServiceResult removePhoto(Long id);

    /**
     * 更新封面
     * @param coverId
     * @param targetId
     * @return
     */
    ServiceResult updateCover(Long coverId, Long targetId);

    /**
     * 更新房源状态
     * @param id
     * @param status
     * @return
     */
    ServiceResult updateStatus(Long id, int status);

    /**
     * 查询房源信息集
     * @param rentSearch
     * @return
     */
    ServiceMultiResult<HouseDTO> query(RentSearch rentSearch);
}
