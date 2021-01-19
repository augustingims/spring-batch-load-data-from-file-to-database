package com.teamdevsolution.batch.config;

import com.teamdevsolution.batch.domain.Formation;
import com.teamdevsolution.batch.listeners.LoadFormationStepListener;
import com.teamdevsolution.batch.mappers.FormationItemPreparedStatementSetter;
import com.teamdevsolution.batch.utils.QueryUtils;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;

@Configuration
public class LoadFormationConfig {

    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    public LoadFormationConfig(StepBuilderFactory stepBuilderFactory, DataSource dataSource) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
    }

    @Bean
    @StepScope
    public StaxEventItemReader<Formation> loadFormationReader(@Value("#{jobParameters['formationsFile']}") final Resource inputFile){
        return new StaxEventItemReaderBuilder<Formation>()
                .name("loadFormationReader")
                .resource(inputFile)
                .addFragmentRootElements("formation")
                .unmarshaller(formationMarshaller())
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Formation> loadFormationWriter(){
        return new JdbcBatchItemWriterBuilder<Formation>()
                .dataSource(dataSource)
                .sql(QueryUtils.FORMATION_INSERT_QUERY)
                .itemPreparedStatementSetter(new FormationItemPreparedStatementSetter())
                .build();
    }

    @Bean
    public Step loadFormationStep(){
        return stepBuilderFactory
                .get("loadFormationStep")
                .<Formation, Formation>chunk(10)
                .reader(loadFormationReader(null))
                .writer(loadFormationWriter())
                .listener(loadFormationStepListener())
                .build();
    }

    @Bean
    public Jaxb2Marshaller formationMarshaller(){
        Jaxb2Marshaller marshaller =  new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Formation.class);
        return marshaller;
    }

    @Bean
    public StepExecutionListener loadFormationStepListener() {
        return new LoadFormationStepListener();
    }
}
