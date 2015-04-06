package com.microWorkflow.kolektor;

/**
* Created by dam on 3/26/15.
*/

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.collectd.api.*;

import java.util.List;
import java.util.Properties;

public class KafkaPlugin implements
        CollectdConfigInterface,
        CollectdInitInterface,
        CollectdShutdownInterface,
        CollectdWriteInterface {

    private static ObjectMapper mapper = new ObjectMapper();

    private String zookeeper_uri;
    private int connection_timeout;
    private String group_id;
    private String topic;
    private boolean async;
    private KafkaProducer<String, String> producer;

    public KafkaPlugin() {
        Collectd.registerConfig("KafkaPlugin", this);
        Collectd.registerInit("KafkaPlugin", this);
        Collectd.registerWrite("KafkaPlugin", this);
        Collectd.registerShutdown("KafkaPlugin", this);
    }

    public KafkaPlugin(String zkUri, String groupId, String topic, boolean async) {
        this.zookeeper_uri = zkUri;
        this.group_id = groupId;
        this.connection_timeout = 1000000;
        this.topic = topic;
        this.async = async;
    }

    @Override
    public int config(OConfigItem oConfigItem) {
        Collectd.logInfo("config called");
        List<OConfigItem> children = oConfigItem.getChildren();
        try {
            for (OConfigItem option: children) {
                String key = option.getKey();
                if ("uri".equalsIgnoreCase(key)) {
                    this.zookeeper_uri = option.getValues().get(0).getString();
                    Collectd.logInfo("uri: " + zookeeper_uri);
                }
                if ("connectiontimeout".equalsIgnoreCase(key)) {
                    this.connection_timeout = option.getValues().get(0).getNumber().intValue();
                    Collectd.logInfo("timeout: " + connection_timeout);
                }
                if ("groupid".equalsIgnoreCase(key)) {
                    this.group_id = option.getValues().get(0).getString();
                    Collectd.logInfo("groupid: " + group_id);
                }
                if ("topic".equalsIgnoreCase(key)) {
                    this.topic = option.getValues().get(0).getString();
                    Collectd.logInfo("topic: " + topic);
                }
                if ("async".equalsIgnoreCase(key)) {
                    this.async = option.getValues().get(0).getBoolean();
                    Collectd.logInfo("async: " + (async ? "True" : "False"));
                }
            }
        } catch (Exception ex) {
            Collectd.logError(String.format("Exception %s", ex.getMessage()));
            return -1;
        }
        return 0;
    }

    @Override
    public int init() {
        Collectd.logInfo("init called");
        try {
            connect();
        } catch (Exception ex) {
            Collectd.logError(String.format("Exception %s", ex.getMessage()));
            return -1;
        }
        return 0;
    }

    public void connect() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.211.55.3:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "none");
        producer = new KafkaProducer<String, String>(props);
    }

    @Override
    public int shutdown() {
        Collectd.logInfo("shutdown called");
        closeProducer();
        return 0;
    }

    public void closeProducer() {
        if (producer != null)
            producer.close();
    }

    @Override
    public int write(ValueList valueList) {
        DataSet dataSet = valueList.getDataSet();
        List<DataSource> sources = dataSet.getDataSources();
        List<Number> values = valueList.getValues();
        Measurement measurement = Measurement.build(dataSet.getType(), sources, values);
        sendMeasurement(measurement);
        return 0;
    }

    public void sendMeasurement(Measurement m) {
        try {
            String json = mapper.writeValueAsString(m);
            ProducerRecord<String, String> data = new ProducerRecord<String, String>(topic, json);
            assert data != null;
            assert producer != null;
            producer.send(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}

