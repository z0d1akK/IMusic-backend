package imusic.backend.mapper.resolver.ops;

import org.springframework.stereotype.Component;
import imusic.backend.entity.ops.Client;
import imusic.backend.mapper.resolver.base.BaseEntityResolver;
import imusic.backend.repository.ops.ClientRepository;

@Component
public class ClientResolver extends BaseEntityResolver<Client, Long> {

    public ClientResolver(ClientRepository clientRepository) {
        super(clientRepository);
    }

    @Override
    protected String getEntityClassName() {
        return "Client";
    }
}
