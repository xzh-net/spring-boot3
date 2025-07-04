package net.xzh.milvus.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.milvus.v2.service.vector.response.GetResp;
import io.milvus.v2.service.vector.response.SearchResp;
import net.xzh.milvus.model.TestRecord;
import net.xzh.milvus.service.MilvusService;

@RestController
@RequestMapping("/milvus")
public class MilvusController {

    @Autowired
    private MilvusService milvusService;

    @GetMapping("/createCollection")
    public String createCollection() {
        milvusService.createCollection();
        return "Collection created successfully";
    }

    @GetMapping("/insertRecords")
    public String insertRecords() {
        List<TestRecord> records = Arrays.asList(
            createRecord("1", "男士纯棉圆领短袖T恤 白色 夏季休闲"),
            createRecord("2", "女士碎花雪纺连衣裙 长款 春装"),
            createRecord("3", "男款运动速干短袖 黑色 透气健身服"),
            createRecord("4", "女童蕾丝公主裙 粉色 儿童节礼服"),
            createRecord("5", "男士条纹POLO衫 商务休闲 棉质")
        );
        milvusService.batchInsertRecords(records);
        return "5 records inserted successfully";
    }

    @GetMapping("/getRecord")
    public GetResp getRecord(@RequestParam(name = "id") String id) {
        return milvusService.getRecord(id);
    }
    
    @GetMapping("/queryVector")
    public List<List<SearchResp.SearchResult>> queryVector(
            @RequestParam(name = "queryText", defaultValue = "男款透气运动T恤") String queryText) {
        return milvusService.queryVector(queryText);
    }
    
    private TestRecord createRecord(String id, String title) {
        TestRecord record = new TestRecord();
        record.setId(id);
        record.setTitle(title);
        return record;
    }
}