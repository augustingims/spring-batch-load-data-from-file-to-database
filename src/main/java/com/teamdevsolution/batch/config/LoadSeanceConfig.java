package com.teamdevsolution.batch.config;

import com.teamdevsolution.batch.deciders.SeanceDecider;
import com.teamdevsolution.batch.domain.Seance;
import com.teamdevsolution.batch.listeners.LoadSeanceStepListener;
import com.teamdevsolution.batch.mappers.SeanceItemPreparedStatementSetter;
import com.teamdevsolution.batch.policies.SeanceSkipPolicy;
import com.teamdevsolution.batch.utils.QueryUtils;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class LoadSeanceConfig {

    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    public LoadSeanceConfig(StepBuilderFactory stepBuilderFactory, DataSource dataSource) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Seance> loadSeanceReaderCsv(@Value("#{jobParameters['seancesFile']}") final Resource inputFile){
        return new FlatFileItemReaderBuilder<Seance>()
                .name("loadSeanceReaderCsv")
                .resource(inputFile)
                .delimited()
                .delimiter(";")
                .names("codeFormation", "idFormateur", "dateDebut", "dateFin")
                .fieldSetMapper(seanceFieldSetMapper(null))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Seance> loadSeanceReaderTxt(
            @Value("#{jobParameters['seancesFile']}") final Resource inputFile) {
        return new FlatFileItemReaderBuilder<Seance>()
                .name("loadSeanceReaderTxt")
                .resource(inputFile)
                .fixedLength()
                .columns(new Range[] { new Range(1, 16), new Range(17, 20), new Range(25,
                        32), new Range(37, 44) })
                .names("codeFormation", "idFormateur", "dateDebut", "dateFin")
                .fieldSetMapper(seanceFieldSetMapper(null))
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Seance> loadSeanceWriter(){
        return new JdbcBatchItemWriterBuilder<Seance>()
                .dataSource(dataSource)
                .sql(QueryUtils.SEANCE_INSERT_QUERY)
                .itemPreparedStatementSetter(new SeanceItemPreparedStatementSetter())
                .build();
    }


    @Bean
    public Step loadSeanceStepWithCsv(){
        return stepBuilderFactory
                .get("loadSeanceStepWithCsv")
                .<Seance, Seance>chunk(10)
                .reader(loadSeanceReaderCsv(null))
                .writer(loadSeanceWriter())
                .faultTolerant()
                .skipPolicy(new SeanceSkipPolicy())
                .listener(loadSeanceStepListener())
                .build();
    }

    @Bean
    public Step loadSeanceStepWithTxt(){
        return stepBuilderFactory
                .get("loadSeanceStepWithTxt")
                .<Seance, Seance>chunk(10)
                .reader(loadSeanceReaderTxt(null))
                .writer(loadSeanceWriter())
                .faultTolerant()
                .skipPolicy(new SeanceSkipPolicy())
                .listener(loadSeanceStepListener())
                .build();
    }

    @Bean
    public ConversionService myConversionService() {
        DefaultConversionService dcs = new DefaultConversionService();
        DefaultConversionService.addDefaultConverters(dcs);
        dcs.addConverter(new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(final String input) {
                DateTimeFormatter df = DateTimeFormatter.ofPattern("ddMMyyyy");
                return LocalDate.parse(input, df);
            }
        });

        return dcs;
    }

    @Bean
    public FieldSetMapper<Seance> seanceFieldSetMapper(final ConversionService myConversionService) {
        BeanWrapperFieldSetMapper<Seance> bean = new BeanWrapperFieldSetMapper<>();
        bean.setTargetType(Seance.class);
        bean.setConversionService(myConversionService);
        return bean;
    }

    @Bean
    public StepExecutionListener loadSeanceStepListener() {
        return new LoadSeanceStepListener();
    }

}
