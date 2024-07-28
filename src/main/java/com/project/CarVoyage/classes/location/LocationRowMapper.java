package com.project.CarVoyage.classes.location;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class LocationRowMapper implements RowMapper<Location> {

    @Override
    public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
        Location location = new Location();
        location.setLocationId(rs.getInt("location_id"));
        location.setName(rs.getString("name"));
        location.setAddress(rs.getString("address"));
        location.setEmail(rs.getString("email"));
        location.setPhoneNumber(rs.getString("phone_number"));
        location.setImage(rs.getString("image"));

        return location;
    }
}
