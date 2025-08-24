package imusic.backend.mapper.resolver.ops;

import org.springframework.stereotype.Component;
import imusic.backend.entity.ops.User;
import imusic.backend.mapper.resolver.base.BaseEntityResolver;
import imusic.backend.repository.ops.UserRepository;

@Component
public class UserResolver extends BaseEntityResolver<User, Long> {

    public UserResolver(UserRepository repository) {
        super(repository);
    }
    @Override
    protected String getEntityClassName() {
        return "User";
    }
}
