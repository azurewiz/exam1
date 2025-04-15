package com.example.student_app;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private StudentService studentService;

    public StudentService getStudentService() {
        return studentService;
    }

    public void setStudentService(StudentService studentService) {
        this.studentService = studentService;
    }

    @Autowired
    private ObjectMapper objectMapper;

    private Student student1;
    private Student student2;

    @BeforeEach
    public void setup() {
        student1 = new Student(1L, "rohit rai", "rohit@gmail.com", 20);
        student2 = new Student(2L, "Sai Arvind Arun", "saiarvind@gmail.com", 22);
    }

    @Test
    public void testGetAllStudents() throws Exception {
        given(studentService.getAllStudents()).willReturn(Arrays.asList(student1, student2));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("rohit rai"))
                .andExpect(jsonPath("$[1].name").value("Sai Arvind Arun"));
    }

    @Test
    public void testGetStudentById() throws Exception {
        given(studentService.getStudentById(1L)).willReturn(Optional.of(student1));

        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("rohit rai"));
    }

    @Test
    public static void testGetStudentById_NotFound(StudentControllerTest studentControllerTest) throws Exception {
        given(studentControllerTest.studentService.getStudentById(anyLong())).willReturn(Optional.empty());

        studentControllerTest.mockMvc.perform(get("/api/students/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateStudent() throws Exception {
        given(studentService.createStudent(any(Student.class))).willReturn(student1);

        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("rohit rai"));
    }

    @Test
    public void testUpdateStudent() throws Exception {
        given(studentService.updateStudent(anyLong(), any(Student.class))).willReturn(student1);

        mockMvc.perform(put("/api/students/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("rohit rai"));
    }

    @Test
    public void testDeleteStudent() throws Exception {
        doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(delete("/api/students/1"))
                .andExpect(status().isNoContent());
    }
}
