package com.datadog.pej.kafka;

import com.datadog.pej.kafka.consumer.Receiver;
import com.datadog.pej.kafka.producer.Sender;
//import datadog.opentracing.DDTracer;
//import io.jaegertracing.Configuration;
//import io.jaegertracing.internal.JaegerTracer;

import datadog.opentracing.DDTracer;
import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Tracer;

import io.opentracing.util.GlobalTracer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;


@Slf4j
@SpringBootApplication
public class SpringKafkaApplication {

  @Autowired
  private Receiver receiver;

  @Autowired
  private Sender sender;

  public static void main(String[] args) {
    SpringApplication.run(SpringKafkaApplication.class, args);
  }

  /* To be used with Jaeger and manual instrumentation */

  @Bean
  @ConditionalOnProperty(
          value="tracer.type",
          havingValue = "jaeger")
  public Tracer jaegerTracer(@Value("ServiceKafka") String service) {
    Configuration.SamplerConfiguration samplerConfig = Configuration.SamplerConfiguration.fromEnv().withType("const").withParam(1);
    Configuration.ReporterConfiguration reporterConfig = Configuration.ReporterConfiguration.fromEnv().withLogSpans(true);
    Configuration config = new Configuration(service).withSampler(samplerConfig).withReporter(reporterConfig);
    return config.getTracer();
  }



  /* To be used without dd-trace-ot.jar and manual instrumentation
  */

  @Primary
  @Bean
  @ConditionalOnProperty(
          value="tracer.type",
          havingValue = "datadog",
          matchIfMissing = true)
  public Tracer initTracer(@Value("ServiceKafka") String service){
    Tracer tracer = DDTracer.builder().build();
    return tracer;
  }


  @Bean
  public RestTemplate restTemplate(){
    return new RestTemplateBuilder().build();
  }


  @Bean
  public CommandLineRunner run() {
    return args -> {

      //sender.send("Un message depuis command line runner");
      //receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);

      log.info("\ntest");
    };
  }

}
