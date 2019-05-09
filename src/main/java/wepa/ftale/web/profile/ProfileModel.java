package wepa.ftale.web.profile;

import java.util.Map;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Data;
import wepa.ftale.domain.Account;
import wepa.ftale.domain.Post;
import wepa.ftale.domain.projection.UserPostView;

/**
 * @author Matias
 */
@AllArgsConstructor
@Data
public class ProfileModel {

    private Account user;
    private ProfileViewDisplayType displayType;
    private UserRelationship auserRelationship;
    private Page<Post> posts;
    private Map<Long, UserPostView> userPostViews;
}