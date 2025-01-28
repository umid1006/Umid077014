package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.io.IOException;

public class MpaRatingSerializer extends JsonSerializer<MpaRating> {

    @Override
    public void serialize(MpaRating mpaRating, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", mpaRating.getId());
        jsonGenerator.writeStringField("name", mpaRating.name());
        jsonGenerator.writeEndObject();
    }
}