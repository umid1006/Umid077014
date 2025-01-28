package ru.yandex.practicum.filmorate;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, FilmDbStorage.class, GenreDbStorage.class}) // Добавьте все DbStorage классы
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional // Добавьте эту аннотацию
class FilmorateApplicationTests {

	private final UserDbStorage userStorage;
	//private final FilmDbStorage filmDbStorage; // Добавьте, когда создадите

	@Test
	public void testFindUserById() throws UserNotFoundException {
		// 1. Подготовка данных для теста
		User testUser = new User("test@email.com", "testLogin", "Test Name", LocalDate.of(2000, 1, 1));
		testUser = userStorage.addUser(testUser); // Добавляем пользователя и получаем его с присвоенным ID

		// 2. Вызов тестируемого метода
		User foundUser = userStorage.getUserById(testUser.getId());

		// 3. Проверка утверждений
		assertThat(foundUser)
				.isNotNull()
				.hasFieldOrPropertyWithValue("id", testUser.getId())
				.hasFieldOrPropertyWithValue("email", "test@email.com")
				.hasFieldOrPropertyWithValue("login", "testLogin")
				.hasFieldOrPropertyWithValue("name", "Test Name")
				.hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 1, 1));
	}
}