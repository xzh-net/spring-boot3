# 基于AI的大模型和检索增强生成（RAG, Retrieval-Augmented Generation）技术，利用海量的知识数据构建本地知识库

使用Spring AI、Milvus 和 Spring AI Alibaba 开源框架，搭建并验证一个基于AI大模型的本地知识库系统。将本地文档切片，使用Embedding向量化后保存到向量数据库并验证其在实际应用中的效果。

- 导入本地PDF
	- http://localhost:8080/milvus2/insertDocuments
- RAG检索测试
	- http://localhost:8080/milvus2/ragJsonText

