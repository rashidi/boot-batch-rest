package scratches.boot.batchrest.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

/**
 * @author ChengJun Chuah, GfK
 */

@Slf4j
public class JobListener  implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Initiating job");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Job done!");
    }
}
