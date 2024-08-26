package com.project.CarVoyage.classes.reservation;
import java.util.List;

public interface ReservationRepository {
    void saveReservation(Reservation reservation);
    List<Reservation> getReservationsByUserId(int userId);
}
