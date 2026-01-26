package com.revature.smartAppointment.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.smartAppointment.Controller.AdminController;
import com.revature.smartAppointment.Model.Appointment;
import com.revature.smartAppointment.Model.TimeSlot;
import com.revature.smartAppointment.Model.enums.AppointmentStatus;
import com.revature.smartAppointment.Service.admin.AdminAppointmentService;
import com.revature.smartAppointment.Service.admin.AdminScheduleService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper(); // Spring Boot provides ObjectMapper with JavaTimeModule registered

    @MockitoBean
    private AdminAppointmentService adminAppointmentService;

    @MockitoBean
    private AdminScheduleService adminScheduleService;

    @Test
    void getAllAppointments_returnsList() throws Exception {
        Appointment appt = new Appointment();
        appt.setStatus(AppointmentStatus.REQUESTED);

        Mockito.when(adminAppointmentService.getAllAppointments()).thenReturn(List.of(appt));

        mockMvc.perform(get("/smart-appointment/api/admin/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("REQUESTED"));
    }

    @Test
    void acceptAppointment_returnsUpdatedAppointment() throws Exception {
        Appointment appt = new Appointment();
        appt.setStatus(AppointmentStatus.CONFIRMED);

        Mockito.when(adminAppointmentService.updateStatus(eq(1), eq(AppointmentStatus.CONFIRMED))).thenReturn(appt);

        mockMvc.perform(put("/smart-appointment/api/admin/appointments/1/accept"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void cancelAppointment_returnsUpdatedAppointment() throws Exception {
        Appointment appt = new Appointment();
        appt.setStatus(AppointmentStatus.CANCELLED);

        Mockito.when(adminAppointmentService.updateStatus(eq(1), eq(AppointmentStatus.CANCELLED))).thenReturn(appt);

        mockMvc.perform(put("/smart-appointment/api/admin/appointments/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void rescheduleAppointment_returnsUpdatedAppointment() throws Exception {
        LocalDate date = LocalDate.of(2026, 1, 25);
        LocalTime time = LocalTime.of(14, 0);

        Appointment appt = new Appointment();
        appt.setDateTimeScheduled(LocalDateTime.of(date, time));

        Mockito.when(adminAppointmentService.reschedule(eq(1), any(LocalDateTime.class)))
                .thenReturn(appt);

        mockMvc.perform(put("/smart-appointment/api/admin/appointments/1/reschedule")
                        .param("date", date.toString())
                        .param("time", time.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dateTimeScheduled").value("2026-01-25T14:00:00"));
    }

    @Test
    void addSchedule_returnsTimeSlot() throws Exception {
        LocalDate date = LocalDate.of(2026, 1, 26);
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(10, 0);

        TimeSlot slot = new TimeSlot();
        slot.setDateAvailable(date);
        slot.setStartTime(start);
        slot.setEndTime(end);

        Mockito.when(adminScheduleService.addDoctorAvailability(eq(1), eq(date), eq(start), eq(end)))
                .thenReturn(slot);

        mockMvc.perform(post("/smart-appointment/api/admin/doctors/1/schedule")
                        .param("date", date.toString())
                        .param("start", start.toString())
                        .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dateAvailable").value(date.toString()))
                .andExpect(jsonPath("$.startTime").value("09:00:00"))
                .andExpect(jsonPath("$.endTime").value("10:00:00"));
    }
}
