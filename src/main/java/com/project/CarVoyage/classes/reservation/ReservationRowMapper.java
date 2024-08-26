package com.project.CarVoyage.classes.reservation;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class ReservationRowMapper implements RowMapper<Reservation> {

    @Override
    public Reservation mapRow(ResultSet rs, int rowNum) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setReservationId(rs.getInt("reservation_id"));
        reservation.setUserId(rs.getInt("user_id"));
        reservation.setCarId(rs.getInt("car_id"));
        reservation.setStartDate(rs.getDate("start_date"));
        reservation.setEndDate(rs.getDate("end_date"));
        reservation.setTotalAmount(rs.getDouble("total_amount"));
        reservation.setStatus(rs.getString("status"));
        reservation.setCreatedAt(rs.getTimestamp("created_at"));

        //ovo sam naknadno ubacio u reservation klasu jer sam morao prikazati na userprofile koji auto je korisnik rezervirao
        reservation.setCarMake(rs.getString("car_make"));
        reservation.setCarModel(rs.getString("car_model"));

        return reservation;
    }
}
