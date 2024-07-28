package com.project.CarVoyage.classes.car;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository {
    List<Car> findAll();

    Car findById(int carId);

    List<Car> findAllSortedByPriceAsc();

    List<Car> findAllSortedByPriceDesc();

}
