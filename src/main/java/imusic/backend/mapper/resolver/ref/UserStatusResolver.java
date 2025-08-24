package imusic.backend.mapper.resolver.ref;

import org.springframework.stereotype.Component;
import imusic.backend.entity.ref.UserStatus;
import imusic.backend.mapper.resolver.base.BaseEntityResolver;
import imusic.backend.repository.ref.UserStatusRepository;

@Component
public class UserStatusResolver extends BaseEntityResolver<UserStatus, Long> {

    public UserStatusResolver(UserStatusRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityClassName() {
        return "UserStatus";
    }
}

