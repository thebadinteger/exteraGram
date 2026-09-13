package androidx.room;

/**
 * Stub for Room's EntityInsertAdapter used in generated *_Impl classes.
 */
public abstract class EntityInsertAdapter<T> {
    protected abstract String createQuery();
    protected abstract void bind(Object statement, T entity);
    public final void insert(T entity) {}
    public final void insert(java.util.Collection<? extends T> entities) {}
}
