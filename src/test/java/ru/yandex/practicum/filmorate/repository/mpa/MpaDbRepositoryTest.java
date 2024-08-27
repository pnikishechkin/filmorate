package ru.yandex.practicum.filmorate.repository.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({MpaDbRepository.class, MpaRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("MpaDbRepositoryTest")
class MpaDbRepositoryTest {

    public static final Integer TEST_MPA_ID = 1;
    public static final Integer DEFAULT_COUNT_MPA = 5;
    private final MpaDbRepository mpaDbRepository;

    static Mpa getTestMpa() {
        return Mpa.builder().id(TEST_MPA_ID).name("G").build();
    }

    @Test
    @DisplayName("Тест получения всех MPA рейтингов")
    void getAll_ReturnCorrectListMpas() {
        List<Mpa> listMpas = mpaDbRepository.getAll();
        Assertions.assertEquals(listMpas.size(), DEFAULT_COUNT_MPA);
    }

    @Test
    @DisplayName("Тест получения MPA рейтинга по идентификатору")
    void getById_MpaExist_ReturnCorrectMpa() {

        Optional<Mpa> mpa = mpaDbRepository.getById(TEST_MPA_ID);

        assertThat(mpa)
                .isPresent()
                .get()
                .usingRecursiveAssertion()
                .isEqualTo(getTestMpa());
    }

    @Test
    @DisplayName("Тест добавления нового MPA рейтинга")
    void addRating_MpaCorrect_MpaAdded() {

        // init
        Mpa newMpa = Mpa.builder().id(TEST_MPA_ID).name("New MPA").build();

        // when
        mpaDbRepository.addRating(newMpa);

        // then
        List<Mpa> listMpas = mpaDbRepository.getAll();
        Assertions.assertEquals(listMpas.size(), DEFAULT_COUNT_MPA + 1);

        Optional<Mpa> mpa = mpaDbRepository.getById(DEFAULT_COUNT_MPA + 1);

        assertThat(mpa)
                .isPresent()
                .get()
                .usingRecursiveAssertion()
                .isEqualTo(newMpa);
    }
}