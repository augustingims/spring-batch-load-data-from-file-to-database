package com.teamdevsolution.batch.listeners;

import com.teamdevsolution.batch.domain.Formateur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.listener.StepListenerSupport;

public class LoadFormateurStepListener extends StepListenerSupport<Formateur, Formateur> implements StepExecutionListener {

    private Logger LOGGER = LoggerFactory.getLogger(LoadFormateurStepListener.class);

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        LOGGER.info("Chargement des formateurs :{} formateur(s) enregistré(s) ", stepExecution.getWriteCount());
        return stepExecution.getExitStatus();
    }
}
