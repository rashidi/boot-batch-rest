package scratches.boot.batchrest.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import scratches.boot.batchrest.post.Post;
import scratches.boot.batchrest.post.PostRestRepository;
import scratches.boot.batchrest.user.User;
import scratches.boot.batchrest.user.UserRestRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.batch.core.ExitStatus.COMPLETED;

/**
 * @author Rashidi Zin
 */
@SpringBatchTest
@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = BatchConfiguration.class)
class BatchConfigurationTests {

    @MockBean
    private UserRestRepository userRepository;

    @MockBean
    private PostRestRepository postRepository;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    @DisplayName("PostRepository should be executed based on number existing user")
    public void postJob() throws Exception {
        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);
        var existingUser = User.builder().id(1L).username("Samantha").build();
        var createdPost = Post.builder().id(2L).build();

        doReturn(existingUser).when(userRepository).findByUsername("Samantha");

        doReturn(null).when(userRepository).findByUsername("Rashidi");

        doReturn(createdPost).when(postRepository).save(any(Post.class));

        var execution = jobLauncherTestUtils.launchJob();

        verify(postRepository).save(postArgumentCaptor.capture());

        assertThat(postArgumentCaptor.getValue())
                .extracting(Post::getUserId)
                .isEqualTo(existingUser.getId());

        assertThat(execution)
                .extracting(JobExecution::getExitStatus)
                .isEqualTo(COMPLETED);
    }

}
