// package com.revature.smartAppointment.ServiceTest;

// import com.revature.smartAppointment.Model.Appointment;
// import com.revature.smartAppointment.Model.TimeSlot;
// import com.revature.smartAppointment.Model.Doctor;
// import com.revature.smartAppointment.Model.Speciality;
// import com.revature.smartAppointment.Model.User;
// import com.revature.smartAppointment.Model.enums.AppointmentStatus;
// import com.revature.smartAppointment.Repository.AppointmentRepository;
// import com.revature.smartAppointment.Repository.DoctorRepository;
// import com.revature.smartAppointment.Repository.TimeSlotRepository;
// import com.revature.smartAppointment.Service.DoctorService;
// import com.revature.smartAppointment.Service.DoctorService.DoctorAppointmentView;
// import com.revature.smartAppointment.Service.DoctorService.DoctorTimeSlotView;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.web.server.ResponseStatusException;

// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.time.LocalTime;
// import java.util.List;
// import java.util.Optional;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.assertj.core.api.Assertions.assertThatThrownBy;
// import static org.mockito.BDDMockito.given;
// import static org.mockito.BDDMockito.then;
// import static org.mockito.Mockito.never;

// @ExtendWith(MockitoExtension.class)
// class DoctorServiceTest {

//     @Mock
//     private DoctorRepository doctorRepository;

//     @Mock
//     private AppointmentRepository appointmentRepository;

//     @Mock
//     private TimeSlotRepository timeSlotRepository;

//     @InjectMocks
//     private DoctorService doctorService;

//     // -----------------------------
//     // CRUD behaviour
//     // -----------------------------

//     @Test
//     void saveDoctor_delegatesToRepository() {
//         Doctor doctor = Doctor.builder().doctorId(null).build();
//         given(doctorRepository.save(doctor)).willReturn(doctor);

//         Doctor result = doctorService.save(doctor);

//         assertThat(result).isSameAs(doctor);
//         then(doctorRepository).should().save(doctor);
//     }

//     @Test
//     void findById_returnsDoctor_whenPresent() {
//         Doctor doctor = Doctor.builder().doctorId(1).build();
//         given(doctorRepository.findById(1)).willReturn(Optional.of(doctor));

//         Optional<Doctor> result = doctorService.findById(1);

//         assertThat(result).isPresent();
//         assertThat(result.get()).isSameAs(doctor);
//     }

//     @Test
//     void findById_returnsEmpty_whenNotPresent() {
//         given(doctorRepository.findById(1)).willReturn(Optional.empty());

//         Optional<Doctor> result = doctorService.findById(1);

//         assertThat(result).isEmpty();
//     }

//     @Test
//     void findAll_returnsAllDoctors() {
//         Doctor d1 = Doctor.builder().doctorId(1).build();
//         Doctor d2 = Doctor.builder().doctorId(2).build();
//         given(doctorRepository.findAll()).willReturn(List.of(d1, d2));

//         List<Doctor> result = doctorService.findAll();

//         assertThat(result).containsExactly(d1, d2);
//     }

//     @Test
//     void deleteById_deletesAndReturnsDoctor_whenPresent() {
//         Doctor doctor = Doctor.builder().doctorId(1).build();
//         given(doctorRepository.findById(1)).willReturn(Optional.of(doctor));

//         Optional<Doctor> result = doctorService.deleteById(1);

//         assertThat(result).isPresent();
//         assertThat(result.get()).isSameAs(doctor);
//         then(doctorRepository).should().deleteById(1);
//     }

//     @Test
//     void deleteById_returnsEmpty_whenNotPresent() {
//         given(doctorRepository.findById(1)).willReturn(Optional.empty());

//         Optional<Doctor> result = doctorService.deleteById(1);

