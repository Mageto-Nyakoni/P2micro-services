package com.revature.AppointmentService.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.revature.AppointmentService.client.InfoClient;
import com.revature.AppointmentService.client.ScheduleClient;
import com.revature.AppointmentService.dto.request.BookAppointmentRequestDto;
import com.revature.AppointmentService.dto.response.AppointmentDto;
import com.revature.AppointmentService.dto.response.SlotDto;
import com.revature.AppointmentService.model.Appointment;
import com.revature.AppointmentService.model.AppointmentStatus;
import com.revature.AppointmentService.repository.AppointmentRepository;
import com.revature.AppointmentService.repository.AppointmentTypeRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository apptRepo;
    private final AppointmentTypeRepository typeRepo;
    private final InfoClient infoClient;
    private final ScheduleClient scheduleClient;

    public AppointmentService(AppointmentRepository apptRepo, AppointmentTypeRepository typeRepo, InfoClient infoClient, ScheduleClient scheduleClient) {
        this.apptRepo = apptRepo;
        this.infoClient = infoClient;
        this.typeRepo = typeRepo;
        this.scheduleClient = scheduleClient;
    }
    private AppointmentDto mapToDto(Appointment appointment, SlotDto slot) {

    String doctorName = "Doctor " + appointment.getDoctorId();
    String appointmentType = appointment.getAppointmentTypeId() != null
            ? "Type " + appointment.getAppointmentTypeId()
            : "General";

    return new AppointmentDto(
            appointment.getAppointmentId(),
            doctorName,
            appointmentType,
            slot != null ? slot.getDateTime() : null,
            slot != null ? slot.getDateTime().plusMinutes(30) : null,
            appointment.getStatus()
    );
}
        // BOOK appointment
    @Transactional
    public AppointmentDto bookAppointment(BookAppointmentRequestDto request) {

        if (request.getDoctorId() == null ||
            request.getPatientId() == null ||
            request.getSlotId() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Doctor, Patient and Slot are required");
        }

        //  validate doctor & patient exists (InfoService)
       infoClient.getDoctor(request.getDoctorId());
        infoClient.getPatient(request.getPatientId());

        //get slot from ScheduleService
         SlotDto slot = scheduleClient.getTimeSlot(request.getSlotId());

          if (slot == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Slot not found");
        }

        if (!slot.getDoctorId().equals(request.getDoctorId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Slot does not belong to doctor");
        }

        if (!slot.isAvailable()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Slot not available");
        }

        //book slot in ScheduleService
         scheduleClient.bookSlot(request.getSlotId());
        
        Appointment appointment = new Appointment();
        appointment.setDoctorId(request.getDoctorId().intValue());
        appointment.setPatientId(request.getPatientId().intValue());
        appointment.setSlotId(request.getSlotId().intValue());
        appointment.setAppointmentTypeId(
                request.getAppointmentTypeId() != null
                        ? request.getAppointmentTypeId().intValue()
                        : null);
        appointment.setDateTimeScheduled(slot.getDateTime());
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        Appointment saved = apptRepo.save(appointment);
        return mapToDto(saved, slot);
    }

    //get appointment by id
   public AppointmentDto getById(Integer id) {

        Appointment appointment =
                apptRepo.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Appointment not found"));
        SlotDto slot =
                scheduleClient.getTimeSlot(
                        appointment.getSlotId());

        return mapToDto(appointment, slot);
    }

    //  Get All Appointments
    public List<AppointmentDto> getAll() {
    return apptRepo.findAll()
            .stream()
            .map(appointment ->
                    mapToDto(
                            appointment,
                            scheduleClient.getTimeSlot(
                                    appointment.getSlotId())))
            .collect(Collectors.toList());  
     }


    //Get Appointments For Patient
    public List<AppointmentDto> getForPatient(Integer patientId) {
    return apptRepo.findByPatientId(patientId)
            .stream()
            .map(appointment ->
                    mapToDto(
                            appointment,
                            scheduleClient.getTimeSlot(
                                    appointment.getSlotId())))
            .collect(Collectors.toList());
}
    //  Cancel Appointment
   @Transactional
public void cancelAppointment(Integer appointmentId) {

    Appointment appointment = apptRepo.findById(appointmentId)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Appointment not found"));

   scheduleClient.freeSlot(
        appointment.getSlotId());

    appointment.setStatus(AppointmentStatus.CANCELLED);
    apptRepo.save(appointment);
}

}
