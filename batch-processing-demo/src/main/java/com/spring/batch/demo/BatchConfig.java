package com.spring.batch.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    @Bean
    public StepExecutionListener customStepListener() {
        return new CustomStepExecutionListener();
    }

    @Bean
    public JobExecutionDecider customDecider(){
        return new CustomJobExecutionDecider();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new StepBuilder("step_one", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("STEP1 EXECUTED");
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                .build();
    }

    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {

        return new StepBuilder("step_two", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("STEP2 EXECUTED");
                        boolean isTrue = false;
                        if(isTrue){
                            throw new Exception("Exception occured!!");
                        }
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                .listener(customStepListener())
                .build();
    }



    @Bean
    public Step step4(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

        return new StepBuilder("step_four", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
                        System.out.println("STEP4 EXECUTED");
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                .build();
    }

    @Bean
    public Step step3(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

        return new StepBuilder("step_three", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
                        System.out.println("STEP3 EXECUTED");
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                .build();
    }

//    @Bean
//    public Job job1(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
//        return new JobBuilder("job1", jobRepository)
//                .start(step1(jobRepository, transactionManager))
//                .on(ExitStatus.COMPLETED.getExitCode())
//                .to(step2(jobRepository, transactionManager))
//                .from(step2(jobRepository, transactionManager))
//                .on(ExitStatus.FAILED.getExitCode())
//                .to(step4(jobRepository, transactionManager))
//                .from(step2(jobRepository, transactionManager))
//                .on("CUSTOM_STATUS_COMPLETED")
//                .to(step3(jobRepository, transactionManager))
//                .end().build();
//    }

    @Bean
    public Job job1(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
        return new JobBuilder("job1", jobRepository)
                .start(step1(jobRepository, transactionManager))
                .on(ExitStatus.COMPLETED.getExitCode())
                .to(customDecider())
                .on("CUSTOM_STATUS_JOB_COMPLETED")
                .to(step3(jobRepository, transactionManager))
                .from(customDecider())
                .on("*")
                .to(step4(jobRepository, transactionManager))
                .end().build();
    }

}
