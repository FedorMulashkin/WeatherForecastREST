package ru.mulashkin.weatherforecastrest.service;

import ru.mulashkin.weatherforecastrest.models.Sensor;

import java.util.*;

public interface SensorService {
    List<Sensor> findAllSensors();
    Optional<Sensor> findSensorById(int id);
    Sensor findSensorByName(String name);
    void saveSensor(Sensor sensor);
    void updateSensor(int id, Sensor sensor);
    void deleteSensor(int id);
}
