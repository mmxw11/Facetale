package wepa.ftale.web.profile;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import wepa.ftale.domain.Account;
import wepa.ftale.domain.Post;

/**
 * @author Matias
 */
@AllArgsConstructor
@Data
public class ProfileModel {

    private Account user;
    private ProfileViewDisplayType displayType;
    private UserRelationship auserRelationship;
    private List<Post> posts;
}