package com.sandeep.config;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class StepTranstionConfiguration extends DefaultBatchConfigurer implements ApplicationContextAware {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public JobExplorer jobExplorer;

	@Autowired
	public JobRepository jobRepository;

	@Autowired
	public JobRegistry jobRegistry;

	@Autowired
	public JobLauncher jobLauncher;

	private ApplicationContext applicationContext;

	@Bean
	public JobRegistryBeanPostProcessor jobRegistrar() throws Exception {
		JobRegistryBeanPostProcessor registrar = new JobRegistryBeanPostProcessor();

		registrar.setJobRegistry(this.jobRegistry);
		registrar.setBeanFactory(this.applicationContext.getAutowireCapableBeanFactory());
		registrar.afterPropertiesSet();

		return registrar;
	}

	@Bean
	public DataSource batchDataSurce(){
		HikariDataSource ds = new HikariDataSource();
		ds.setDriverClassName("org.postgresql.Driver");
		ds.setJdbcUrl("jdbc:postgresql://localhost:5432/learning_spring_batch");
		ds.setUsername("postgres");
		ds.setPassword("root");
		ds.setMaximumPoolSize(100);
		ds.setIdleTimeout(60000);
		ds.setConnectionTestQuery("SELECT 1");
		return ds;
	}
	
	@Bean
	public SimpleJobLauncher jobLauncher() throws Exception{
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(getBatchJobRepository());
		return jobLauncher;
	}
	@Bean
	public JobRepository getBatchJobRepository() throws Exception {
		JobRepositoryFactoryBean jobRepo = new JobRepositoryFactoryBean();
		jobRepo.setDataSource(batchDataSurce());
		jobRepo.setTransactionManager(getTransactionManager());
		return jobRepo.getObject();
	}

	@Bean
	public JobOperator jobOperator() throws Exception {
		SimpleJobOperator simpleJobOperator = new SimpleJobOperator();

		simpleJobOperator.setJobLauncher(this.jobLauncher);
		simpleJobOperator.setJobParametersConverter(new DefaultJobParametersConverter());
		simpleJobOperator.setJobRepository(this.jobRepository);
		simpleJobOperator.setJobExplorer(this.jobExplorer);
		simpleJobOperator.setJobRegistry(this.jobRegistry);

		simpleJobOperator.afterPropertiesSet();

		return simpleJobOperator;
	}
	
	@Bean
	public Step step1(){
		return stepBuilderFactory.get("Step 1").tasklet((contribution, chunkContext)->{
			SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");

			System.out.println(
					String.format(">>Step1 was run at %s",
							formatter.format(new Date())));
			return RepeatStatus.FINISHED;
		}).build();
	}

	@Bean
	public Step step2(){
		return stepBuilderFactory.get("Step 2").tasklet((contribution, chunkContext)->{
			SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");

			System.out.println(
					String.format(">> Step2 run at %s",
							formatter.format(new Date())));
			return RepeatStatus.FINISHED;
		}).build();
	}
	
	@Bean
	public Step step3(){
		return stepBuilderFactory.get("Step 3").tasklet((contribution, chunkContext)->{
			SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");

			System.out.println(
					String.format(">>Step3 was run at %s",
							formatter.format(new Date())));
			return RepeatStatus.FINISHED;
		}).build();
	}
	
	@Bean
	public Job job() {
		return jobBuilderFactory.get("transitionJobNext")
				.incrementer(new RunIdIncrementer())
				.start(step1()).on("COMPLETED").to(step2()).on("COMPLETED").to(step3()).end()
				.build();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public JobLauncher getJobLauncher() {
		SimpleJobLauncher jobLauncher = null;
		try {
			jobLauncher = new SimpleJobLauncher();
			jobLauncher.setJobRepository(jobRepository);
			jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
			jobLauncher.afterPropertiesSet();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return jobLauncher;
	}
}
