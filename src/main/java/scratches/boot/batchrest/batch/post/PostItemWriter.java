package scratches.boot.batchrest.batch.post;

import lombok.AllArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import scratches.boot.batchrest.post.Post;
import scratches.boot.batchrest.post.PostRestRepository;

import java.util.List;

/**
 * @author Rashidi Zin
 */
@AllArgsConstructor
public class PostItemWriter implements ItemWriter<Post> {

    private final PostRestRepository repository;

    @Override
    public void write(List<? extends Post> items) {
        items.forEach(repository::save);
    }

}
