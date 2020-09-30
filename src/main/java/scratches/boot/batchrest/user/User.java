package scratches.boot.batchrest.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

/**
 * @author Rashidi Zin
 */
@Value
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = User.UserBuilder.class)
public class User {

    Long id;

    String username;

    @JsonPOJOBuilder(withPrefix = "")
    public static class UserBuilder {

    }

}
