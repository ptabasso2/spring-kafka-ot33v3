package com.datadog.pej.kafka.consumer;

import java.util.concurrent.CountDownLatch;

import com.datadog.pej.kafka.Quote;
//import datadog.trace.api.DDTags;
import datadog.trace.api.DDTags;
import io.opentracing.*;
import io.opentracing.contrib.kafka.TracingKafkaUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.client.RestTemplate;


@Slf4j
public class Receiver {

  private CountDownLatch latch = new CountDownLatch(1);

  @Autowired
  RestTemplate restTemplate;

  @Autowired
  Tracer tracer;

  public CountDownLatch getLatch() {
    return latch;
  }

  @KafkaListener(topics = "users")
  public void receive(String payload, ConsumerRecord<?,?> cr) {

    log.info("received payload='{}'", payload);

    SpanContext spanContext = TracingKafkaUtils.extractSpanContext(cr.headers(), tracer);

    ScopeManager sm = tracer.scopeManager();
    Tracer.SpanBuilder sb0 = tracer.buildSpan("Quote").asChildOf(spanContext);
    sb0.withTag(DDTags.RESOURCE_NAME, "GET /api/random").withTag(DDTags.SPAN_TYPE, "web").withTag(DDTags.SERVICE_NAME, "Quote");

    Span span = sb0.start();

    try(Scope scope = sm.activate(span)) {
      Quote quote = restTemplate.getForObject("https://gturnquist-quoters.cfapps.io/api/random", Quote.class);
      log.info(quote.toString());
      //latch.countDown();
    } finally {
      span.finish();
    }



  Tracer.SpanBuilder sb1 = tracer.buildSpan("Google").asChildOf(spanContext);
  sb1.withTag(DDTags.RESOURCE_NAME, "GET /").withTag(DDTags.SPAN_TYPE, "web").withTag(DDTags.SERVICE_NAME, "Google");
  Span span1 = sb1.start();

  try(Scope scope = sm.activate(span1)) {
     String result = restTemplate.getForObject("https://www.google.fr", String.class);
     log.info(result);
     latch.countDown();
  } finally {
     span1.finish();
  }


  }
}
