package scratches.boot.batchrest.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import scratches.boot.batchrest.batch.post.PostItemProcessor;
import scratches.boot.batchrest.batch.post.PostItemWriter;
import scratches.boot.batchrest.batch.user.UserItemReader;
import scratches.boot.batchrest.post.Post;
import scratches.boot.batchrest.post.PostRestRepository;
import scratches.boot.batchrest.user.User;
import scratches.boot.batchrest.user.UserRestRepository;

/**
 * @author Rashidi Zin
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Bean
    public UserItemReader userItemReader(UserRestRepository repository) {
        return new UserItemReader(repository);
    }

    @Bean
    public PostItemProcessor postItemProcessor() {
        return new PostItemProcessor();
    }

    @Bean
    public PostItemWriter postItemWriter(PostRestRepository repository) {
        return new PostItemWriter(repository);
    }

    @Bean
    public Step postStep(StepBuilderFactory factory, UserItemReader reader, PostItemWriter writer) {
        return factory.get("postStep")
                .<User, Post>chunk(1)
                .reader(reader)
                .processor(postItemProcessor())
                .writer(writer)
                .build();
    }

    @Bean
    public Job postJob(JobBuilderFactory factory, Step postStep) {
        return factory.get("postJob").incrementer(new RunIdIncrementer()).flow(postStep).end().build();
    }

}
