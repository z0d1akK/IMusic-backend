package imusic.backend.mapper.resolver.base;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class BaseEntityResolver<T, ID> implements EntityResolver<T, ID>  {

    protected final JpaRepository<T, ID> repository;

    protected BaseEntityResolver(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    public T resolve(ID id) {
        if (id == null) return null;
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("%s не найден, ключ: %s", getEntityClassName(), id)));
    }

    protected abstract String getEntityClassName();
}

