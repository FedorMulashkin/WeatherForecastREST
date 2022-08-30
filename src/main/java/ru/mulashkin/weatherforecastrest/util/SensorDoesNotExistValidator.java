package ru.mulashkin.weatherforecastrest.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.mulashkin.weatherforecastrest.models.Measurement;
import ru.mulashkin.weatherforecastrest.models.Sensor;
import ru.mulashkin.weatherforecastrest.repository.SensorRepository;

@Component
public class SensorDoesNotExistValidator implements Validator {

    private final SensorRepository sensorRepository;

    @Autowired
    public SensorDoesNotExistValidator(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Measurement.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Measurement measurement = (Measurement) target;
        if (sensorRepository.findByName(measurement.getSensor().getName()) == null) {
            errors.rejectValue("sensor", "", "This sensor does not exist");
        }
    }
}
