package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest
public class UserControllerTest {

    @Autowired
    UserController controller;

    @Test
    public void addCorrectUsers_getUsers_correctData() {


    }

    @Test
    public void addUserIncorrectEmail_throwException() {

        ValidationException ex = Assertions.assertThrows(ValidationException.class, () -> {
            User user =
                    User.builder().id(0).name("Имя")
                            .login("Login").email("sdfsdfmail.ru")
                            .birthday(LocalDate.of(1990, 5, 19)).build();
            controller.addUser(user);
        });

        Assertions.assertNotNull(ex);
        Assertions.assertEquals(ex.getMessage(), "Ошибка! Электронная почта не может быть пустой и должна содержать символ @");
    }

    @Test
    public void addUserEmptyLogin_throwException() {

        ValidationException ex = Assertions.assertThrows(ValidationException.class, () -> {
            User user =
                    User.builder().id(0).name("Имя")
                            .login("").email("sdfsdf@mail.ru")
                            .birthday(LocalDate.of(1990, 5, 19)).build();
            controller.addUser(user);
        });

        Assertions.assertNotNull(ex);
        Assertions.assertEquals(ex.getMessage(), "Ошибка! Логин не может быть пустым и содержать пробелы");
    }

    @Test
    public void addUserBirthdayFromFuture_throwException() {

        ValidationException ex = Assertions.assertThrows(ValidationException.class, () -> {
            User user =
                    User.builder().id(0).name("Имя")
                            .login("asd").email("sdfsdf@mail.ru")
                            .birthday(LocalDate.of(2222, 5, 19)).build();
            controller.addUser(user);
        });

        Assertions.assertNotNull(ex);
        Assertions.assertEquals(ex.getMessage(), "Ошибка! Дата рождения не может быть в будущем");
    }
}
