package com.project.CarVoyage.classes.reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

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

    @Override
    public List<Reservation> getReservationsByUserId(int userId) {
        String sql = """
                SELECT r.reservation_id, r.user_id, r.car_id, r.start_date, r.end_date, r.total_amount, r.status,
                       r.created_at, c.make AS car_make, c.model AS car_model
                FROM reservations r
                INNER JOIN cars c ON r.car_id = c.car_id
                WHERE r.user_id = ?
                """;
        return jdbcTemplate.query(sql, new ReservationRowMapper(), userId);
    }
}


