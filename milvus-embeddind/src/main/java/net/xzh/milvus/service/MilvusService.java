package net.xzh.milvus.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.milvus.pool.MilvusClientV2Pool;
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
public class MilvusService {

    private static final Logger log = LoggerFactory.getLogger(MilvusService.class);
    
    //类似于mysql中的表，定义一个名称为collection_02的集合
    private static final String COLLECTION_NAME = "collection_02";
    //向量维度定义1536，跟阿里巴巴embedding向量服务返回的维度保持一致
    private static final int VECTOR_DIM = 1536;
    private static final String CLIENT_KEY = "default";
    private static final Gson GSON = new Gson(); // 单例Gson对象

    @Autowired
    private MilvusClientV2Pool clientPool;
    @Autowired
    private EmbeddingModel embeddingModel;

    // 连接执行模板（通用连接管理）
    public <T> T execute(MilvusOperation<T> operation) {
        MilvusClientV2 client = clientPool.getClient(CLIENT_KEY);
        try {
            return operation.execute(client);
        } finally {
            clientPool.returnClient(CLIENT_KEY, client);
        }
    }

    @FunctionalInterface
    public interface MilvusOperation<T> {
        T execute(MilvusClientV2 client);
    }

    /**
     * 创建Collection
     */
    public void createCollection() {
        execute(client -> {
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
            return null;
        });
    }

    /**
     * 批量插入记录（优化性能）
     */
    public void batchInsertRecords(List<TestRecord> records) {
        // 预先生成所有向量（避免在连接执行块中操作）
        List<JsonObject> dataList = records.stream().map(record -> {
            float[] floatArray = embeddingModel.embed(record.getTitle());
            
            // 优化向量转换：避免装箱操作
            List<Float> vectorList = new ArrayList<>(floatArray.length);
            for (float f : floatArray) {
                vectorList.add(f);
            }
            
            JsonObject vector = new JsonObject();
            vector.addProperty("id", record.getId());
            vector.addProperty("title", record.getTitle());
            vector.add("title_vector", GSON.toJsonTree(vectorList)); // 使用单例GSON
            return vector;
        }).collect(Collectors.toList());

        // 一次性批量插入所有数据
        execute(client -> {
            InsertReq insertReq = InsertReq.builder()
                    .collectionName(COLLECTION_NAME)
                    .data(dataList)
                    .build();
            client.insert(insertReq);
            return null;
        });
    }

    /**
     * 通过ID获取记录
     */
    public GetResp getRecord(String id) {
        return execute(client -> {
            GetReq getReq = GetReq.builder()
                    .collectionName(COLLECTION_NAME)
                    .ids(Collections.singletonList(id))
                    .build();
            return client.get(getReq);
        });
    }

    /**
     * 向量检索
     */
    public List<List<SearchResp.SearchResult>> queryVector(String queryText) {
        // 外部执行向量转换
        float[] floatArray = embeddingModel.embed(queryText);

        return execute(client -> {
            SearchReq searchReq = SearchReq.builder()
                    .collectionName(COLLECTION_NAME)
                    .data(Collections.singletonList(new FloatVec(floatArray)))
                    .topK(3)
                    .outputFields(Collections.singletonList("*"))
                    .build();
            
            SearchResp searchResp = client.search(searchReq);
            List<List<SearchResp.SearchResult>> searchResults = searchResp.getSearchResults();
            
            // 使用Lambda简化日志输出
            searchResults.forEach(results -> 
                results.forEach(result -> 
                    log.info("ID={}, Score={}, Result={}", 
                            result.getId(), 
                            result.getScore(), 
                            result.getEntity())
                )
            );
            
            return searchResults;
        });
    }
}