package imusic.backend.mapper.resolver.ops;

import imusic.backend.entity.ops.Notification;
import imusic.backend.mapper.resolver.base.BaseEntityResolver;
import imusic.backend.repository.ops.NotificationRepository;
import org.springframework.stereotype.Component;

@Component
public class NotificationResolver extends BaseEntityResolver<Notification, Long> {

    public NotificationResolver(NotificationRepository notificationRepository) {
        super(notificationRepository);
    }

    @Override
    protected String getEntityClassName() {
        return "Notification";
    }
}
