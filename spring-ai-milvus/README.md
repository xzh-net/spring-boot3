# Springboot集成Milvus和Embedding服务，实现向量化检索

本示例假设构建一个商品相似度搜索引擎，在向量数据库里先插入一些商品描述数据，然后输入商品名称，找到相似的商品

- 创建Collection
	- http://localhost:8080/milvus/createCollection
- 插入数据
	- http://localhost:8080/milvus/insertRecord
- 查询单挑记录
	- http://localhost:8080/milvus/getRecord?id=2
- 按向量检索相似度
	- http://localhost:8080/milvus/getRecord?id=2
