package com.project.CarVoyage.classes.car;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class CarRowMapper implements RowMapper<Car> {

    @Override
    public Car mapRow(ResultSet rs, int rowNum) throws SQLException {
        Car car = new Car();
        car.setCarId(rs.getInt("car_id"));
        car.setMake(rs.getString("make"));
        car.setModel(rs.getString("model"));
        car.setYear(rs.getInt("year"));
        car.setColor(rs.getString("color"));
        car.setLicensePlate(rs.getString("license_plate"));
        car.setMileage(rs.getInt("mileage"));
        car.setDailyRate(rs.getDouble("daily_rate"));
        car.setLocationId(rs.getInt("location_id"));
        car.setFuelType(rs.getString("fuel_type"));
        car.setTransmissionType(rs.getString("transmission_type"));
        car.setSeats(rs.getInt("seats"));
        car.setDoors(rs.getInt("doors"));
        car.setAirConditioning(rs.getString("air_conditioning"));
        car.setTankSize(rs.getInt("tank_size"));
        car.setImage(rs.getString("image"));

        return car;
    }

}
