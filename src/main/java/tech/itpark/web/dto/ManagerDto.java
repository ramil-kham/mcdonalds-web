package tech.itpark.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO - Data Transfer Object
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ManagerDto {
  private long id;
  private String name;
  private String department;
  private long bossId;
}
