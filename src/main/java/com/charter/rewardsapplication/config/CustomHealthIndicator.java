package com.charter.rewardsapplication.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        boolean serviceHealthy = true;

        return Health.up()
                .withDetail("service", "Rewards Application is running")
                .build();

    }
}
