package com.project.CarVoyage.classes.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcPaymentRepository implements PaymentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

}
