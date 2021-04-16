package tech.itpark.web.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tech.itpark.web.dto.ManagerDto;
import tech.itpark.web.dto.ManagerUpdateDto;

import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
@Component // то new писать не нужно - за меня будет создано Spring'ом
public class ManagerManager {
  // DataSource
  private final DataSource dataSource; // объект, через который можно получать доступ к базе данных

  public List<ManagerDto> getAll() {
    // DB
    // 1. getConnection() - получить подключение к БД
    // 2. createStatement() - "как руками" пишем запрос
    // 3. выполняем запрос

    // SQLException - checked:
    // 1. Либо выносим в сигнатуру метода
    // 2. Либо заключаем в try-catch

    try (
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        // executeQuery возвращает ResultSet
        // executeUpdate возвращает int
        // execute возвращает boolean
        ResultSet resultSet = statement.executeQuery("SELECT id, name, department, boss_id FROM managers ORDER BY id");
    ) {
      List<ManagerDto> result = new LinkedList<>();
      while (resultSet.next()) {
        // resultSet.getLong(1); // в ResultSet'е колонки считаются с 1
        long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String department = resultSet.getString("department");
        long bossId = resultSet.getLong("boss_id");
        result.add(new ManagerDto(
            id, name, department, bossId
        ));
      }
      return result;
    } catch (SQLException e) {
      throw new RuntimeException(e); // заворачиваем checked в unchecked
    }
  }

  public ManagerUpdateDto save(ManagerUpdateDto dto) {
    if (dto.getId() == 0) {
      return create(dto);
    }
    return update(dto);
  }

  public ManagerUpdateDto create(ManagerUpdateDto dto) {
    try (
        Connection connection = dataSource.getConnection();
        // TODO: запомнить навсегда: как только начинаете подставлять данные от пользователя - только prepareStatement
        // """ - text blocks работает только с Java 15
        PreparedStatement statement = connection.prepareStatement("""
              INSERT INTO managers(name, salary, plan, department, boss_id)
              VALUES ( ?, ?, ?, ?, ? );
            """, Statement.RETURN_GENERATED_KEYS);
    ) {
      int index = 0;
      // TODO: statement.setX(index, value);
      // TODO: ++index эквивалентно index = index + 1, возвращает index
      statement.setString(++index, dto.getName());
      statement.setInt(++index, dto.getSalary());
      statement.setInt(++index, dto.getPlan());
      statement.setString(++index, dto.getDepartment());
      statement.setLong(++index, dto.getBossId());
      statement.executeUpdate();

      // как только запрос выполнился, мы можем запросить сгенерированные ключи
      // т.к. нам нужно id
      try (
          ResultSet generatedKeys = statement.getGeneratedKeys();
      ) {
        // проверили, если ключи сгенерировались, то забрали их
        // положили в dto и отправили обратно
        if (generatedKeys.next()) {
          dto.setId(generatedKeys.getLong(1));
          return dto;
        }

        throw new RuntimeException("keys not generated");
      }

    } catch (SQLException e) {
      throw new RuntimeException(e); // заворачиваем checked в unchecked
    }
  }

  public ManagerUpdateDto update(ManagerUpdateDto dto) {
    try (
        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("""
              UPDATE managers
              SET name = ?, salary = ?, plan = ?, department = ?, boss_id = ?
              WHERE id = ?;
            """);
    ) {
      int index = 0;
      statement.setString(++index, dto.getName());
      statement.setInt(++index, dto.getSalary());
      statement.setInt(++index, dto.getPlan());
      statement.setString(++index, dto.getDepartment());
      statement.setLong(++index, dto.getBossId());
      statement.setLong(++index, dto.getId()); // WHERE id = ?
      statement.executeUpdate();

      return dto;
    } catch (SQLException e) {
      throw new RuntimeException(e); // заворачиваем checked в unchecked
    }
  }
}
