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
import ru.mulashkin.weatherforecastrest.util.ErrorResponse;
import ru.mulashkin.weatherforecastrest.util.NotCreatedException;
import ru.mulashkin.weatherforecastrest.util.NotFoundException;
import ru.mulashkin.weatherforecastrest.util.SensorDoesNotExistValidator;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static ru.mulashkin.weatherforecastrest.util.ErrorResponse.collectErrors;

@RestController
@RequestMapping("/api/measurements")
public class MeasurementController {
    private final MeasurementService measurementService;
    private final ModelMapper modelMapper;
    private final SensorDoesNotExistValidator sensorValidator;

    @Autowired
    public MeasurementController(MeasurementService measurementService,
                                 ModelMapper modelMapper,
                                 SensorDoesNotExistValidator sensorValidator) {
        this.measurementService = measurementService;
        this.modelMapper = modelMapper;
        this.sensorValidator = sensorValidator;
    }

    @GetMapping
    public ResponseEntity<List<MeasurementDTO>> findAllMeasurements() {
        List<MeasurementDTO> measurements = measurementService
                .findAllMeasurements().stream().map(this::convertToMeasurementDTO).toList();
        if (measurements.isEmpty()) {
            throw new NotFoundException("Sorry, but measurement database is empty");
        }
        return new ResponseEntity<>(measurements, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeasurementDTO> findMeasurementById(@PathVariable("id") int id) {
        Measurement measurement = measurementService.findMeasurementById(id)
                .orElseThrow(() -> new NotFoundException("Measurement with id " + id + " does not exist"));
        return new ResponseEntity<>(convertToMeasurementDTO(measurement), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> createMeasurement(@RequestBody @Valid MeasurementDTO measurementDTO,
                                                  BindingResult bindingResult) {
        sensorValidator.validate(convertToMeasurementEntity(measurementDTO), bindingResult);
        if (bindingResult.hasErrors()) {
            throw new NotCreatedException(collectErrors(bindingResult));
        }
        measurementService.saveMeasurement(convertToMeasurementEntity(measurementDTO));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateMeasurement(@PathVariable("id") int id,
                                                  @RequestBody @Valid MeasurementDTO measurementDTO,
                                                  BindingResult bindingResult) {
        Measurement measurement = convertToMeasurementEntity(measurementDTO);
        System.out.println(measurement);
        measurement.setSensor(convertToSensorEntity(measurementDTO.getSensor()));
        sensorValidator.validate(convertToMeasurementEntity(measurementDTO), bindingResult);
        if (bindingResult.hasErrors()) {
            throw new NotCreatedException(collectErrors(bindingResult));
        }
        measurementService.updateMeasurement(id, measurement);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeasurementById(@PathVariable("id") int id) {
        Optional<Measurement> sensor = measurementService.findMeasurementById(id);
        if (sensor.isPresent()) {
            measurementService.deleteMeasurement(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        throw new NotFoundException("Measurement with id " + id + " does not exist");
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> measurementProblem(NotCreatedException exception) {
        ErrorResponse error = new ErrorResponse(exception.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> measurementProblem(NotFoundException exception) {
        ErrorResponse error = new ErrorResponse(exception.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    private MeasurementDTO convertToMeasurementDTO(Measurement measurement) {
        return modelMapper.map(measurement, MeasurementDTO.class);
    }

    private Measurement convertToMeasurementEntity(MeasurementDTO measurementDTO) {
        return modelMapper.map(measurementDTO, Measurement.class);
    }

    private SensorDTO convertToSensorDTO(Sensor sensor) {
        return modelMapper.map(sensor, SensorDTO.class);
    }

    private Sensor convertToSensorEntity(SensorDTO sensorDTO) {
        return modelMapper.map(sensorDTO, Sensor.class);
    }
}
