package pl.javastart.bootcamp.domain.admin.task;

import lombok.Data;

@Data
public class ReorderDto {

    private Long itemId;
    private int targetPosition;

}
