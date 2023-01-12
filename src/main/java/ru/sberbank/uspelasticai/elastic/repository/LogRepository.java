package ru.sberbank.uspelasticai.elastic.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.uspelasticai.elastic.model.Log;

import java.util.List;

@Repository
public interface LogRepository extends ElasticsearchRepository<Log, String> {
//    List<Log> findByName(String text);
//
//    List<Log> findBySalary(Long salary);
    List<Log> findByIndexContaining(String index);
}
