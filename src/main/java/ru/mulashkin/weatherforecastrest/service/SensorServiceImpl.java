package ru.mulashkin.weatherforecastrest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mulashkin.weatherforecastrest.models.Sensor;
import ru.mulashkin.weatherforecastrest.repository.SensorRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SensorServiceImpl implements SensorService {

    private final SensorRepository sensorRepository;

    @Autowired
    public SensorServiceImpl(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @Override
    public List<Sensor> findAllSensors() {
        return sensorRepository.findAll();
    }

    @Override
    public Optional<Sensor> findSensorById(int id) {
        return sensorRepository.findById(id);
    }

    @Override
    public Sensor findSensorByName(String name) {
        return sensorRepository.findByName(name);
    }

    @Override
    @Transactional
    public void saveSensor(Sensor sensor) {
        sensorRepository.save(sensor);
    }

    @Override
    @Transactional
    public void updateSensor(int id, Sensor sensor) {
        Sensor sensor1 = sensorRepository.findById(id).orElseThrow();
        sensor1.setName(sensor.getName());
    }

    @Override
    @Transactional
    public void deleteSensor(int id) {
        sensorRepository.deleteById(id);
    }


}
