package ru.yandex.practicum.filmorate.repository.director;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

@Component
public class DirectorRowMapper implements RowMapper<Director> {
    @Override
    public Director mapRow(ResultSet rs, int rowNum) throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();

        return Director.builder()
                .id(rs.getInt("director_id"))
                .name(rs.getString("director_name"))
                .build();
    }
}