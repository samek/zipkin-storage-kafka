/*
 * Copyright 2019 [name of copyright owner]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.storage.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import zipkin2.storage.SpanConsumer;
import zipkin2.storage.SpanStore;
import zipkin2.storage.StorageComponent;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class KafkaStorage extends StorageComponent {

    public static class Builder extends StorageComponent.Builder {
        String bootstrapServers = "localhost:29092";
        String applicationId = "zipkin-server_v2";
        String traceStoreName = "zipkin-traces-store";
        String serviceSpanStoreName = "zipkin-service-operations-store";
        String dependencyStoreName = "zipkin-dependencies-store";

        @Override
        public StorageComponent.Builder strictTraceId(boolean strictTraceId) {
            if (!strictTraceId) throw new IllegalArgumentException("unstrict trace ID not supported");
            return this;
        }

        @Override
        public StorageComponent.Builder searchEnabled(boolean searchEnabled) {
            if (searchEnabled) throw new IllegalArgumentException("search not supported");
            return this;
        }

        @Override
        public Builder autocompleteKeys(List<String> keys) {
            if (keys == null) throw new NullPointerException("keys == null");
            if (!keys.isEmpty()) throw new IllegalArgumentException("autocomplete not supported");
            return this;
        }

        public Builder bootstrapServers(String bootstrapServers) {
            if (bootstrapServers == null) throw new NullPointerException("bootstrapServers == null");
            this.bootstrapServers = bootstrapServers;
            return this;
        }

        public Builder applicationId(String applicationId) {
            if (applicationId == null) throw new NullPointerException("applicationId == null");
            this.applicationId = applicationId;
            return this;
        }

        public Builder tracesStoreName(String tracesStoreName) {
            if (tracesStoreName == null) throw new NullPointerException("traceStoreName == null");
            this.traceStoreName = tracesStoreName;
            return this;
        }

        public Builder serviceOperationsStoreName(String serviceOperationsStoreName) {
            if (serviceOperationsStoreName == null)
                throw new NullPointerException("serviceSpanStoreName == null");
            this.serviceSpanStoreName = serviceOperationsStoreName;
            return this;
        }

        public Builder dependenciesStoreName(String dependenciesStoreName) {
            if (dependenciesStoreName == null) throw new NullPointerException("dependencyStoreName == null");
            this.dependencyStoreName = dependenciesStoreName;
            return this;
        }

        @Override
        public StorageComponent build() {
            return new KafkaStorage(this);
        }

        Builder() {
        }
    }

    final Producer<String, byte[]> producer;
    final KafkaStreams kafkaStreams;
    final KeyValueBytesStoreSupplier traceStoreSupplier;
    final KeyValueBytesStoreSupplier serviceSpanStoreSupplier;
    final KeyValueBytesStoreSupplier dependencyStoreSupplier;

    KafkaStorage(Builder builder) {
        final Properties producerConfigs = new Properties();
        producerConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, builder.bootstrapServers);
        producerConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class); //TODO validate format
        producerConfigs.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        //TODO add a way to introduce custom properties
        this.producer = new KafkaProducer<>(producerConfigs);

        this.traceStoreSupplier = Stores.persistentKeyValueStore(builder.traceStoreName);
        this.serviceSpanStoreSupplier = Stores.persistentKeyValueStore(builder.serviceSpanStoreName);
        this.dependencyStoreSupplier = Stores.persistentKeyValueStore(builder.dependencyStoreName);

        final Properties streamsConfig = new Properties();
        streamsConfig.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, builder.bootstrapServers);
        streamsConfig.put(StreamsConfig.APPLICATION_ID_CONFIG, builder.applicationId);
        streamsConfig.put(StreamsConfig.EXACTLY_ONCE, true);
        streamsConfig.put(StreamsConfig.STATE_DIR_CONFIG, "target/kafka-streams" + Instant.now().getEpochSecond());
        this.kafkaStreams = new StreamsSupplier(
                streamsConfig, traceStoreSupplier, serviceSpanStoreSupplier, dependencyStoreSupplier)
                .get();
        kafkaStreams.start();
    }

    @Override
    public SpanStore spanStore() {
        return new KafkaSpanStore(this);
    }

    @Override
    public SpanConsumer spanConsumer() {
        return new KafkaSpanConsumer(this);
    }

    @Override
    public void close() {
        producer.close(1, TimeUnit.SECONDS);
        kafkaStreams.close(Duration.ofSeconds(1));
    }
}
