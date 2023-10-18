package com.topchoir.directory.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.topchoir.directory.domain.Equipment;
import com.topchoir.directory.domain.Event;
import com.topchoir.directory.domain.Member;
import com.topchoir.directory.repository.MemberRepository;
import com.topchoir.directory.service.AuthService;
import com.topchoir.directory.service.CustomMemberDetailsService;
import com.topchoir.directory.service.MemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MemberControllerTests {

    private static final Logger LOG = Logger.getLogger(MemberControllerTests.class.toString());

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        Member john0 = new Member(null, null, Member.MemberType.ADMIN, null, "singer - tenor", LocalDate.of(2003, 11, 22),
                "29 St Johns NL", LocalDate.of(1990, 04, 10), "289-864-3880",
                "76ef87f2086f90eo72098004", "pnOPQfgiy63@07!--", "f0_switz@gmail.ca", "Stone", "John0");
        Member jane0 = new Member(null, null, Member.MemberType.MEMBER, null, "singer - soprano", LocalDate.of(2015, 02, 18),
                "12b-3600 Junper Rains Dr ON", LocalDate.of(1995, 05, 29), "383-451-9003",
                "17gh87f8556f90da09834774", "8463ayUIhge@$#&))>", "j0Nelly@yahoo.co.uk", "Nelliers", "Jane0");
        memberRepository.save(john0);
        memberRepository.save(jane0);

        Equipment piano = new Equipment(null, null, "R37534-63439", "Yamaha: Perfection", "Piano");
        Equipment frontSpeaker1 = new Equipment(null, null, "X03432-64344", "Bose, Located near instruments", "Front Speaker 1");

        Event practice = new Event(null, null, LocalDateTime.of(2023, 8, 18, 21, 30, 00), LocalDateTime.of(2023, 8, 18, 18, 30, 00), "Weekly", "Practice");

        john0.setEquipment(Arrays.asList(piano));
        john0.setEquipment(Arrays.asList(frontSpeaker1));
        john0.setEvents(Arrays.asList(practice));

        jane0.setEquipment(Arrays.asList(piano));
        jane0.setEquipment(Arrays.asList(frontSpeaker1));
        jane0.setEvents(Arrays.asList(practice));

        memberRepository.save(john0);
        memberRepository.save(jane0);
    }

    @AfterEach
    public void tearDown() {
        memberRepository.deleteAll();
    }


    @Test
    @WithUserDetails("auth0|76ef87f2086f90eo72098004")
    void AdminRetrievesAllMembersDetails() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4))) //considering the import.sql rows
                .andExpect(jsonPath("$[2].firstName").value("John0"))
                .andExpect(jsonPath("$[3].firstName").value("Jane0"));
    }

    @Test
    @WithUserDetails("auth0|76ef87f2086f90eo72098004")
    void AdminRetrievesOneMemberDetails() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/members/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John0"));
    }

    @Test
    @WithUserDetails("auth0|17gh87f8556f90da09834774")
    void NonAdminAttemptsToRetrieveAdminMemberDetails() throws Exception {
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/members/3"));
        } catch (Exception e) {
            assertEquals("Request processing failed: org.springframework.web.client.HttpClientErrorException: "
                    +"401 Member does not have the permissions to view this information.", e.getMessage());

        }
    }

    @Test
    public void whenValidInputThenCreateMember() throws IOException, Exception {
        Member jones = new Member(null, null, null, null, "singer - alto", LocalDate.of(2015, 02, 18),
                "907b Jarry Junes Blvd, MB", LocalDate.of(1995, 05, 29), "383-451-9003",
                "25ng90f8556e80rt09801629", "72HG2!0Odfuye*^%", "j_Cheerioos9@yahoo.co.uk", "Cherry", "Jones");

        when(authService.getUserId(anyString(), anyString())).thenReturn("25ng90f8556e80rt09801629");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/members")
                        .content(objectMapper.writeValueAsString(jones)).contentType("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Cherry"))
                .andExpect(jsonPath("$.userId").value("25ng90f8556e80rt09801629"))
                .andReturn();
        ;

        assertEquals("application/json", mvcResult.getResponse().getContentType());

    }

    @Test
    @WithUserDetails("auth0|17gh87f8556f90da09834774")
    void whenUpdateMemberThenReturnUpdatedMember() throws Exception {
        Map<String, Object> partialJane = new HashMap<String, Object>();
        partialJane.put("lastName", "Newmann");
        partialJane.put("address", "29 Juniper St SK");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/api/members/4")
                        .content(objectMapper.writeValueAsString(partialJane)).contentType("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Newmann"))
                .andReturn();
        ;

    }

    @Test
    @WithUserDetails("auth0|76ef87f2086f90eo72098004")
    public void whenDeleteMemberThenReturnException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/members/4"))
                .andExpect(status().isOk());

        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/members/4"));
        } catch (Exception e) {
            assertEquals("Request processing failed: org.hibernate.ObjectNotFoundException: No row with the "
                    + "given identifier exists: [Member with id {4} not found#Optional.empty]", e.getMessage());

        }

    }
}

