package com.spring.batch.demo;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.lang.Nullable;

public class CustomStepExecutionListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        //logic for before step execution
        System.out.println("STEP NAME: "+stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // logic for after step execution
        System.out.println("AFTER: "+stepExecution.getStepName());
        return new ExitStatus("CUSTOM_STATUS_COMPLETED");
    }
}
