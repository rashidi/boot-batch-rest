package scratches.boot.batchrest.batch.post;

import org.springframework.batch.item.ItemProcessor;
import scratches.boot.batchrest.post.Post;
import scratches.boot.batchrest.user.User;

/**
 * @author Rashidi Zin
 */
public class PostItemProcessor implements ItemProcessor<User, Post> {

    @Override
    public Post process(User user) {
        return Post.builder()
                .userId(user.getId())
                .title("Spring Boot Batch Rest")
                .body("Implement rest operation with Spring Boot Batch")
                .build();

    }
}
