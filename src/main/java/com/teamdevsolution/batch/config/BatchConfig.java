package com.teamdevsolution.batch.config;

import com.teamdevsolution.batch.deciders.SeanceDecider;
import com.teamdevsolution.batch.validators.MyJobParametersValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;

    public BatchConfig(JobBuilderFactory jobBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
    }

    @Bean
    public JobParametersValidator defaultJobParametersValidator(){
        DefaultJobParametersValidator bean = new DefaultJobParametersValidator();
        bean.setRequiredKeys(new String[]{"formateursFile","formationsFile","seancesFile"});
        bean.setOptionalKeys(new String[]{"run.id"});
        return bean;
    }

    @Bean
    public JobParametersValidator myJobParametersValidator(){
        return new MyJobParametersValidator();
    }

    @Bean
    public JobParametersValidator compositeJobParametersValidator(){
        CompositeJobParametersValidator composite = new CompositeJobParametersValidator();
        composite.setValidators(Arrays.asList(defaultJobParametersValidator(), myJobParametersValidator()));
        return composite;
    }

    @Bean
    public Job jobFormationBatch(final Step loadFormateurStep, final Step loadFormationStep, final Step loadSeanceStepWithCsv, final Step loadSeanceStepWithTxt){
        return jobBuilderFactory.get("formations-batch")
                .start(loadFormateurStep)
                .next(loadFormationStep)
                .next(seanceDecider())
                .from(seanceDecider()).on("txt").to(loadSeanceStepWithTxt)
                .from(seanceDecider()).on("csv").to(loadSeanceStepWithCsv)
                .from(loadSeanceStepWithTxt).on("*").end()
                .from(loadSeanceStepWithCsv).on("*").end()
                .end()
                .validator(compositeJobParametersValidator())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public JobExecutionDecider seanceDecider(){
        return new SeanceDecider();
    }
}
