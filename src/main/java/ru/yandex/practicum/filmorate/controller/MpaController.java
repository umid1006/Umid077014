package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService mpaService;

    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("/{id}")
    public MpaRating getMpaById(@PathVariable int id) {
        return mpaService.getMpaRatingById(id);
    }

    @GetMapping
    public List<MpaRating> getAllMpa() {
        return mpaService.getAllMpaRatings();
    }
}