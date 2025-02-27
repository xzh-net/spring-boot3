package net.xzh.milvus.service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.AddFieldReq;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.vector.request.GetReq;
import io.milvus.v2.service.vector.request.InsertReq;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.GetResp;
import io.milvus.v2.service.vector.response.SearchResp;
import net.xzh.milvus.model.TestRecord;
 
@Component
public class MilvusEmbeddingService {
 
    private static final Logger log = LoggerFactory.getLogger(MilvusEmbeddingService.class);
 
    //类似于mysql中的表，定义一个名称为collection_02的集合
    private static final String COLLECTION_NAME = "collection_02";
    //向量维度定义1536，跟阿里巴巴embedding向量服务返回的维度保持一致
    private static final int VECTOR_DIM = 1536;
 
    private final MilvusClientV2 client;
    //注入阿里巴巴EmbeddingModel
    @Autowired
    private EmbeddingModel embeddingModel;
 
 
    public MilvusEmbeddingService(MilvusClientV2 client) {
        this.client = client;
    }
 
    /**
     * 创建一个Collection
     */
    public void createCollection() {
 
        CreateCollectionReq.CollectionSchema schema = client.createSchema();
 
        schema.addField(AddFieldReq.builder()
                .fieldName("id")
                .dataType(DataType.VarChar)
                .isPrimaryKey(true)
                .autoID(false)
                .build());
 
        schema.addField(AddFieldReq.builder()
                .fieldName("title")
                .dataType(DataType.VarChar)
                .maxLength(10000)
                .build());
 
        schema.addField(AddFieldReq.builder()
                .fieldName("title_vector")
                .dataType(DataType.FloatVector)
                .dimension(VECTOR_DIM)
                .build());
 
        IndexParam indexParam = IndexParam.builder()
                .fieldName("title_vector")
                .metricType(IndexParam.MetricType.COSINE)
                .build();
 
        CreateCollectionReq createCollectionReq = CreateCollectionReq.builder()
                .collectionName(COLLECTION_NAME)
                .collectionSchema(schema)
                .indexParams(Collections.singletonList(indexParam))
                .build();
 
        client.createCollection(createCollectionReq);
    }
 
    /**
     * 往collection中插入一条数据
     */
    public void insertRecord(TestRecord record) {
        JsonObject vector = new JsonObject();
        vector.addProperty("id", record.getId());
        vector.addProperty("title", record.getTitle());
        List<Float> vectorList = new ArrayList<>();
        Gson gson = new Gson();
        //调用阿里向量模型服务，返回1536维向量float
        float[] floatArray = embeddingModel.embed(record.getTitle());
        for (float f : floatArray) {
            vectorList.add(f);
        }
 
        vector.add("title_vector", gson.toJsonTree(vectorList));
 
        InsertReq insertReq = InsertReq.builder()
                .collectionName(COLLECTION_NAME)
                .data(Collections.singletonList(vector))
                .build();
        client.insert(insertReq);
    }
    
    /**
     * 通过ID获取记录
     */
    public GetResp getRecord(String id) {
        GetReq getReq = GetReq.builder()
                .collectionName(COLLECTION_NAME)
                .ids(Collections.singletonList(id))
                .build();
        GetResp resp = client.get(getReq);
        return resp;
    }
 
    /**
     * 按照向量检索，找到相似度最近的topK
     */
    public List<List<SearchResp.SearchResult>>  queryVector(String queryText) {
        //调用阿里向量模型服务，对查询条件进行向量化
        float[] floatArray = embeddingModel.embed(queryText);
 
        SearchResp searchR = client.search(SearchReq.builder()
                .collectionName(COLLECTION_NAME)
                .data(Collections.singletonList(new FloatVec(floatArray)))
                .topK(3)
                .outputFields(Collections.singletonList("*"))
                .build());
        List<List<SearchResp.SearchResult>> searchResults = searchR.getSearchResults();
        for (List<SearchResp.SearchResult> results : searchResults) {
            for (SearchResp.SearchResult result : results) {
                log.info("ID="+(String)result.getId() + ",Score="+result.getScore() + ",Result="+result.getEntity().toString());
            }
        }
        return searchResults;
    }
 
}