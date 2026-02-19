package org.example.ultimatecalendarmaven.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class BusinessTimeService {

  private final ZoneId businessZone;

  public BusinessTimeService(
      @Value("${business.timezone}") String timezone) {
    this.businessZone = ZoneId.of(timezone);
  }

  public Instant toInstant(LocalDateTime localDateTime) {
    return localDateTime
        .atZone(businessZone)
        .toInstant();
  }

  public LocalDateTime toLocal(Instant instant) {
    return instant
        .atZone(businessZone)
        .toLocalDateTime();
  }

  public ZoneId getZone() {
    return businessZone;
  }
}