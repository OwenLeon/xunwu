package com.owen.service.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.owen.entity.House;
import com.owen.entity.HouseDetail;
import com.owen.entity.HouseTag;
import com.owen.repository.HouseDetailRepository;
import com.owen.repository.HouseRepository;
import com.owen.repository.HouseTagRepository;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 瓦力.
 */
@Service
public class SearchServiceImpl implements ISearchService {
    private static final Logger logger = LoggerFactory.getLogger(ISearchService.class);

    private static final String INDEX_NAME = "xunwu";

    private static final String INDEX_TYPE = "house";

    private static final String INDEX_TOPIC = "house_build";



    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private HouseDetailRepository houseDetailRepository;

    @Autowired
    private HouseTagRepository houseTagRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TransportClient esClient;

    @Autowired
    private ObjectMapper objectMapper;

    //构建索引:1.先从数据库中查出来
    @Override
    public void index(Long houseId) {
        House house=houseRepository.findOne(houseId);
        if(house==null){
            logger.error("Index Hhouse {} dose not exist!",houseId);
            return;
        }
        HouseIndexTemplate indexTemplate = new HouseIndexTemplate();
        //进行映射
        modelMapper.map(house,indexTemplate);

        HouseDetail detail = houseDetailRepository.findByHouseId(houseId);
        if(detail==null){
            // TODO 异常情况
        }

        //进行映射
        modelMapper.map(detail,indexTemplate);

        List<HouseTag> tags = houseTagRepository.findAllByHouseId(houseId);
        if(tags!=null && !tags.isEmpty()){
            List<String> tagString = new ArrayList<>();
            tags.forEach( houseTag -> tagString.add(houseTag.getName()));
            indexTemplate.setTags(tagString);
        }

        SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE).setQuery(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId));

        logger.debug(requestBuilder.toString());
        SearchResponse searchResponse = requestBuilder.get();


        boolean success;
        long totalHits = searchResponse.getHits().getTotalHits();
        if(totalHits==0){
            success = create(indexTemplate);
        }else if(totalHits==1){
            String esId = searchResponse.getHits().getAt(0).getId();
            success = update(esId,indexTemplate);
        }else {
            success =  deleteAndCreate(totalHits,indexTemplate);
        }

        if(success){
            logger.debug("Index success with house "+houseId);
        }

    }

    private boolean create(HouseIndexTemplate indexTemplate){
        try {
            IndexResponse response = this.esClient.prepareIndex(INDEX_NAME, INDEX_TYPE).
                    setSource(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();
            logger.debug("Create index with house: "+ indexTemplate.getHouseId());

            if(response.status() == RestStatus.CREATED){
                return true;
            }else {
                return false;
            }

        } catch (JsonProcessingException e) {
            logger.error("Error to index house " +indexTemplate.getHouseId());
            return false;
        }
    }

    private boolean update(String esId,HouseIndexTemplate indexTemplate){
        try {
            UpdateResponse response = this.esClient.prepareUpdate(INDEX_NAME, INDEX_TYPE,esId).
                    setDoc(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();
            logger.debug("Update index with house: "+ indexTemplate.getHouseId());

            if(response.status() == RestStatus.OK){
                return true;
            }else {
                return false;
            }
        } catch (JsonProcessingException e) {
            logger.error("Error to index house " +indexTemplate.getHouseId());
            return false;
        }
    }

    private boolean deleteAndCreate(long totalHit,HouseIndexTemplate indexTemplate){
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE.newRequestBuilder(esClient).
                filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, indexTemplate.getHouseId())).source(INDEX_NAME);
        logger.debug("Delete by query for house: "+ builder);
        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        if(deleted != totalHit){
            logger.warn("Need delete {},but {} was deleted!",totalHit,deleted);
            return false;
        }else {
            return create(indexTemplate);
        }
    }









    @Override
    public void remove(Long houseId) {
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE.newRequestBuilder(esClient).
                filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId)).source(INDEX_NAME);
        logger.debug("Delete by query for house: "+ builder);
        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        logger.debug("Delete total "+ deleted);
    }
}
