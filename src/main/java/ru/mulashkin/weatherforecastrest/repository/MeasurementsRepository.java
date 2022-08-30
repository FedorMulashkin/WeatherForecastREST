package ru.mulashkin.weatherforecastrest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mulashkin.weatherforecastrest.models.Measurement;
import ru.mulashkin.weatherforecastrest.models.Sensor;

import java.util.*;

@Repository
public interface MeasurementsRepository extends JpaRepository<Measurement, Integer> {
    List<Measurement> findMeasurementsBySensor(Sensor sensor);
}
