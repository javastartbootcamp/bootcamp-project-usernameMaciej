package pl.javastart.bootcamp.utils;

public interface OrderableItem {

    Long getId();

    Long getSortOrder();

    void setSortOrder(Long sortOrder);

    default boolean isArchived() {
        return false;
    }
}
