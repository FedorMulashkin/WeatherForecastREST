package ru.mulashkin.weatherforecastrest.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.mulashkin.weatherforecastrest.models.Sensor;
import ru.mulashkin.weatherforecastrest.repository.SensorRepository;
@Component
public class SensorUniqueNameValidator implements Validator {

    private final SensorRepository sensorRepository;

    @Autowired
    public SensorUniqueNameValidator(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Sensor.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Sensor sensor = (Sensor) target;
        if (sensorRepository.findByName(sensor.getName()) != null){
            errors.rejectValue("name", "", "This name is already taken");
        }
    }
}
