package com.sandeep.controller;


import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobLaunchingController {

	//@Autowired
	//private JobLauncher jobLauncher;

	@Autowired
	private JobOperator jobOperator;

	//@Autowired
	//private Job job;

	@RequestMapping(value = "/start", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void launch() throws Exception {
		//JobParameters jobParameters =
			//	new JobParametersBuilder()
				//		.toJobParameters();
		//this.jobLauncher.run(job, jobParameters);
		this.jobOperator.startNextInstance("transitionJobNext");
	}
}
