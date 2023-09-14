package com.topchoir.directory.resource;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.topchoir.directory.domain.Event;
import com.topchoir.directory.repository.EventRepository;
import com.topchoir.directory.service.EventService;
import org.hibernate.ObjectNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class EventControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private static final Logger LOG = Logger.getLogger(EventControllerTests.class.toString());

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        Event practice = new Event( null, null, LocalDateTime.of(2023,8,18,21,30,00),LocalDateTime.of(2023,8,18,18,30,00), "Weekly", "Practice");
        Event encounter = new Event( null, null, LocalDateTime.of(2023,10,18,21,30,00),LocalDateTime.of(2023,10,18,18,30,00), "Annual Concert", "Encounter");
        eventRepository.save(practice);
        eventRepository.save(encounter);
    }

    @AfterEach
    public void tearDown() {
        eventRepository.deleteAll();
    }


    @Test
    void getAllEvents() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Practice"))
                .andExpect(jsonPath("$[1].name").value("Encounter"));
    }

    @Test
    void getOneEvent() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/events/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Encounter"));
    }

    @Test
    public void whenValidInput_thenCreateEvent() throws IOException, Exception {

        Event powerShift = new Event( null, null, LocalDateTime.of(2023,8,18,21,30,00),LocalDateTime.of(2023,8,18,18,30,00), "Annual", "Power Shift");

        MvcResult mvcResult =  mockMvc.perform(MockMvcRequestBuilders.post("/api/events")
                        .content(objectMapper.writeValueAsString(powerShift)).contentType("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Annual"))
                .andReturn();;

        assertEquals("application/json", mvcResult.getResponse().getContentType());

    }

    @Test
    void whenUpdateEvent_thenReturnUpdatedEvent() throws Exception {
        Map<String, Object> partialEncounter = new HashMap<String,Object>();
        partialEncounter.put("name","Encounter His Power");
        partialEncounter.put("description","Meet Him, Praise Him, Worship Him, Encounter Him");

        MvcResult mvcResult =  mockMvc.perform(MockMvcRequestBuilders.patch("/api/events/2")
                        .content(objectMapper.writeValueAsString(partialEncounter)).contentType("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Encounter His Power"))
                .andReturn();;

    }

    @Test
    public void whenDeleteEvent_thenReturnException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/events/2"))
                .andExpect(status().isOk());

        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/events/2"));
        }catch (Exception e) {
            assertEquals("Request processing failed: org.hibernate.ObjectNotFoundException: No row with the "
                    + "given identifier exists: [Event with id {2} not found#Optional.empty]",e.getMessage());

        }

    }
}