package org.konnecticator.server;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.konnecticator.server.config.ConfigurationProvider;
import org.konnecticator.server.config.ServerConfiguration;
import org.konnecticator.server.connect.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class WebController {

    @Autowired
    private StreamsBuilderFactoryBean factoryBean;

    @Autowired
    ConfigurationProvider configurationProvider;

    private final Logger logger = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private ServerConfiguration serverConfiguration;

    @Autowired
    RestClient restClient;

    @GetMapping("/offsets/")
    public List<KeyValue<String, String>> getOffsetsStateStore() {

        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
        ReadOnlyKeyValueStore<String, String> store = kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(serverConfiguration.getOffsetsStateStoreName(), QueryableStoreTypes.keyValueStore())
        );

        List<KeyValue<String, String>> resultList = new ArrayList<>();
        store.all().forEachRemaining(resultList::add);

        return resultList;
    }

    @GetMapping("/configs/")
    public List<KeyValue<String, String>> getConfigsStateStore() {

        ServerConfiguration config = configurationProvider.getServerConfiguration();

        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
        ReadOnlyKeyValueStore<String, String> store = kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(config.getConfigsStateStoreName(), QueryableStoreTypes.keyValueStore())
        );

        List<KeyValue<String, String>> resultList = new ArrayList<>();
        store.all().forEachRemaining(resultList::add);

        return resultList;
    }

    @GetMapping("/status/")
    public List<KeyValue<String, String>> getStatusStateStore() {

        ServerConfiguration config = configurationProvider.getServerConfiguration();

        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
        ReadOnlyKeyValueStore<String, String> store = kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(config.getStatusStateStoreName(), QueryableStoreTypes.keyValueStore())
        );

        List<KeyValue<String, String>> resultList = new ArrayList<>();
        store.all().forEachRemaining(resultList::add);

        return resultList;
    }

    @PostMapping("/connector/restart/{name}")
    public boolean restart(@PathVariable String name) {

        //TODO: get proper task id
        restClient.restart(name, "0");

        return true;
    }
}