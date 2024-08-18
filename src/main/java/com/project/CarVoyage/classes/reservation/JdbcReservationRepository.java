package com.project.CarVoyage.classes.reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcReservationRepository implements ReservationRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void saveReservation(Reservation reservation) {
        String sql = "INSERT INTO reservations (user_id, car_id, start_date, end_date, total_amount, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, reservation.getUserId(), reservation.getCarId(), reservation.getStartDate(),
                reservation.getEndDate(), reservation.getTotalAmount(), reservation.getStatus());
    }
}


