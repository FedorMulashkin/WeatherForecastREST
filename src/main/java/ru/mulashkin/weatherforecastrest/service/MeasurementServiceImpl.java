package ru.mulashkin.weatherforecastrest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mulashkin.weatherforecastrest.models.Measurement;
import ru.mulashkin.weatherforecastrest.models.Sensor;
import ru.mulashkin.weatherforecastrest.repository.MeasurementsRepository;
import ru.mulashkin.weatherforecastrest.repository.SensorRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MeasurementServiceImpl implements MeasurementService {
    private final MeasurementsRepository measurementsRepository;
    private final SensorService sensorService;

    @Autowired
    public MeasurementServiceImpl(MeasurementsRepository measurementsRepository, SensorService sensorService) {
        this.measurementsRepository = measurementsRepository;
        this.sensorService = sensorService;
    }

    @Override
    public List<Measurement> findAllMeasurements() {
        return measurementsRepository.findAll();
    }

    @Override
    public Optional<Measurement> findMeasurementById(int id) {
        return measurementsRepository.findById(id);
    }

    @Override
    public List<Measurement> findAllMeasurementsBySensor(Sensor sensor) {
        return measurementsRepository.findMeasurementsBySensor(sensor);
    }

    @Override
    @Transactional
    public void saveMeasurement(Measurement measurement) {
        enrichMeasurement(measurement);
        measurementsRepository.save(measurement);
    }

    @Override
    @Transactional
    public void updateMeasurement(int id, Measurement measurement) {
        enrichMeasurement(measurement);
        Measurement measurement1 = measurementsRepository.findById(id).orElseThrow();
        measurement1.setValue(measurement.getValue());
        measurement1.setRaining(measurement.getRaining());
        measurement1.setSensor(measurement.getSensor());
    }

    @Override
    @Transactional
    public void deleteMeasurement(int id) {
        measurementsRepository.deleteById(id);
    }

    public void enrichMeasurement(Measurement measurement) {
        measurement.setSensor(sensorService.findSensorByName(measurement.getSensor().getName()));
        measurement.setTimestamp(LocalDateTime.now());
    }
}
