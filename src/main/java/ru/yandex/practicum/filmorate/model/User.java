package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString(exclude = "friends")
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor // Добавил, чтобы работал @Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    int id;

    @Size(min = 2, max = 255, message = "Имя должно содержать от 2 до 255 символов")
    @Column(name = "name")
    String name;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы")
    @Column(name = "login", unique = true, nullable = false)
    String login;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Email должен быть корректного формата")
    @Column(name = "email", unique = true, nullable = false)
    String email;

    @NotNull(message = "Дата рождения не может быть null")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    @Column(name = "birthday", nullable = false)
    LocalDate birthday;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "friendships", joinColumns = @JoinColumn(name = "user1_id"))
    @MapKeyColumn(name = "user2_id")
    @Column(name = "created_at")
    @Builder.Default
    Set<Integer> friends = new HashSet<>();

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public void addFriend(int userId) {
        friends.add(userId);
    }

    public void removeFriend(int userId) {
        friends.remove(userId);
    }
}