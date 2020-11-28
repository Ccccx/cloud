package com.cjz.webmvc.elasticsearch;

import com.cjz.webmvc.utils.TreeFactory;
import com.google.gson.Gson;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-11-26 18:39
 */
@Slf4j
public class ElasticsearchTest {

	@Test
	public void t1() {
		ClientConfiguration clientConfiguration = ClientConfiguration.builder()
				.connectedTo("192.168.250.80:9200", "192.168.250.80:9201", "192.168.250.80:9202")
				.build();

		final RestHighLevelClient rest = RestClients.create(clientConfiguration).rest();

		final ElasticsearchRestTemplate elasticsearchRestTemplate = new ElasticsearchRestTemplate(rest);

		final IndexCoordinates busDeviceIndex = IndexCoordinates.of("bus_device_index");
		Query query = new NativeSearchQueryBuilder()
				.withQuery(matchAllQuery())
				.withPageable(PageRequest.of(1, 1))
				.addAggregation(terms("groupByType").field("type.keyword")
						.subAggregation(terms("groupByProvince").field("areaInfo.provinceName.keyword").size(30)
								.subAggregation(terms("groupByCity").field("areaInfo.cityName.keyword").size(50)
										.subAggregation(terms("groupByCompany").field("company.keyword").size(50)))
						))
				.build();

		final SearchHits<Object> search = elasticsearchRestTemplate.search(query, Object.class, busDeviceIndex);
		final Aggregations aggregations = search.getAggregations();
		final Map<String, Aggregation> typeMap = Objects.requireNonNull(aggregations).asMap();
		List<AreaInfo> areaInfos = new ArrayList<>();
		resolveAggregations("groupByType", typeMap, bucket -> {
			log.info("{} : {}", bucket.getKey(), bucket.getDocCount());
			areaInfos.add(new AreaInfo(bucket.getKey(), bucket.getDocCount(), null));
			final Map<String, Aggregation> province = bucket.getAggregations().asMap();
			resolveAggregations("groupByProvince", province, bucket1 -> {
				log.info("{} {} : {}", StringUtils.repeat(" ", 4), bucket1.getKey(), bucket1.getDocCount());
				areaInfos.add(new AreaInfo(bucket1.getKey(), bucket1.getDocCount(), bucket.getKey()));
				final Map<String, Aggregation> city = bucket1.getAggregations().asMap();
				resolveAggregations("groupByCity", city, bucket2 -> {
					log.info("{} {} : {}", StringUtils.repeat(" ", 8), bucket2.getKey(), bucket2.getDocCount());
					areaInfos.add(new AreaInfo(bucket2.getKey(), bucket2.getDocCount(), bucket1.getKey()));
					final Map<String, Aggregation> company = bucket2.getAggregations().asMap();
					resolveAggregations("groupByCompany", company, bucket3 -> {
						log.info("{} {} : {}", StringUtils.repeat(" ", 12), bucket3.getKey(), bucket3.getDocCount());
						areaInfos.add(new AreaInfo(bucket3.getKey(), bucket3.getDocCount(), bucket2.getKey()));
					});
				});
			});
		});
		final List<AreaInfo> tree = TreeFactory.build(areaInfos, AreaInfo::getId, AreaInfo::getPid, AreaInfo::setChildren);
		log.info("AreaInfo : {}", new Gson().toJson(tree));
	}

	public void resolveAggregations(String aggName, Map<String, Aggregation> aggregationMap, Consumer<Terms.Bucket> callback) {
		final ParsedStringTerms parsedStringTerms = (ParsedStringTerms) aggregationMap.get(aggName);
		for (Terms.Bucket bucket : parsedStringTerms.getBuckets()) {
			callback.accept(bucket);
		}
	}

	@Data
	public static class AreaInfo {
		private String id;
		private long count;
		private String pid;
		private List<AreaInfo> children;

		public AreaInfo(Object id, long count, Object pid) {
			this.id = (String) id;
			this.count = count;
			this.pid = (String) pid;
		}
	}

	@Test
	@SneakyThrows
	public void t2() {

	}

	@Data
	public static class GroupByArea {
		private Aggregation aggregation;
	}
}
