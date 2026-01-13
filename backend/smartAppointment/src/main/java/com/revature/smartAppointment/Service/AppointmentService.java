package com.revature.smartAppointment.Service;

import java.util.List;
import java.util.Optional;

import com.revature.smartAppointment.Model.Appointment;

public class AppointmentService implements ServiceInterface<Appointment> {

    @Override
    public Appointment save(Appointment entity) {
        return null;
    }

    @Override
    public Optional<Appointment> findById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Appointment> findAll() {
        return List.of();
    }

    @Override
    public Optional<Appointment> deleteById(int id) {
        return Optional.empty();
    }

    @Override
    public Appointment updateById(int id, Appointment entity) {
        return null;
    }
}