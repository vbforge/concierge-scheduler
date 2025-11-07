package com.vbforge.concierge.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration to enable JPA auditing
 * This activates @CreatedDate and @LastModifiedDate annotations
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

    // No additional configuration needed
    // The annotation alone enables auditing

}
