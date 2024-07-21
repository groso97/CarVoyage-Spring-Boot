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

}
