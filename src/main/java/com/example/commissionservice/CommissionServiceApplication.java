package com.example.commissionservice;

import com.example.commissionservice.entity.CommissionRule;
import com.example.commissionservice.repository.CommissionRuleRepository;
import com.example.commissionservice.services.RuleCacheService;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.File;

@SpringBootApplication
public class CommissionServiceApplication  implements
        ApplicationListener<ContextRefreshedEvent> {
    private final ApplicationContext context;

    private final CommissionRuleRepository commissionRuleRepository;
    private final RuleCacheService ruleCacheService;
    public CommissionServiceApplication(ApplicationContext context, CommissionRuleRepository commissionRuleRepository, RuleCacheService ruleCacheService) {
        this.context = context;
        this.commissionRuleRepository = commissionRuleRepository;

        this.ruleCacheService = ruleCacheService;
    }

    public static void main(String[] args) {
        SpringApplication.run(CommissionServiceApplication.class, args);
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        //load the h2 from the sample csv and then load the data into the cache
        CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper mapper = new CsvMapper();
        File file = context.getResource("classpath:sampledata.csv").getFile();
        MappingIterator<CommissionRule> readValues =
                mapper.reader(CommissionRule.class).with(bootstrapSchema).readValues(file);
        commissionRuleRepository.saveAll(readValues.readAll());
        ruleCacheService.loadCache();
    }
}
