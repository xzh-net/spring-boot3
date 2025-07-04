package net.xzh.milvus.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.milvus.pool.MilvusClientV2Pool;
import io.milvus.pool.PoolConfig;
import io.milvus.v2.client.ConnectConfig;

@Configuration
public class MilvusConfig {

	@Value("${milvus.uri}")
	private String uri;

	@Value("${milvus.token}")
	private String token;

	@Value("${milvus.pool.maxIdle:10}")
	private int maxIdle;

	@Value("${milvus.pool.maxTotal:20}")
	private int maxTotal;

	@Bean(destroyMethod = "close")
	public MilvusClientV2Pool milvusPool() throws ClassNotFoundException, NoSuchMethodException {
			
		ConnectConfig connectConfig = ConnectConfig.builder()
                .uri(uri)
                .token(token)
                .build();

        PoolConfig poolConfig = PoolConfig.builder()
                .maxIdlePerKey(maxIdle)
                .maxTotalPerKey(maxTotal)
                .maxBlockWaitDuration(Duration.ofSeconds(5))
                .minEvictableIdleDuration(Duration.ofSeconds(10))
                .build();
        
		return new MilvusClientV2Pool(poolConfig, connectConfig);
	}
}