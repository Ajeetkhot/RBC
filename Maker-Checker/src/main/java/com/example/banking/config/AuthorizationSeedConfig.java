package com.example.banking.config;

import com.example.rbc.RbcRule;
import com.example.rbc.RbcRuleRepository;
import com.example.rbc.RuleCondition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class AuthorizationSeedConfig {
    @Bean
    CommandLineRunner seedAuthorizationRules(RbcRuleRepository repository, ObjectMapper objectMapper) {
        return args -> {
            if (repository.count() > 0) return;
            save(repository, objectMapper, 1L, "INITIATE_TRANSFER", List.of(
                    new RuleCondition("amount", RuleCondition.Operator.LESS_THAN_OR_EQUAL, new BigDecimal("5000"))));
            save(repository, objectMapper, 2L, "INITIATE_TRANSFER", List.of(
                    new RuleCondition("amount", RuleCondition.Operator.LESS_THAN_OR_EQUAL, new BigDecimal("10000"))));
            save(repository, objectMapper, 2L, "CREATE_BENEFICIARY", List.of());
            save(repository, objectMapper, 3L, "INITIATE_TRANSFER", List.of(
                    new RuleCondition("amount", RuleCondition.Operator.LESS_THAN_OR_EQUAL, new BigDecimal("2000"))));
            save(repository, objectMapper, 201L, "APPROVE_TRANSFER", List.of(
                    new RuleCondition("makerId", RuleCondition.Operator.EQUALS, 1L)));
            save(repository, objectMapper, 202L, "APPROVE_TRANSFER", List.of(
                    new RuleCondition("amount", RuleCondition.Operator.LESS_THAN_OR_EQUAL, new BigDecimal("10000"))));
            save(repository, objectMapper, 203L, "APPROVE_TRANSFER", List.of(
                    new RuleCondition("makerId", RuleCondition.Operator.IN, List.of(1L, 2L))));
        };
    }

    private void save(RbcRuleRepository repository, ObjectMapper objectMapper, Long userId,
                      String action, List<RuleCondition> conditions) throws JsonProcessingException {
        RbcRule rule = new RbcRule("MAKER_CHECKER", userId, action, "BANKING_RESOURCE", "ALLOW",
                objectMapper.writeValueAsString(conditions));
        repository.save(rule);
    }
}