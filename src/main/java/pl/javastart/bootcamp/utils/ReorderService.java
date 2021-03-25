package pl.javastart.bootcamp.utils;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ReorderService<T extends OrderableItem> {

    private static final long SORT_ORDER_STEP = 100;

    private JpaRepository<T, Long> repository;

    public ReorderService(JpaRepository<T, Long> repository) {
        this.repository = repository;
    }

    public void moveItem(Long itemId, int targetSortPosition) {
        T item = repository.findById(itemId).orElseThrow();
        List<T> items = repository.findAll()
                .stream()
                .filter(i -> !i.getId().equals(itemId))
                .filter(i -> !i.isArchived())
                .sorted(Comparator.comparing(OrderableItem::getSortOrder))
                .collect(Collectors.toList());
        Long sortOrder = calculateSortOrderAndReorder(targetSortPosition, items);
        item.setSortOrder(sortOrder);
        repository.save(item);
    }

    private Long calculateSortOrderAndReorder(int targetSortPosition, List<T> items) {
        Long sortOrder;

        if (items.isEmpty()) {
            sortOrder = SORT_ORDER_STEP;
        } else if (targetSortPosition == 0) {
            long firstItemSortOrder = items.get(0).getSortOrder();
            sortOrder = firstItemSortOrder - SORT_ORDER_STEP;
        } else if (targetSortPosition >= items.size()) {
            OrderableItem last = items.get(items.size() - 1);
            sortOrder = last.getSortOrder() + SORT_ORDER_STEP;
        } else {
            sortOrder = insertOrderableItemItemBetweenOthers(targetSortPosition, items);
        }
        return sortOrder;
    }

    private long insertOrderableItemItemBetweenOthers(int targetSortPosition,
                                                      List<T> allOrderBySortOrder) {
        OrderableItem before = allOrderBySortOrder.get(targetSortPosition - 1);
        OrderableItem after = allOrderBySortOrder.get(targetSortPosition);

        long targetSortOrder = ((before.getSortOrder() + after.getSortOrder()) / 2);
        if (targetSortOrder != before.getSortOrder() && targetSortOrder != after.getSortOrder()) {
            return targetSortOrder;
        } else {
            updateAllOrderableItemItemsSortOrders(allOrderBySortOrder, targetSortPosition);
            return insertOrderableItemItemBetweenOthers(targetSortPosition, allOrderBySortOrder);
        }
    }

    private void updateAllOrderableItemItemsSortOrders(List<T> allOrderBySortOrder,
                                                       int targetSortPosition) {
        long i = 1;
        for (T courseOrderableItem : allOrderBySortOrder) {
            if (i > targetSortPosition) {
                courseOrderableItem.setSortOrder((i + 1) * SORT_ORDER_STEP);
            } else {
                courseOrderableItem.setSortOrder(i * SORT_ORDER_STEP);
            }
            i++;
        }
        repository.saveAll(allOrderBySortOrder);
    }
}
