package com.revature.smartAppointment.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.revature.smartAppointment.Controller.AdminAvailabilityController;
import com.revature.smartAppointment.Service.AvailabilityWindowService;
import com.revature.smartAppointment.dto.AvailabilityWindowDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminAvailabilityController.class)
class AdminAvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private AvailabilityWindowService availabilityWindowService;

    public AdminAvailabilityControllerTest(){
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // =====================
    // GET /{doctorId}/availability-windows
    // =====================
    @Test
    void getAvailabilityWindows_returnsDTOList() throws Exception {
        AvailabilityWindowDTO dto = new AvailabilityWindowDTO();
        dto.setWindowId(1);
        dto.setDoctorId(10);
        dto.setActive(true);

        when(availabilityWindowService.getWindowsForDoctor(10))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/admin/doctors/10/availability-windows"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].windowId").value(1))
                .andExpect(jsonPath("$[0].doctorId").value(10))
                .andExpect(jsonPath("$[0].active").value(true));

        verify(availabilityWindowService).getWindowsForDoctor(10);
    }

    // =====================
    // POST /{doctorId}/availability-windows
    // =====================
    @Test
    void createAvailabilityWindow_callsService() throws Exception {
        AdminAvailabilityController.AvailabilityWindowRequest request =
                new AdminAvailabilityController.AvailabilityWindowRequest();
        request.setDate(LocalDate.of(2026, 1, 25));
        request.setStartTime(LocalTime.of(9, 0));
        request.setEndTime(LocalTime.of(10, 0));

        mockMvc.perform(post("/admin/doctors/5/availability-windows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(availabilityWindowService)
                .createWindow(5, request.getDate(), request.getStartTime(), request.getEndTime());
    }
}

