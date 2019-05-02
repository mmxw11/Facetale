package wepa.ftale.web.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import wepa.ftale.domain.Account;

/**
 * @author Matias
 */
@AllArgsConstructor
@Data
public class ProfileModel {

    private Account user;
    private ProfileViewDisplayType displayType;
    private AuthenticatedUserRelationship auserRelationship;
}