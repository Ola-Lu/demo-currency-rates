package com.example.currency.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyFetchService {
  private static final Logger logger = LoggerFactory.getLogger(CurrencyFetchService.class);

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  @Value("${app.schedule.api-url}")
  private String apiUrl;
  @Value("${app.schedule.currency-from}")
  private String currencyFrom;
  @Value("${app.schedule.currency-to}")
  private String currencyTo;
  @Value("${spring.kafka.template.default-topic}")
  private String defaultTopic;

  private String apiFullUrl;

  @PostConstruct
  public void init() {
    this.apiFullUrl = apiUrl + "base=" + currencyFrom + "&" + "symbols=" + currencyTo;
  }


  public CurrencyFetchService(KafkaTemplate<String, String> kafkaTemplate,
      RestTemplateBuilder restTemplateBuilder,
      ObjectMapper objectMapper) {
    this.kafkaTemplate = kafkaTemplate;
    this.restTemplate = restTemplateBuilder.build();
    this.objectMapper = objectMapper;
  }

  @Scheduled(fixedRateString = "${app.schedule.fetch-rate}")
  public void fetchAndSend() {
    try {
      Map<?, ?> apiResponse = restTemplate.getForObject(apiFullUrl, Map.class);
      logger.debug("Fetched currency rates: {}", apiResponse);
      if (apiResponse != null) {
        String json = objectMapper.writeValueAsString(apiResponse);
        kafkaTemplate.send(defaultTopic, json);
        logger.info("Sent currency data to Kafka");
      } else {
        logger.info("Received null response");
      }
    } catch (Exception e) {
      logger.error("Failed to fetch/send currency rates", e);
    }
  }
}
