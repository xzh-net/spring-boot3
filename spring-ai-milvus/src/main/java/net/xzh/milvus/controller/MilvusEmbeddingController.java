package net.xzh.milvus.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.milvus.v2.service.vector.response.GetResp;
import io.milvus.v2.service.vector.response.SearchResp;
import net.xzh.milvus.model.TestRecord;
import net.xzh.milvus.service.MilvusEmbeddingService;
 
 
@RestController
@RequestMapping("/milvus")
public class MilvusEmbeddingController {
 
    @Autowired
    private MilvusEmbeddingService milvusEmbeddingService;
 
 
    @GetMapping("/createCollection")
    public void createCollection() {
        milvusEmbeddingService.createCollection();
    }
 
    @GetMapping("/insertRecord")
    public void insertRecord() throws IOException {
 
        TestRecord record = new TestRecord();
        record.setId("1");
        record.setTitle("男士纯棉圆领短袖T恤 白色 夏季休闲");
        milvusEmbeddingService.insertRecord(record);
 
        record = new TestRecord();
        record.setId("2");
        record.setTitle("女士碎花雪纺连衣裙 长款 春装");
        milvusEmbeddingService.insertRecord(record);
 
        record = new TestRecord();
        record.setId("3");
        record.setTitle("男款运动速干短袖 黑色 透气健身服");
        milvusEmbeddingService.insertRecord(record);
 
        record = new TestRecord();
        record.setId("4");
        record.setTitle("女童蕾丝公主裙 粉色 儿童节礼服");
        milvusEmbeddingService.insertRecord(record);
 
        record = new TestRecord();
        record.setId("5");
        record.setTitle("男士条纹POLO衫 商务休闲 棉质");
        milvusEmbeddingService.insertRecord(record);
    }
 
    @GetMapping("/getRecord")
    public GetResp getRecord(@RequestParam(name = "id") String id){
        GetResp resp = milvusEmbeddingService.getRecord(id);
        return resp;
    }
	
    @GetMapping("/queryVector")
    public List<List<SearchResp.SearchResult>> queryVector() {
        String queryText = "男款透气运动T恤";
        List<List<SearchResp.SearchResult>> searchResults = milvusEmbeddingService.queryVector(queryText);
        return searchResults;
    }
 
}