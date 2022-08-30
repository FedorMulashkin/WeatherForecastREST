package ru.mulashkin.weatherforecastrest.service;

import ru.mulashkin.weatherforecastrest.models.Measurement;
import ru.mulashkin.weatherforecastrest.models.Sensor;

import java.util.List;
import java.util.Optional;

public interface MeasurementService {
    List<Measurement> findAllMeasurements();
    Optional<Measurement> findMeasurementById(int id);
    List<Measurement> findAllMeasurementsBySensor(Sensor sensor);
    void saveMeasurement(Measurement measurement);
    void updateMeasurement(int id, Measurement measurement);
    void deleteMeasurement(int id);
}
