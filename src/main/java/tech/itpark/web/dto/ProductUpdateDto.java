package tech.itpark.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class ProductUpdateDto {
    private long id;
    private String name;
    private int price;
    private int qty;
}

