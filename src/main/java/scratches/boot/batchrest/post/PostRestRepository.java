package scratches.boot.batchrest.post;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * @author Rashidi Zin
 */
@Repository
public class PostRestRepository {

    private final RestTemplate restTemplate;

    public PostRestRepository(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public Post save(Post post) {
        var uri = URI.create("https://jsonplaceholder.typicode.com/posts");

        return restTemplate.postForEntity(uri, post, Post.class).getBody();
    }

}
