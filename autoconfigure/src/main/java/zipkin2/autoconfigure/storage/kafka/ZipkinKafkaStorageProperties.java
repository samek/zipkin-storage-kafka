/*
 * Copyright 2019 jeqo
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
package zipkin2.autoconfigure.storage.kafka;

import org.apache.kafka.common.record.CompressionType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import zipkin2.storage.kafka.KafkaStorage;

import java.io.Serializable;

@ConfigurationProperties("zipkin.storage.kafka")
public class ZipkinKafkaStorageProperties implements Serializable {
  private static final long serialVersionUID = 0L;

  private boolean ensureTopics = true;
  private String bootstrapServers = "localhost:9092";
  private String compressionType = CompressionType.NONE.name();

  private String spansTopic = "zipkin-spans_v1";
  private Integer spansTopicPartitions = 1;
  private Short spansTopicReplicationFactor = 1;
  private String tracesTopic = "zipkin-traces_v1";
  private Integer tracesTopicPartitions = 1;
  private Short tracesTopicReplicationFactor = 1;
  private String servicesTopic = "zipkin-services_v1";
  private Integer servicesTopicPartitions = 1;
  private Short servicesTopicReplicationFactor = 1;
  private String dependenciesTopic = "zipkin-dependencies_v1";
  private Integer dependenciesTopicPartitions = 1;
  private Short dependenciesTopicReplicationFactor = 1;

  private String storeDirectory = "/tmp/zipkin";

  KafkaStorage.Builder toBuilder() {
    return KafkaStorage.newBuilder()
        .ensureTopics(ensureTopics)
        .bootstrapServers(bootstrapServers)
        .compressionType(compressionType)
        .spansTopic(KafkaStorage.Topic.builder(spansTopic)
            .partitions(spansTopicPartitions)
            .replicationFactor(spansTopicReplicationFactor)
            .build())
        .tracesTopic(KafkaStorage.Topic.builder(tracesTopic)
            .partitions(tracesTopicPartitions)
            .replicationFactor(tracesTopicReplicationFactor)
            .build())
        .servicesTopic(KafkaStorage.Topic.builder(servicesTopic)
            .partitions(servicesTopicPartitions)
            .replicationFactor(servicesTopicReplicationFactor)
            .build())
        .dependenciesTopic(KafkaStorage.Topic.builder(dependenciesTopic)
            .partitions(dependenciesTopicPartitions)
            .replicationFactor(dependenciesTopicReplicationFactor)
            .build())
        .storeDirectory(storeDirectory);
  }

  public boolean isEnsureTopics() {
    return ensureTopics;
  }

  public void setEnsureTopics(boolean ensureTopics) {
    this.ensureTopics = ensureTopics;
  }

  public String getBootstrapServers() {
    return bootstrapServers;
  }

  public void setBootstrapServers(String bootstrapServers) {
    this.bootstrapServers = bootstrapServers;
  }

  public String getSpansTopic() {
    return spansTopic;
  }

  public void setSpansTopic(String spansTopic) {
    this.spansTopic = spansTopic;
  }

  public String getTracesTopic() {
    return tracesTopic;
  }

  public void setTracesTopic(String tracesTopic) {
    this.tracesTopic = tracesTopic;
  }

  public String getServicesTopic() {
    return servicesTopic;
  }

  public void setServicesTopic(String servicesTopic) {
    this.servicesTopic = servicesTopic;
  }

  public String getDependenciesTopic() {
    return dependenciesTopic;
  }

  public void setDependenciesTopic(String dependenciesTopic) {
    this.dependenciesTopic = dependenciesTopic;
  }

  public String getStoreDirectory() {
    return storeDirectory;
  }

  public void setStoreDirectory(String storeDirectory) {
    this.storeDirectory = storeDirectory;
  }

  public String getCompressionType() {
    return compressionType;
  }

  public void setCompressionType(String compressionType) {
    this.compressionType = compressionType;
  }

  public Integer getSpansTopicPartitions() {
    return spansTopicPartitions;
  }

  public void setSpansTopicPartitions(Integer spansTopicPartitions) {
    this.spansTopicPartitions = spansTopicPartitions;
  }

  public Short getSpansTopicReplicationFactor() {
    return spansTopicReplicationFactor;
  }

  public void setSpansTopicReplicationFactor(Short spansTopicReplicationFactor) {
    this.spansTopicReplicationFactor = spansTopicReplicationFactor;
  }

  public Integer getTracesTopicPartitions() {
    return tracesTopicPartitions;
  }

  public void setTracesTopicPartitions(Integer tracesTopicPartitions) {
    this.tracesTopicPartitions = tracesTopicPartitions;
  }

  public Short getTracesTopicReplicationFactor() {
    return tracesTopicReplicationFactor;
  }

  public void setTracesTopicReplicationFactor(Short tracesTopicReplicationFactor) {
    this.tracesTopicReplicationFactor = tracesTopicReplicationFactor;
  }

  public Integer getServicesTopicPartitions() {
    return servicesTopicPartitions;
  }

  public void setServicesTopicPartitions(Integer servicesTopicPartitions) {
    this.servicesTopicPartitions = servicesTopicPartitions;
  }

  public Short getServicesTopicReplicationFactor() {
    return servicesTopicReplicationFactor;
  }

  public void setServicesTopicReplicationFactor(Short servicesTopicReplicationFactor) {
    this.servicesTopicReplicationFactor = servicesTopicReplicationFactor;
  }

  public Integer getDependenciesTopicPartitions() {
    return dependenciesTopicPartitions;
  }

  public void setDependenciesTopicPartitions(Integer dependenciesTopicPartitions) {
    this.dependenciesTopicPartitions = dependenciesTopicPartitions;
  }

  public Short getDependenciesTopicReplicationFactor() {
    return dependenciesTopicReplicationFactor;
  }

  public void setDependenciesTopicReplicationFactor(Short dependenciesTopicReplicationFactor) {
    this.dependenciesTopicReplicationFactor = dependenciesTopicReplicationFactor;
  }
}
