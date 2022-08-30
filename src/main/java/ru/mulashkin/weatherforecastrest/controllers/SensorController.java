package ru.mulashkin.weatherforecastrest.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.mulashkin.weatherforecastrest.dto.MeasurementDTO;
import ru.mulashkin.weatherforecastrest.dto.SensorDTO;
import ru.mulashkin.weatherforecastrest.models.Measurement;
import ru.mulashkin.weatherforecastrest.models.Sensor;
import ru.mulashkin.weatherforecastrest.service.MeasurementService;
import ru.mulashkin.weatherforecastrest.service.SensorService;
import ru.mulashkin.weatherforecastrest.util.ErrorResponse;
import ru.mulashkin.weatherforecastrest.util.NotCreatedException;
import ru.mulashkin.weatherforecastrest.util.NotFoundException;
import ru.mulashkin.weatherforecastrest.util.SensorUniqueNameValidator;

import javax.validation.Valid;
import java.util.*;

import static ru.mulashkin.weatherforecastrest.util.ErrorResponse.collectErrors;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {
    private final SensorService sensorService;
    private final MeasurementService measurementService;
    private final ModelMapper modelMapper;
    private final SensorUniqueNameValidator sensorValidator;

    @Autowired
    public SensorController(SensorService sensorService,
                            MeasurementService measurementService,
                            ModelMapper modelMapper,
                            SensorUniqueNameValidator sensorValidator) {
        this.sensorService = sensorService;
        this.measurementService = measurementService;
        this.modelMapper = modelMapper;
        this.sensorValidator = sensorValidator;
    }

    @GetMapping
    public ResponseEntity<List<SensorDTO>> findAllSensors() {
        List<SensorDTO> sensors = sensorService.findAllSensors().stream().map(this::convertToSensorDTO).toList();
        if (sensors.isEmpty()) {
            throw new NotFoundException("Sorry, but sensor database is empty");
        }
        return new ResponseEntity<>(sensors, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SensorDTO> findSensorById(@PathVariable("id") int id) {
        Sensor sensor = sensorService.findSensorById(id)
                .orElseThrow(() -> new NotFoundException("Sensor with id " + id + " does not exist"));
        return new ResponseEntity<>(convertToSensorDTO(sensor), HttpStatus.OK);
    }

    @GetMapping("/{id}/measurements")
    public ResponseEntity<List<MeasurementDTO>> findAllSensorMeasurements(@PathVariable("id") int id) {
        Sensor sensor = sensorService.findSensorById(id)
                .orElseThrow(() -> new NotFoundException("Sensor with id " + id + " does not exist"));
        List<MeasurementDTO> measurements = measurementService
                .findAllMeasurementsBySensor(sensor).stream().map(this::convertToMeasurementDTO).toList();
        return new ResponseEntity<>(measurements, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> createSensor(@RequestBody @Valid SensorDTO sensorDTO,
                                             BindingResult bindingResult) {
        sensorValidator.validate(convertToSensorEntity(sensorDTO), bindingResult);
        if (bindingResult.hasErrors()) {
            throw new NotCreatedException(collectErrors(bindingResult));
        }
        sensorService.saveSensor(convertToSensorEntity(sensorDTO));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateSensor(@PathVariable("id") int id,
                                             @RequestBody @Valid SensorDTO sensorDTO,
                                             BindingResult bindingResult) {
        sensorService.findSensorById(id).orElseThrow(() -> new NotFoundException("Sensor with id " + id + " does not exist"));
        sensorValidator.validate(convertToSensorEntity(sensorDTO), bindingResult);
        if (bindingResult.hasErrors()) {
            throw new NotCreatedException(collectErrors(bindingResult));
        }
        sensorService.updateSensor(id, convertToSensorEntity(sensorDTO));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSensorById(@PathVariable("id") int id) {
        Optional<Sensor> sensor = sensorService.findSensorById(id);
        if (sensor.isPresent()) {
            sensorService.deleteSensor(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        throw new NotFoundException("Sensor with id " + id + " does not exist");
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> sensorProblem(NotCreatedException exception) {
        ErrorResponse error = new ErrorResponse(exception.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> sensorProblem(NotFoundException exception) {
        ErrorResponse error = new ErrorResponse(exception.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    private SensorDTO convertToSensorDTO(Sensor sensor) {
        return modelMapper.map(sensor, SensorDTO.class);
    }

    private Sensor convertToSensorEntity(SensorDTO sensorDTO) {
        return modelMapper.map(sensorDTO, Sensor.class);
    }

    private MeasurementDTO convertToMeasurementDTO(Measurement measurement) {
        return modelMapper.map(measurement, MeasurementDTO.class);
    }

    private Measurement convertToMeasurementEntity(MeasurementDTO measurementDTO) {
        return modelMapper.map(measurementDTO, Measurement.class);
    }
}
