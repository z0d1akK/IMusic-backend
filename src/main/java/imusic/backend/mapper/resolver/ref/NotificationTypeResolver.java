package imusic.backend.mapper.resolver.ref;

import imusic.backend.entity.ref.NotificationType;
import imusic.backend.mapper.resolver.base.BaseEntityResolver;
import imusic.backend.repository.ref.NotificationTypeRepository;
import org.springframework.stereotype.Component;

@Component
public class NotificationTypeResolver extends BaseEntityResolver<NotificationType, Long> {

    public NotificationTypeResolver(NotificationTypeRepository notificationTypeRepository) {
        super(notificationTypeRepository);
    }

    @Override
    protected String getEntityClassName() {
        return "NotificationType";
    }
}
