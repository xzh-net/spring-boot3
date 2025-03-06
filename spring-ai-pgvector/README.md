# PostgreSQL 数据库向量化的核心：pgvector 

pgvector是一款开源的向量搜索引擎，除了具备所有Postgres数据库的特性外，最主要的特点是能在Postgres数据库存储和检索向量数据，支持向量的精确检索和模糊检索。向量格式除了传统embedding模型的单精度浮点数外，还支持半精度浮点数，二元向量或者稀疏向量。

```bash
docker build -f postgres16-age -t postgres16-age . 
```