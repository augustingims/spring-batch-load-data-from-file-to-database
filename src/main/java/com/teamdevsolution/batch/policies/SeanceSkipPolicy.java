package com.teamdevsolution.batch.policies;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.dao.DataIntegrityViolationException;

public class SeanceSkipPolicy implements SkipPolicy {
    @Override
    public boolean shouldSkip(Throwable throwable, int i) throws SkipLimitExceededException {
        if (throwable instanceof DataIntegrityViolationException && i < 10) {
            return true;
        }
        return false;
    }
}
