package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.constant.FilmorateConstants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FilmDataCheckerValidation implements ConstraintValidator<FilmDataChecker, LocalDate> {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(FilmorateConstants.DATE_TIME_FORMATTER);
    private LocalDate startDate;

    @Override
    public void initialize(FilmDataChecker filmDataChecker) {
        ConstraintValidator.super.initialize(filmDataChecker);
        this.startDate = LocalDate.parse(filmDataChecker.value(), dateTimeFormatter);
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value.isAfter(this.startDate);
    }
}
