package ru.sberbank.uspelasticai.elastic.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.uspelasticai.elastic.model.Log;
import ru.sberbank.uspelasticai.elastic.repository.LogRepository;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;


@RestController
@RequestMapping("/rest/search")
public class SearchAndGetElasticData {

    @Autowired
    LogRepository logRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchTemplate;

    @Autowired
    private RestHighLevelClient client;


//    @Autowired
//    ElasticsearchOperations operations;

//    @Autowired
//    ElasticsearchClient elasticsearchClient;
//
//    @Autowired
//    RestClient restClient;

//    ElasticsearchRestTemplate elasticsearchRestTemplate;


//    @GetMapping(value = "/index/{index}")
    public List<Log> getLogDataByIndex(@PathVariable final String index) {
            Query query = new NativeSearchQueryBuilder()
                    .withQuery(matchQuery("index", index)) //"kibana_sample_data_logs"
//                    .withSearchAfter(new Object[]{sortAfterValue})
                    .withIds()
                    .build();
            SearchHits<Log> searchHits = elasticsearchTemplate.search(query, Log.class, IndexCoordinates.of(index));

//        System.out.println(searchHits.getTotalHits());

            return searchHits.get().map(SearchHit::getContent).collect(Collectors.toList());
        }

        @GetMapping(value = "/index/{index}")
        public void getScrollDataByIndex() throws IOException {
            final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
            SearchRequest searchRequest = new SearchRequest("kibana_sample_data_logs");
            searchRequest.scroll(scroll);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder
                    .query(matchQuery("index", "kibana_sample_data_logs"))
                    .searchAfter();
            searchRequest.source(searchSourceBuilder);

                SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//                searchResponse.getHits().getAt(lastIndex).getSortValues();
                String scrollId = searchResponse.getScrollId();
                org.elasticsearch.search.SearchHit[] searchHits = searchResponse.getHits().getHits();

                while (searchHits != null && searchHits.length > 0) {

                    SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                    scrollRequest.scroll(scroll);
                    searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
                    scrollId = searchResponse.getScrollId();
//                    searchResponse.getHits().getAt(lastIndex).getSortValues();
                    searchHits = searchResponse.getHits().getHits();
                    System.out.println(searchHits.toString());
                }


            ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(scrollId);
            ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            boolean succeeded = clearScrollResponse.isSucceeded();
            System.out.println(succeeded);

        }

        @GetMapping(value = "/index/last_id/{index}")
        private String getLastElastticSearchId(@PathVariable final String index) throws IOException {
//        RestHighLevelClient client = getElasticSearchClient();
            String SearchHit = null;
            SearchRequest searchRequest = new SearchRequest(index);
            SearchSourceBuilder b = new SearchSourceBuilder();
            b.query(QueryBuilders.matchAllQuery());
//            b.searchAfter(new Object[]{SearchHit});
            b.sort(new FieldSortBuilder("_id").order(SortOrder.DESC));
            b.from(0);
            b.size(1);
            searchRequest.source(b);

//            String SearchHit = null;
            try {

                SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
                org.elasticsearch.search.SearchHits hits = searchResponse.getHits();

                if (hits.getTotalHits().value > 0) {
                    org.elasticsearch.search.SearchHit[] searchHits = hits.getHits();
                    for (org.elasticsearch.search.SearchHit hit : searchHits) {
                        SearchHit = hit.getId();
//                        SearchHit = hit.getDocumentFields().get("timestamp").getValue();
//                    return hit.getId();
//                        searchResponse.getHits().getAt(Integer.parseInt(SearchHit)).getSortValues();
                        return SearchHit;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                client.close();
                return SearchHit;

            }
        }

    @GetMapping(value = "/index/lastdata/{index}")
    private void getLastElastticSearcData(@PathVariable final String index) throws IOException {
        List<String> messageLog = new ArrayList<>();
        Set<String> ips = new HashSet<>();
        Map<String, List<String>> logsWithIPAndMessage = new HashMap<>();
        String SearchHit = null;
        org.elasticsearch.search.SearchHits hits = null;
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder b = new SearchSourceBuilder();
        b.query(QueryBuilders.matchAllQuery());
//            b.searchAfter(new Object[]{SearchHit});
        b.sort(new FieldSortBuilder("_id").order(SortOrder.DESC));
        b.from(0);
        b.size(100);
        searchRequest.source(b);

        try {

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            hits = searchResponse.getHits();

            while (true) {
                List<Log> Logs = new ArrayList<>();
                TimeUnit.SECONDS.sleep(10);
                if (hits.getTotalHits().value > 0) {
                    org.elasticsearch.search.SearchHit[] searchHits = hits.getHits();
                    for (org.elasticsearch.search.SearchHit hit : searchHits) {
                        SearchHit = hit.getId();
//                        searchResponse.getHits().getAt(Integer.parseInt(SearchHit)).getSortValues();
                    }
                }
                b.searchAfter(new Object[]{SearchHit});
                searchRequest.source(b);
                searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
                hits = searchResponse.getHits();
                hits.forEach(hit -> {
                    try {
                        JsonNode hitJSON = new ObjectMapper().readTree(hit.toString());
//                        String sourceJSON = hitJSON.findValue("_source").toString();
//                        System.out.println("sourceJSON:" + sourceJSON);
                        Log log = new ObjectMapper().readValue(hitJSON.findValue("_source").toString(), Log.class);
//                        Log log = new ObjectMapper().readValue(hit.toString(), new TypeReference<Log>() {});
                        Logs.add(log);
                        messageLog.add(log.getMessage());
//                        System.out.println(log.getMessage());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                });
//                System.out.println(messageLog.toString());
                for (Log log : Logs) {
                    ips.add(log.getIp());
                }
                ips.forEach(i -> {
                    List<String> listMessages = new ArrayList<>();
                    listMessages = Logs.stream()
                            .filter(e -> e.getIp().equals(i))
                            .map(e -> e.getMessage())
                            .collect(Collectors.toList());

                    logsWithIPAndMessage.put(i, listMessages);
                });

                System.out.println(logsWithIPAndMessage);
                logsWithIPAndMessage.clear();
                messageLog.clear();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            client.close();
//            System.out.println(getHitsData(hits));
//            return getHitsData(hits);
        }
    }

//    private org.elasticsearch.search.SearchHits getHitsData(org.elasticsearch.search.SearchHits hits) {
//
//        return hits;
//    }


    @GetMapping(value = "/all")
    public List<Log> searchAll() {
        List<Log> logsList = new ArrayList<>();
        Iterable<Log> logs = logRepository.findAll();
        logs.forEach(logsList::add);
        return logsList;
    }

}
