package com.topchoir.directory.resource;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.topchoir.directory.domain.Equipment;
import com.topchoir.directory.domain.Event;
import com.topchoir.directory.domain.Member;
import com.topchoir.directory.repository.MemberRepository;
import com.topchoir.directory.service.MemberService;
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MemberControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private static final Logger LOG = Logger.getLogger(MemberControllerTests.class.toString());

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        Member john = new Member( null, null, null, null, "singer - tenor", LocalDate.of(2003,11,22), "29 St Johns NL", LocalDate.of(1990,04,10), "289-864-3880", "f_switz@gmail.ca", "Stone", "John");
        Member jane = new Member( null, null, null, null, "singer - soprano", LocalDate.of(2015,02,18), "12b-3600 Junper Rains Dr ON", LocalDate.of(1995,05,29), "383-451-9003", "jNelly@yahoo.co.uk", "Nelliers", "Jane");
        memberRepository.save(john);
        memberRepository.save(jane);

        Equipment piano = new Equipment( null, null, "R37534-63439", "Yamaha: Perfection", "Piano");
        Equipment frontSpeaker1 = new Equipment( null, null, "X03432-64344", "Bose, Located near instruments", "Front Speaker 1");

        Event practice = new Event( null, null, LocalDateTime.of(2023,8,18,21,30,00),LocalDateTime.of(2023,8,18,18,30,00), "Weekly", "Practice");

        john.setEquipment(Arrays.asList(piano));
        john.setEquipment(Arrays.asList(frontSpeaker1));
        john.setEvents(Arrays.asList(practice));

        jane.setEquipment(Arrays.asList(piano));
        jane.setEquipment(Arrays.asList(frontSpeaker1));
        jane.setEvents(Arrays.asList(practice));

        memberRepository.save(john);
        memberRepository.save(jane);
    }

    @After
    public void tearDown() {
        memberRepository.deleteAll();
    }


    @Test
    @Order(1)
    void getAllMembers() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    @Order(2)
    void getOneMember() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @Order(3)
    public void whenValidInput_thenCreateEmployee() throws IOException, Exception {

        Member jones = new Member( null, null, null, null, "singer - alto", LocalDate.of(2015,02,18), "907b Jarry Junes Blvd, MB", LocalDate.of(1995,05,29), "383-451-9003", "j_Cheerioos9@yahoo.co.uk", "Cherry", "Jones");

        MvcResult mvcResult =  mockMvc.perform(MockMvcRequestBuilders.post("/members")
                        .content(objectMapper.writeValueAsString(jones)).contentType("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Cherry"))
                .andReturn();;

        assertEquals("application/json", mvcResult.getResponse().getContentType());

    }

    @Test
    @Order(4)
    void whenUpdateMember_thenReturnUpdatedMember() throws Exception {
        Map<String, Object> partialJane = new HashMap<String,Object>();
        partialJane.put("lastName","Newmann");
        partialJane.put("address","29 Juniper St SK");

        MvcResult mvcResult =  mockMvc.perform(MockMvcRequestBuilders.patch("/members/2")
                        .content(objectMapper.writeValueAsString(partialJane)).contentType("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Newmann"))
                .andReturn();;

    }

    @Test
    @Order(5)
    public void whenDeleteMember_thenReturnException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/members/2"))
                .andExpect(status().isOk());

        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/members/2"));
        }catch (Exception e) {
            assertEquals("Request processing failed: org.hibernate.ObjectNotFoundException: No row with the "
                    + "given identifier exists: [Member with id {2} not found#Optional.empty]",e.getMessage());

        }

    }
}

