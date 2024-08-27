package com.project.CarVoyage.classes.car;


import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcCarRepository implements CarRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Car> findAll() {
        String sql = "SELECT * FROM cars";
        return jdbcTemplate.query(sql, new CarRowMapper());
    }

    @Override
    public Car findById(int carId) {
        String sql = "SELECT * FROM cars WHERE car_id = ?";
        return jdbcTemplate.queryForObject(sql, new CarRowMapper(), carId);
    }

    @Override
    public List<Car> findAllSortedByPriceAsc() {
        String sql = "SELECT * FROM cars ORDER BY daily_rate ASC";
        return jdbcTemplate.query(sql, new CarRowMapper());
    }

    @Override
    public List<Car> findAllSortedByPriceDesc() {
        String sql = "SELECT * FROM cars ORDER BY daily_rate DESC";
        return jdbcTemplate.query(sql, new CarRowMapper());
    }

    @Override
    public List<Car> findByLocation(String location) {
        String sql = "SELECT * FROM cars WHERE location_id = (SELECT location_id FROM locations WHERE name = ?)";
        return jdbcTemplate.query(sql, new CarRowMapper(), location);
    }

    @Override
    public List<Car> findByFuelType(String fuelType) {
        String sql = "SELECT * FROM cars WHERE fuel_type = ?";
        return jdbcTemplate.query(sql, new CarRowMapper(), fuelType);
    }

    @Override
    public List<Car> findByTransmissionType(String transmissionType) {
        String sql = "SELECT * FROM cars WHERE transmission_type = ?";
        return jdbcTemplate.query(sql, new CarRowMapper(), transmissionType);
    }

    @Override
    public List<Car> findBySeats(int seats) {
        String sql = "SELECT * FROM cars WHERE seats = ?";
        return jdbcTemplate.query(sql, new CarRowMapper(), seats);
    }

    public List<Car> findAvailableCars(int locationId, LocalDate dateFrom, LocalDate dateTo) {
        String sql = "SELECT * FROM cars c WHERE c.location_id = ? " +
                "AND c.car_id NOT IN (" +
                "SELECT r.car_id FROM reservations r " +
                "WHERE r.start_date < ? AND r.end_date > ?)";

        return jdbcTemplate.query(sql, new CarRowMapper(), locationId, dateTo, dateFrom);
    }

    @Override
    public Car findCarMakeAndModelById(int carId) {
        String sql = "SELECT make, model FROM cars WHERE car_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Car car = new Car();
            car.setMake(rs.getString("make"));
            car.setModel(rs.getString("model"));
            return car;
        }, carId);
    }
}
