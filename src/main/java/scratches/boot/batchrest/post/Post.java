package scratches.boot.batchrest.post;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

/**
 * @author Rashidi Zin
 */
@Value
@Builder
@JsonDeserialize(builder = Post.PostBuilder.class)
public class Post {

    Long id;

    String title;

    String body;

    Long userId;

    @JsonPOJOBuilder(withPrefix = "")
    public static class PostBuilder {

    }
}
