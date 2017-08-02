package edu.wlu.graffiti.elasticsearch;

import java.net.InetAddress;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Elasticsearch configuration/set up
 * @author cooperbaird
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "edu.wlu.graffiti.elasticsearch")
public class EsConfig {

	@Value("${elasticsearch.host}")
	private String EsHost;

	@Value("${elasticsearch.port}")
	private int EsPort;

	@Value("${elasticsearch.clustername}")
	private String EsClusterName;

	@Bean
	public Client client() throws Exception {
		Settings esSettings = Settings.builder().put("cluster.name", EsClusterName).build();
		TransportClient tc = new PreBuiltTransportClient(esSettings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(EsHost), EsPort));
		return tc;
	}
	
	@Bean
    public ElasticsearchOperations elasticsearchTemplate() throws Exception {
        return new ElasticsearchTemplate(client());
    }

}
