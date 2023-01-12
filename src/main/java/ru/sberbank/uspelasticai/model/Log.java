package ru.sberbank.uspelasticai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import static org.springframework.data.elasticsearch.annotations.FieldType.*;

@Document(indexName = "kibana_sample_data_logs")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Log {
    @Id
    private String _id;
    @Field (type = Text)
    private String index;
    @Field (type = Date)
    private String timestamp;
    @Field (type = Ip)
    private String ip;
    @Field (type = Text)
    private String message;

    public Log() {
    }

    public Log(String id) {
        this._id = id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id='" + _id + '\'' +
                ", index='" + index + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", ip='" + ip + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
