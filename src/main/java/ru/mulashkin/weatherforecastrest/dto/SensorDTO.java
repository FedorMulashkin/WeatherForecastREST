package ru.mulashkin.weatherforecastrest.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class SensorDTO {
    @NotBlank
    @Size(min = 3, max = 30, message = "Name should be between 3 and 30 characters")
    private String name;

    public SensorDTO() {
    }

    public SensorDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SensorDTO sensorDTO = (SensorDTO) o;

        return getName() != null ? getName().equals(sensorDTO.getName()) : sensorDTO.getName() == null;
    }

    @Override
    public int hashCode() {
        return getName() != null ? getName().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SensorDTO{" +
                "name='" + name + '\'' +
                '}';
    }
}
