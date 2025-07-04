# milvus-sdk-java集成Embedding服务，实现向量化检索

Milvus是一款开源向量数据库，专为支持大规模向量检索而设计，特别适用于大模型领域中的应用。通过调用阿里云百炼大模型服务平台所提供的Embedding服务，实现数据的向量化存储与高效检索。

本示例构建一个商品相似度搜索引擎，在向量数据库里先插入一些商品描述数据，然后输入商品名称，找到相似的商品



测试地址

- 创建Collection：http://localhost:8080/milvus/createCollection
- 插入数据：http://localhost:8080/milvus/insertRecords
- 查询单条记录：http://localhost:8080/milvus/getRecord?id=1
- 按向量检索相似度：http://localhost:8080/milvus/queryVector


参考资料

- https://milvus.io/api-reference/java/v2.5.x/About.md

