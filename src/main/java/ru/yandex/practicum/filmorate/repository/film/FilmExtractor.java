package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class FilmExtractor implements ResultSetExtractor<List<Film>> {

    @Override
    public List<Film> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
        List<Film> data = new ArrayList<>();
        while (rs.next()) {
            Film film = Film.builder()
                    .id(rs.getInt("film_id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .duration(rs.getInt("duration"))
                    .build();

            // film.setRatingId(rs.getInt("rating_id"));

            data.add(film);

//            String country = rs.getString("country_name");
//            data.putIfAbsent(country, new ArrayList<>());
//            String city = rs.getString("city_name");
//            data.get(country).add(city);
        }
        return data;
    }
}
