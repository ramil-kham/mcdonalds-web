package tech.itpark.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tech.itpark.web.dto.ManagerDto;
import tech.itpark.web.dto.ManagerUpdateDto;
import tech.itpark.web.manager.ManagerManager;

import javax.sql.DataSource;
import java.util.List;

@RequiredArgsConstructor // генерирует конструктор с полями, перед которыми стоит final (не static)
@RestController
@RequestMapping("/managers") // /managers будет прибавляться ко всем путялм
public class ManagerController {
  private final ManagerManager manager;

//  будет автоматически сгенерирована благодаря @RequiredArgsConstructor
//  public ManagerController(ManagerManager manager) {
//    this.manager = manager;
//  }

  @GetMapping()
  public List<ManagerDto> getAll() {
    return manager.getAll();
  }

  @PostMapping()
  public ManagerUpdateDto save(@RequestBody ManagerUpdateDto dto) {
    return manager.save(dto);
  }
}
