package tech.itpark.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SaleDto {
  private long id;
  private long managerId;
  private List<SaleDetailsDto> details;
}
