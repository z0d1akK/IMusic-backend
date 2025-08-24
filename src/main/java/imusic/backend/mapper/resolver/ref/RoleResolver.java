package imusic.backend.mapper.resolver.ref;

import org.springframework.stereotype.Component;
import imusic.backend.entity.ref.Role;
import imusic.backend.mapper.resolver.base.BaseEntityResolver;
import imusic.backend.repository.ref.RoleRepository;

@Component
public class RoleResolver extends BaseEntityResolver<Role, Long> {

    public RoleResolver(RoleRepository repository) {
        super(repository);
    }

    @Override
    protected String getEntityClassName() {
        return "Role";
    }
}
