package com.project.CarVoyage.classes.car;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcCarRepository implements CarRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

}
