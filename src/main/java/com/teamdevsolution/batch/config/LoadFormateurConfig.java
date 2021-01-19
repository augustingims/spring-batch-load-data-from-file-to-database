package com.teamdevsolution.batch.config;

import com.teamdevsolution.batch.domain.Formateur;
import com.teamdevsolution.batch.listeners.LoadFormateurStepListener;
import com.teamdevsolution.batch.mappers.FormateurItemPreparedStatementSetter;
import com.teamdevsolution.batch.utils.QueryUtils;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

@Configuration
public class LoadFormateurConfig {

    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    public LoadFormateurConfig(StepBuilderFactory stepBuilderFactory, DataSource dataSource) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Formateur> loadFormateurReader(@Value("#{jobParameters['formateursFile']}") final Resource inputFile){

        return new FlatFileItemReaderBuilder<Formateur>()
                .name("loadFormateurReader")
                .resource(inputFile)
                .delimited()
                .delimiter(";")
                .names("id", "nom", "prenom", "adresseEmail")
                .targetType(Formateur.class)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Formateur> loadFormateurWritter() {
        return new JdbcBatchItemWriterBuilder<Formateur>()
                .dataSource(dataSource)
                .sql(QueryUtils.FORMATEUR_INSERT_QUERY)
                .itemPreparedStatementSetter(new FormateurItemPreparedStatementSetter())
                .build();
    }

    @Bean
    public Step loadFormateurStep(){
        return stepBuilderFactory.get("loadFormateurStep")
                .<Formateur, Formateur>chunk(10)
                .reader(loadFormateurReader(null))
                .writer(loadFormateurWritter())
                .listener(loadFormateurStepListener())
                .build();
    }

    @Bean
    public StepExecutionListener loadFormateurStepListener() {
        return new LoadFormateurStepListener();
    }
}
