package tech.itpark.web.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tech.itpark.web.dto.ProductDto;
import tech.itpark.web.dto.ProductUpdateDto;
import tech.itpark.web.manager.ProductManager;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")

public class ProductController {
    private final ProductManager manager;

    @GetMapping()
    public List<ProductDto> getAll() {
        return manager.getAll();
    }

    @PostMapping()
    public ProductUpdateDto save(@RequestBody ProductUpdateDto dto) {
        return manager.save(dto);
    }

    @RequestMapping("/{id}/remove")
    public void removeById(@PathVariable long id) {
        manager.removeById(id);
    }
}
