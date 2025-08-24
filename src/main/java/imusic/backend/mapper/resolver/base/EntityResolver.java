package imusic.backend.mapper.resolver.base;

public interface EntityResolver<T, ID> {
    T resolve(ID id);
}

