package com.topchoir.directory.resource;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.topchoir.directory.domain.Equipment;
import com.topchoir.directory.repository.EquipmentRepository;
import com.topchoir.directory.service.EquipmentService;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class EquipmentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private static final Logger LOG = Logger.getLogger(EquipmentControllerTests.class.toString());

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        Equipment piano = new Equipment( null, null, "R37534-63439", "Yamaha: Perfection", "Piano");
        Equipment frontSpeaker1 = new Equipment( null, null, "X03432-64344", "Bose, Located near instruments", "Front Speaker 1");
        equipmentRepository.save(piano);
        equipmentRepository.save(frontSpeaker1);
    }

    @AfterEach
    public void tearDown() {
        equipmentRepository.deleteAll();
    }


    @Test
    void getAllEquipment() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/equipment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Piano"))
                .andExpect(jsonPath("$[1].name").value("Front Speaker 1"));
    }

    @Test
    void getOneEquipment() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/equipment/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Piano"));
    }

    @Test
    public void whenValidInputThenCreateOneEquipment() throws IOException, Exception {

        Equipment drums = new Equipment( null, null, "X03432-64344", "Yamaha", "Drums");

        MvcResult mvcResult =  mockMvc.perform(MockMvcRequestBuilders.post("/api/equipment")
                        .content(objectMapper.writeValueAsString(drums)).contentType("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceNumber").value("X03432-64344"))
                .andReturn();;

        assertEquals("application/json", mvcResult.getResponse().getContentType());

    }

    @Test
    void whenUpdateOneEquipmentThenReturnUpdatedOneEquipment() throws Exception {
        Map<String, Object> partialPiano = new HashMap<String,Object>();
        partialPiano.put("name","Piano-15678");
        partialPiano.put("referenceNumber","X03762-10864");

        MvcResult mvcResult =  mockMvc.perform(MockMvcRequestBuilders.patch("/api/equipment/1")
                        .content(objectMapper.writeValueAsString(partialPiano)).contentType("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Piano-15678"))
                .andReturn();;

    }

    @Test
    public void whenDeleteOneEquipmentThenReturnException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/equipment/2"))
                .andExpect(status().isOk());

        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/equipment/2"));
        }catch (Exception e) {
            assertEquals("Request processing failed: org.hibernate.ObjectNotFoundException: No row with the "
                    + "given identifier exists: [Equipment with id {2} not found#Optional.empty]",e.getMessage());

        }

    }
}

