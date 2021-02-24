package com.cjz.webmvc.elasticsearch;

import com.cjz.webmvc.utils.TreeFactory;
import com.google.gson.Gson;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.AggregatorFactories.Builder;
import org.elasticsearch.search.aggregations.bucket.composite.ParsedComposite;
import org.elasticsearch.search.aggregations.bucket.composite.ParsedComposite.ParsedBucket;
import org.elasticsearch.search.aggregations.bucket.composite.TermsValuesSourceBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.metrics.ParsedCardinality;
import org.elasticsearch.search.aggregations.metrics.ParsedValueCount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.search.aggregations.AggregationBuilders.*;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-11-26 18:39
 */
@Slf4j
public class ElasticsearchTest {

    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @BeforeEach
    @SneakyThrows
    public void before() {
        ClassPathResource resource = new ClassPathResource("es01.crt");

        java.security.cert.CertificateFactory factory =
                java.security.cert.CertificateFactory.getInstance("X.509");
        java.security.cert.Certificate trustedCa = null;
        try (java.io.InputStream is = resource.getInputStream()) {
            trustedCa = factory.generateCertificate(is);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        java.security.KeyStore trustStore = java.security.KeyStore.getInstance("pkcs12");
        trustStore.load(null, null);
        trustStore.setCertificateEntry("ca", trustedCa);
        SSLContextBuilder sslContextBuilder = SSLContexts.custom()
                .loadTrustMaterial(trustStore, null);
        final javax.net.ssl.SSLContext sslContext = sslContextBuilder.build();

        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("es01:9200").usingSsl(sslContext)
                .withBasicAuth("elastic", "tmkj@zgb123")
                .build();
        final RestHighLevelClient rest = RestClients.create(clientConfiguration).rest();
        elasticsearchRestTemplate = new ElasticsearchRestTemplate(rest);
    }

    @Test
    @SneakyThrows
    public void t1() {
        final IndexCoordinates busDeviceIndex = IndexCoordinates.of("bus_device_index");
        Query query = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .withPageable(PageRequest.of(1, 1))
                .addAggregation(terms("groupByType").field("type.keyword")
                        .subAggregation(terms("groupByProvince").field("areaInfo.provinceName.keyword").size(30)
                                .subAggregation(terms("groupByCity").field("areaInfo.cityName.keyword").size(50)
                                        .subAggregation(terms("groupByCompany").field("company.keyword").size(50)))))
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

    @Test
    public void t2() {
        final IndexCoordinates busDeviceIndex = IndexCoordinates.of("bus_device_index");
        Query query = new NativeSearchQueryBuilder()
                .addAggregation(terms("cityDeviceTop").field("areaInfo.cityCode").size(10))
                .build();
        final SearchHits<DataInfo> search = elasticsearchRestTemplate.search(query, DataInfo.class, busDeviceIndex);
        final Aggregations aggregations = search.getAggregations();
        final Map<String, Aggregation> typeMap = Objects.requireNonNull(aggregations).asMap();
        resolveAggregations("cityDeviceTop", typeMap, bucket -> log.info("{} : {}", bucket.getKey(), bucket.getDocCount()));
    }

    @Test
    public void t3() {
        final IndexCoordinates busDeviceIndex = IndexCoordinates.of("bus_device_index");
        Query query = new NativeSearchQueryBuilder()
                .withQuery(boolQuery().filter(termQuery("company.keyword", "南通市NDBKVq3f测试有限公司")))
                .addAggregation(AggregationBuilders.count("countByCompany").field("company.keyword"))
                .build();
        final SearchHits<DataInfo> search = elasticsearchRestTemplate.search(query, DataInfo.class, busDeviceIndex);
        final Aggregations aggregations = search.getAggregations();
        final Map<String, Aggregation> typeMap = Objects.requireNonNull(aggregations).asMap();
        ParsedValueCount valueCount = (ParsedValueCount) typeMap.get("countByCompany");
        log.info("{} ", valueCount.getValue());
    }


    @Test
    public void t4() {
        final IndexCoordinates busDeviceIndex = IndexCoordinates.of("bus_device_index");
        Query query = new NativeSearchQueryBuilder()
                .withPageable(PageRequest.of(1, 1))
                .addAggregation(AggregationBuilders.cardinality("countBySubType").field("company.keyword"))
                .build();
        final SearchHits<DataInfo> search = elasticsearchRestTemplate.search(query, DataInfo.class, busDeviceIndex);
        final Aggregations aggregations = search.getAggregations();
        final Map<String, Aggregation> typeMap = Objects.requireNonNull(aggregations).asMap();
        ParsedCardinality valueCount = (ParsedCardinality) typeMap.get("countBySubType");
        log.info("{} ", valueCount.getValue());
    }

    @Test
    void t5() {
        final IndexCoordinates busDeviceIndex = IndexCoordinates.of("bus_device_index");
        final StringQuery sql = new StringQuery("SELECT areaInfo.provinceName as provinceName, count(1) as total FROM \"bus_device_index\" group  by areaInfo.provinceName order by total desc");
        final SearchHits<DataInfo> search = elasticsearchRestTemplate.search(sql, DataInfo.class, busDeviceIndex);
        log.info("---");
    }

    @Test
    void t6() {
        final IndexCoordinates busDeviceIndex = IndexCoordinates.of("bus_device_index");
        Query query = new NativeSearchQueryBuilder()
                .withQuery(boolQuery().filter(termQuery("type.keyword", "BUS")).filter(termQuery("areaInfo.provinceName.keyword", "黑龙江省")))
                .addAggregation(terms("groupBy").field("areaInfo.cityName.keyword")).build();
        final SearchHits<DataInfo> search = elasticsearchRestTemplate.search(query, DataInfo.class, busDeviceIndex);
        final Aggregations aggregations = search.getAggregations();
    }

    @Test
    void t7() {
        final IndexCoordinates busDeviceIndex = IndexCoordinates.of("bus_device_index");
        Query query = new NativeSearchQueryBuilder()
                .withPageable(PageRequest.of(1, 1))
                .withQuery(boolQuery().filter(termQuery("company.keyword", "测试17号公司")))
                .addAggregation(composite("groupBy", Arrays.asList(
                        new TermsValuesSourceBuilder("groupByProvinceName").field("areaInfo.provinceName.keyword"),
                        new TermsValuesSourceBuilder("groupByCityName").field("areaInfo.cityName.keyword"),
                        new TermsValuesSourceBuilder("groupByType").field("type.keyword"),
                        new TermsValuesSourceBuilder("groupBySubType").field("subType.keyword"),
                        new TermsValuesSourceBuilder("groupByVersionCode").field("versionCode.keyword")
                )).size(1000))
                .build();
        final SearchHits<DataInfo> search = elasticsearchRestTemplate.search(query, DataInfo.class, busDeviceIndex);
        final Aggregations aggregations = search.getAggregations();
        log.info("----");
        final List<Aggregation> list = aggregations.asList();
        final ParsedComposite parsedComposite = (ParsedComposite) list.get(0);
        final List<ParsedBucket> buckets = parsedComposite.getBuckets();
        Gson gson = new Gson();
        buckets.forEach(v -> {
            Map<String, Object> map = v.getKey();
            log.info(" {}  {}", gson.toJson(map), v.getDocCount());
        });
    }

    @Test
    public void t8() {
        final IndexCoordinates busDeviceIndex = IndexCoordinates.of("bus_device_index");
        Query query = new NativeSearchQueryBuilder()
                .addAggregation(terms("company").field("company.keyword").size(100)
                        .subAggregations(new Builder()
                                .addAggregator(cardinality("deviceCount").field("id.keyword"))
                                .addAggregator(cardinality("subType").field("subType.keyword"))
                                .addAggregator(cardinality("provinceCount").field("areaInfo.provinceName.keyword"))
                                .addAggregator(cardinality("cityCount").field("areaInfo.cityName.keyword"))
                                .addAggregator(cardinality("versionCodeCount").field("versionCode.keyword"))
                        ))
                .build();
        final SearchHits<DataInfo> search = elasticsearchRestTemplate.search(query, DataInfo.class, busDeviceIndex);
        final Aggregations aggregations = search.getAggregations();
        log.info("----");
        final List<Aggregation> list = aggregations.asList();
        final Aggregation aggregation = list.get(0);
        final ParsedStringTerms stringTerms = (ParsedStringTerms) aggregation;
        final List<? extends Bucket> buckets = stringTerms.getBuckets();
        for (Bucket bucket : buckets) {
            final ParsedStringTerms.ParsedBucket parsedBucket = (ParsedStringTerms.ParsedBucket) bucket;
            final Aggregations parsedBucketAggregations = parsedBucket.getAggregations();
            final Map<String, Aggregation> stringAggregationMap = parsedBucketAggregations.asMap();
            log.info("company: {} versionCodeCount: {} provinceCount: {} deviceCount: {} cityCount: {} subType: {}",
                    parsedBucket.getKeyAsString(),
                    ((ParsedCardinality) stringAggregationMap.get("versionCodeCount")).getValue(),
                    ((ParsedCardinality) stringAggregationMap.get("provinceCount")).getValue(),
                    ((ParsedCardinality) stringAggregationMap.get("deviceCount")).getValue(),
                    ((ParsedCardinality) stringAggregationMap.get("cityCount")).getValue(),
                    ((ParsedCardinality) stringAggregationMap.get("subType")).getValue());
            return;
        }
    }


    public void resolveAggregations(String aggName, Map<String, Aggregation> aggregationMap, Consumer<Terms.Bucket> callback) {
        final ParsedTerms parsedTerms = (ParsedTerms) aggregationMap.get(aggName);
        for (Terms.Bucket bucket : parsedTerms.getBuckets()) {
            callback.accept(bucket);
        }
    }

    @Test
    @SneakyThrows
    void t9() {
        final IndexCoordinates logstashIndex = IndexCoordinates.of("bus_device*");
        elasticsearchRestTemplate.indexOps(logstashIndex).delete();
        TimeUnit.SECONDS.sleep(5);
    }

    @Test
    void t10() throws InterruptedException {
        final IndexCoordinates logstashIndex = IndexCoordinates.of("logstash-log4j2-*");
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date week = cal.getTime();
        final String date = DateFormatUtils.format(week, "yyyy-MM-dd");
        final NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(rangeQuery("@timestamp").lt(date)).build();
        elasticsearchRestTemplate.delete(query, DataInfo.class, logstashIndex);
        log.info("\n{}", query.getQuery().toString());
        elasticsearchRestTemplate.indexOps(logstashIndex).refresh();
        TimeUnit.SECONDS.sleep(20);
    }

    @Test
    void t11() {
        final IndexCoordinates logstashIndex = IndexCoordinates.of("logstash-log4j2-*");
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date week = cal.getTime();
        final String date = DateFormatUtils.format(week, "yyyy-MM-dd");
        final NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withPageable(PageRequest.of(1, 1))
                .withQuery(rangeQuery("@timestamp").lte(date))
                .addAggregation(terms("groupByIndex").field("_index").size(10))
                .build();
        log.info("\n{}", query.getQuery().toString());
        final SearchHits<DataInfo> search = elasticsearchRestTemplate.search(query, DataInfo.class, logstashIndex);
        final Aggregations aggregations = search.getAggregations();
        final List<Aggregation> list = aggregations.asList();
        final Aggregation aggregation = list.get(0);
        final ParsedStringTerms stringTerms = (ParsedStringTerms) aggregation;
        final List<? extends Bucket> buckets = stringTerms.getBuckets();
        for (Bucket bucket : buckets) {
            log.info("{} : {}", bucket.getKey(), bucket.getDocCount());
        }
    }

    @Test
    void t12() {
        final IndexCoordinates logstashIndex = IndexCoordinates.of("device_index");
        final NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withPageable(PageRequest.of(1, 1))
                .withQuery(boolQuery().filter(termQuery("type.keyword", "TEST")))
                .addAggregation(terms("cityDeviceTop").field("areaInfo.cityName.keyword").size(10))
                .build();
        query.setTrackTotalHits(true);
        log.info("\n{}", query.getQuery().toString());
        final SearchHits<DataInfo> search = elasticsearchRestTemplate.search(query, DataInfo.class, logstashIndex);
        log.info("total: {}", search.getTotalHits());
    }

    @Data
    public static class DataInfo {

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


    @Data
    public static class GroupByArea {
        private Aggregation aggregation;
    }
}