//         assertThat(result).isEmpty();
//         then(doctorRepository).shouldHaveNoMoreInteractions();
//     }

    @Test
    void updateById_updatesFields_whenDoctorExists() {
        User existingUser = new User();
        Speciality existingSpeciality = new Speciality(1, "Old", "desc");
        Doctor existing = Doctor.builder()
                .doctorId(1)
                .user(existingUser)
                .experienceYears(5)
                .gender("M")
                .speciality(existingSpeciality)
                .bio("Old bio")
                .build();
//     @Test
//     void updateById_updatesFields_whenDoctorExists() {
//         User existingUser = new User();
//         Speciality existingSpeciality = new Speciality(1, "Old", "desc");
//         Doctor existing = Doctor.builder()
//                 .doctorId(1)
//                 .user(existingUser)
//                 .experienceYears(5)
//                 .gender("M")
//                 .speciality(existingSpeciality)
//                 .bio("Old bio")
//                 .build();

//         User newUser = new User();
//         Speciality newSpeciality = new Speciality(2, "New", "desc2");
//         Doctor updatedPayload = Doctor.builder()
//                 .user(newUser)
//                 .experienceYears(10)
//                 .gender("F")
//                 .speciality(newSpeciality)
//                 .bio("New bio")
//                 .build();

//         given(doctorRepository.findById(1)).willReturn(Optional.of(existing));
//         given(doctorRepository.save(existing)).willReturn(existing);

//         Doctor result = doctorService.updateById(1, updatedPayload);

//         assertThat(result.getUser()).isSameAs(newUser);
//         assertThat(result.getExperienceYears()).isEqualTo(10);
//         assertThat(result.getGender()).isEqualTo("F");
//         assertThat(result.getSpeciality()).isSameAs(newSpeciality);
//         assertThat(result.getBio()).isEqualTo("New bio");
//         then(doctorRepository).should().save(existing);
//     }

//     @Test
//     void updateById_returnsNull_whenDoctorDoesNotExist() {
//         Doctor updatedPayload = Doctor.builder().build();
//         given(doctorRepository.findById(1)).willReturn(Optional.empty());

//         Doctor result = doctorService.updateById(1, updatedPayload);

//         assertThat(result).isNull();
//         then(doctorRepository).should().findById(1);
//     }

//     @Test
//     void getTodaysAppointments_returnsMappedViews_whenAppointmentsExist() {
//         Integer doctorId = 1;
//         LocalDate today = LocalDate.now();
//         LocalDateTime start = today.atStartOfDay();
//         LocalDateTime end = today.atTime(LocalTime.MAX);

//         Appointment appt = Appointment.builder()
//                 .appointmentId(10)
//                 .dateTimeScheduled(start.plusHours(2))
//                 .status(AppointmentStatus.REQUESTED)
//                 .build();

//         Doctor doctor = Doctor.builder().doctorId(doctorId).build();
//         given(doctorRepository.findById(doctorId)).willReturn(Optional.of(doctor)); // existence check
//         given(appointmentRepository.findByDoctorDoctorIdAndDateTimeScheduledBetween(doctorId, start, end))
//                 .willReturn(List.of(appt));

//         List<DoctorAppointmentView> result = doctorService.getTodaysAppointments(doctorId);

//         assertThat(result).hasSize(1);
//         DoctorAppointmentView view = result.get(0);
//         assertThat(view.getAppointmentId()).isEqualTo(10);
//         assertThat(view.getScheduledDateTime()).isEqualTo(appt.getDateTimeScheduled());
//         assertThat(view.getStatus()).isEqualTo(AppointmentStatus.REQUESTED);

//         then(appointmentRepository)
//                 .should()
//                 .findByDoctorDoctorIdAndDateTimeScheduledBetween(doctorId, start, end);
//     }

//     @Test
//     void getCurrentWeeksAppointments_usesWeekRange_andReturnsViews() {
//         Integer doctorId = 2;

//         LocalDate today = LocalDate.now();
//         LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
//         LocalDate weekEnd = weekStart.plusDays(6);

//         LocalDateTime start = weekStart.atStartOfDay();
//         LocalDateTime end = weekEnd.atTime(LocalTime.MAX);

//         Appointment appt = Appointment.builder()
//                 .appointmentId(20)
//                 .dateTimeScheduled(start.plusDays(1))
//                 .status(AppointmentStatus.CONFIRMED)
//                 .build();

//         Doctor doctor = Doctor.builder().doctorId(doctorId).build();
//         given(doctorRepository.findById(doctorId)).willReturn(Optional.of(doctor));
//         given(appointmentRepository.findByDoctorDoctorIdAndDateTimeScheduledBetween(doctorId, start, end))
//                 .willReturn(List.of(appt));

//         List<DoctorAppointmentView> result = doctorService.getCurrentWeeksAppointments(doctorId);

//         assertThat(result).hasSize(1);
//         assertThat(result.get(0).getAppointmentId()).isEqualTo(20);
//         assertThat(result.get(0).getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
//     }

//     @Test
//     void getAppointmentDetailsForDoctor_throwsNotFound_whenAppointmentMissing() {
//         Integer doctorId = 1;
//         Integer appointmentId = 99;

//         given(appointmentRepository.findById(appointmentId)).willReturn(Optional.empty());

//         assertThatThrownBy(() -> doctorService.getAppointmentDetailsForDoctor(doctorId, appointmentId))
//                 .isInstanceOf(ResponseStatusException.class)
//                 .hasMessageContaining("Appointment not found");
//     }

//     @Test
//     void updateAppointmentStatus_updatesStatusAndPersists() {
//         Integer doctorId = 1;
//         Integer appointmentId = 5;

//         Appointment existing = Appointment.builder()
//                 .appointmentId(appointmentId)
//                 .status(AppointmentStatus.REQUESTED)
//                 .build();

//         given(appointmentRepository.findById(appointmentId)).willReturn(Optional.of(existing));
//         given(appointmentRepository.save(existing)).willAnswer(invocation -> invocation.getArgument(0));

//         DoctorAppointmentView view = doctorService.updateAppointmentStatus(doctorId, appointmentId,
//                 AppointmentStatus.CANCELLED);

//         assertThat(view.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
//         then(appointmentRepository).should().save(existing);
//     }

//     @Test
//     void cancelAppointment_delegatesToUpdateAppointmentStatus() {
//         Integer doctorId = 3;
//         Integer appointmentId = 7;

//         Appointment existing = Appointment.builder()
//                 .appointmentId(appointmentId)
//                 .status(AppointmentStatus.CONFIRMED)
//                 .build();

//         given(appointmentRepository.findById(appointmentId)).willReturn(Optional.of(existing));
//         given(appointmentRepository.save(existing)).willAnswer(invocation -> invocation.getArgument(0));

//         DoctorAppointmentView view = doctorService.cancelAppointment(doctorId, appointmentId);

//         assertThat(view.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
//         then(appointmentRepository).should().save(existing);
//     }

//     @Test
//     void getDoctorTimeSlots_returnsMappedViews() {
//         Integer doctorId = 4;

//         LocalDateTime startTime = LocalDateTime.now().plusDays(1);
//         LocalDateTime endTime = startTime.plusHours(1);

//         TimeSlot slot = TimeSlot.builder()
//                 .slotId(11)
//                 .startTime(startTime)
//                 .endTime(endTime)
//                 .build();

//         Doctor doctor = Doctor.builder().doctorId(doctorId).build();
//         given(doctorRepository.findById(doctorId)).willReturn(Optional.of(doctor));
//         given(timeSlotRepository.findByDoctorDoctorId(doctorId)).willReturn(List.of(slot));

//         List<DoctorTimeSlotView> result = doctorService.getDoctorTimeSlots(doctorId);

//         assertThat(result).hasSize(1);
//         DoctorTimeSlotView view = result.get(0);
//         assertThat(view.getSlotId()).isEqualTo(11);
//         assertThat(view.getStartTime()).isEqualTo(startTime);
//         assertThat(view.getEndTime()).isEqualTo(endTime);
//     }

//     @Test
//     void getDoctorTimeSlots_doesNotQuerySlots_whenDoctorDoesNotExist() {
//         Integer doctorId = 99;

//         given(doctorRepository.findById(doctorId)).willReturn(Optional.empty());

//         assertThatThrownBy(() -> doctorService.getDoctorTimeSlots(doctorId))
//                 .isInstanceOf(ResponseStatusException.class)
//                 .hasMessageContaining("Doctor not found");

//         then(timeSlotRepository).should(never()).findByDoctorDoctorId(doctorId);
//     }
// }
