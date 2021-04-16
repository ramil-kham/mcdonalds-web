package tech.itpark.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ManagerUpdateDto {
  private long id;
  private String name;
  private int salary;
  private int plan;
  private String department;
  private long bossId;
}
