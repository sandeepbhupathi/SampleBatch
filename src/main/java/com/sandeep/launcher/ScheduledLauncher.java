package com.sandeep.launcher;


import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledLauncher {
	@Autowired
	public JobOperator jobOperator;

	@Scheduled(cron = "0 55 21 * * ?")
	public void runJob() throws Exception {
		this.jobOperator.startNextInstance("transitionJobNext");
}

}