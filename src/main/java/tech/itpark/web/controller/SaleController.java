package tech.itpark.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.itpark.web.dto.SaleDto;
import tech.itpark.web.manager.SaleManager;

@RequiredArgsConstructor // генерирует конструктор с полями, перед которыми стоит final (не static)
@RestController
@RequestMapping("/sales") // /managers будет прибавляться ко всем путялм
public class SaleController {
  private final SaleManager manager;

  @PostMapping()
  public SaleDto save(@RequestBody SaleDto dto) {
    return manager.save(dto);
  }
}
