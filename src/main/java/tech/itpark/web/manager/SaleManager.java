package tech.itpark.web.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tech.itpark.web.dto.SaleDto;
import tech.itpark.web.dto.SaleDetailsDto;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class SaleManager {
  private final DataSource dataSource;

  public SaleDto save(SaleDto dto) {
    try (
        Connection connection = dataSource.getConnection();
    ) {
      if (dto.getDetails().size() == 0) {
        throw new RuntimeException("Empty sale - add items");
      }
      // TODO: 1. Если нужно с кем-то сравнить на равенство:
      //  equals + Predicate
      // TODO: 2. Если нужно с кем-то сравнить (больше, меньше или равно):
      //  Comparable (10%)/Comparator (90%)
      dto.getDetails().sort((o1, o2) -> Long.compare(o1.getProductId(), o2.getProductId()));

      // TODO: Generic -> List<>
      // TODO: Wrapper (классы) - Long (long), Boolean (boolean), Integer (int) ...
//      long value = 99;
//      // Long objValue = Long.valueOf(value); // руками
//      Long objValue = value; // TODO: Autoboxing Long.valueOf(value)
//      // long primValue = objValue.longValue(); // руками
//      long primValue = objValue; // TODO: Autounboxing: objValue.longValue()

      // TODO: валидация данных - одна из трудоёмких
      List<String> ids = new LinkedList<>();
      for (SaleDetailsDto detail : dto.getDetails()) {
        // TODO: String.valueOf: 1 -> "1"
        ids.add(String.valueOf(detail.getProductId()));
      }
      // TODO: "1", "2", "3" -> "1, 2, 3"
      String idsJoined = String.join(",", ids);
      // TODO: SELECT id, price, qty FROM products WHERE id IN (1, 2, 3);
      try (
          Statement statement = connection.createStatement();
          // TODO: execute -> CREATE TABLE
          // TODO: executeUpdate -> INSERT, UPDATE, DELETE
          // TODO: executeQuery -> SELECT
          ResultSet resultSet = statement.executeQuery(
              "SELECT id, price, qty FROM products WHERE id IN ("
                  + idsJoined +
                 ") ORDER BY id;");
      ) {
        // то, что у нас сейчас есть на складе (хранится в БД)
        // TODO: 1. actualDetails отсортирован по id ASC
        // TODO: 2. dto.details (что прислал пользователь)
        List<SaleDetailsDto> actualDetails = new ArrayList<>();
        while (resultSet.next()) {
          actualDetails.add(new SaleDetailsDto(
              resultSet.getLong("id"),
              resultSet.getInt("price"),
              resultSet.getInt("qty")
          ));
        }

        // 1. actualDetails - то, что в базе
        // 2. dto.getDetails - что хочет купить пользователь
        if (actualDetails.size() < dto.getDetails().size()) {
          throw new RuntimeException("Упс, некоторые товары не найдены");
        }

        // делаем переменную i, на каждой итерации увеличиваем на 1 до тех пор, пока i < details.size()
        for (int i = 0; i < dto.getDetails().size(); i++) {
          SaleDetailsDto wish = dto.getDetails().get(i);
          SaleDetailsDto actual = actualDetails.get(i);

          if (wish.getQty() > actual.getQty()) {
            throw new RuntimeException("Недостаточно количества на складе");
          }

          if (wish.getPrice() != actual.getPrice()) {
            throw new RuntimeException("Цена изменилась");
          }
        }
      }

      // 1. INSERT sales
      try (
          PreparedStatement statement = connection.prepareStatement(
              "INSERT INTO sales(manager_id) VALUES ( ? );",
              Statement.RETURN_GENERATED_KEYS
          );
      ) {
        int index = 0;
        statement.setLong(++index, dto.getManagerId());
        statement.executeUpdate();
        try (
            ResultSet generatedKeys = statement.getGeneratedKeys();
        ) {
          if (!generatedKeys.next()) {
            throw new RuntimeException("key not generated");
          }
          dto.setId(generatedKeys.getLong(1));
        }
      }

      // 3. INSERT sale_positions
      // Нужно всего один prepared statement: и каждый раз делаете setX + update
      try (
          PreparedStatement statement = connection.prepareStatement("""
                  INSERT INTO sale_details(sale_id, product_id, name, price, qty)
                  VALUES ( ?, ?, ?, ?, ? );""",
              Statement.RETURN_GENERATED_KEYS
          );
      ) {
        for (SaleDetailsDto details : dto.getDetails()) {
          int index = 0;
          statement.setLong(++index, dto.getId());
          statement.setLong(++index, details.getProductId());
          statement.setString(++index, "TODO");
          statement.setLong(++index, details.getPrice());
          statement.setLong(++index, details.getQty());
          statement.executeUpdate();
        }
      }
      return dto;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
