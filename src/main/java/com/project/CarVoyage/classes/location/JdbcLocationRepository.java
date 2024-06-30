package com.project.CarVoyage.classes.location;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcLocationRepository implements LocationRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

}
