package com.owen.web.controller.house;
import com.owen.base.ApiResponse;
import com.owen.base.RentValueBlock;
import com.owen.entity.SupportAddress;
import com.owen.service.IUserService;
import com.owen.service.ServiceMultiResult;
import com.owen.service.ServiceResult;
import com.owen.service.house.IAddressService;
import com.owen.service.house.IHouseService;
import com.owen.service.search.ISearchService;
import com.owen.web.dto.*;

import com.owen.web.form.RentSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * Created by 瓦力.
 */
@Controller
public class HouseController {

    @Autowired
    private IAddressService addressService;

    @Autowired
    private IHouseService houseService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ISearchService searchService;

    /**
     * 获取支持城市列表
     * @return
     */
    @GetMapping("address/support/cities")
    @ResponseBody
    public ApiResponse getSupportCities() {
        ServiceMultiResult<SupportAddressDTO> result = addressService.findAllCities();
        if (result.getResultSize() == 0) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(result.getResult());
    }

    /**
     * 获取对应城市支持区域列表
     * @param cityEnName
     * @return
     */
    @GetMapping("address/support/regions")
    @ResponseBody
    public  ApiResponse getSupportRegions(@RequestParam(name = "city_name") String cityEnName){
        ServiceMultiResult<SupportAddressDTO> result = addressService.findAllRegionsByCityName(cityEnName);
        if (result.getResultSize() == 0) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(result.getResult());
    }
    /**
     * 获取具体城市所支持的地铁线路
     * @param cityEnName
     * @return
     */
    @GetMapping("address/support/subway/line")
    @ResponseBody
    public ApiResponse getSupportSubwayLine(@RequestParam(name = "city_name") String cityEnName) {
        List<SubwayDTO> subways = addressService.findAllSubwayByCity(cityEnName);
        if (subways.isEmpty()) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }

        return ApiResponse.ofSuccess(subways);
    }
    /**
     * 获取对应地铁线路所支持的地铁站点
     * @param subwayId
     * @return
     */
    @GetMapping("address/support/subway/station")
    @ResponseBody
    public ApiResponse getSupportSubwayStation(@RequestParam(name = "subway_id") Long subwayId) {
        List<SubwayStationDTO> stationDTOS = addressService.findAllStationBySubway(subwayId);
        if (stationDTOS.isEmpty()) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }

        return ApiResponse.ofSuccess(stationDTOS);
    }

    @GetMapping("rent/house")
    public String rentHousePage(@ModelAttribute RentSearch rentSearch, Model model, HttpSession session,
                                RedirectAttributes redirectAttributes){
        if(rentSearch.getCityEnName() == null){
            String cityEnNameInSession = (String)session.getAttribute("cityEnName");
            if(cityEnNameInSession == null){
                redirectAttributes.addAttribute("msg","must_chose_city");
                return "redirect:/index";
            }else {
                rentSearch.setCityEnName(cityEnNameInSession);
            }
        }else {
            session.setAttribute("cityName",rentSearch.getCityEnName());
        }

        ServiceResult<SupportAddressDTO> city = addressService.findCity(rentSearch.getCityEnName());
        if(!city.isSuccess()){
            redirectAttributes.addAttribute("msg","must_chose_city");
            return "redirect:/index";
        }
        model.addAttribute("currentCity", city.getResult());

        ServiceMultiResult<SupportAddressDTO> addressResult = addressService.findAllRegionsByCityName(rentSearch.getCityEnName());
        if(addressResult == null || addressResult.getTotal() < 1 ){
            redirectAttributes.addAttribute("msg","must_chose_city");
            return "redirect:/index";
        }
        ServiceMultiResult<HouseDTO> serviceMultiResult = houseService.query(rentSearch);
        model.addAttribute("total", serviceMultiResult.getTotal());
        model.addAttribute("houses",serviceMultiResult.getResult());
        //区间
        if(rentSearch.getRegionEnName() == null){
            rentSearch.setRegionEnName("*");
        }
        model.addAttribute("searchBody",rentSearch);
        model.addAttribute("regions",addressResult.getResult());
        model.addAttribute("priceBlocks", RentValueBlock.PRICE_BLOCK);
        model.addAttribute("areaBlocks",RentValueBlock.AREA_BLOCK);
        model.addAttribute("currentPriceBlock",RentValueBlock.matchPrice(rentSearch.getPriceBlock()));
        model.addAttribute("currentAreaBlock",RentValueBlock.matchArea(rentSearch.getAreaBlock()));
        return "rent-list";
    }

    @GetMapping("rent/house/show/{id}")
    public String show(@PathVariable(value = "id") Long houseId,
                       Model model) {
        if (houseId <= 0) {
            return "404";
        }

        ServiceResult<HouseDTO> serviceResult = houseService.findCompleteOne(houseId);
        if (!serviceResult.isSuccess()) {
            return "404";
        }


        HouseDTO houseDTO = serviceResult.getResult();
        Map<SupportAddress.Level, SupportAddressDTO>
                addressMap = addressService.findCityAndRegion(houseDTO.getCityEnName(), houseDTO.getRegionEnName());

        SupportAddressDTO city = addressMap.get(SupportAddress.Level.CITY);
        SupportAddressDTO region = addressMap.get(SupportAddress.Level.REGION);

        model.addAttribute("city", city);
        model.addAttribute("region", region);

        ServiceResult<UserDTO> userDTOServiceResult = userService.findById(houseDTO.getAdminId());
        model.addAttribute("agent", userDTOServiceResult.getResult());
        model.addAttribute("house", houseDTO);

       // ServiceResult<Long> aggResult = searchService.aggregateDistrictHouse(city.getEnName(), region.getEnName(), houseDTO.getDistrict());
       // model.addAttribute("houseCountInDistrict", aggResult.getResult());
        model.addAttribute("houseCountInDistrict", 0);
        return "house-detail";
    }

}
