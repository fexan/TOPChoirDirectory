package com.topchoir.directory.service;


import com.topchoir.directory.domain.Equipment;
import com.topchoir.directory.domain.Event;
import com.topchoir.directory.domain.Member;
import com.topchoir.directory.repository.MemberRepository;
import org.hibernate.ObjectNotFoundException;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.After;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MemberServiceIntegrationTests {

    private static final Logger LOG = Logger.getLogger(MemberServiceIntegrationTests.class.toString());
    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        Member john = new Member( null, null, null, null, "singer - tenor", LocalDate.of(2003,11,22),
                "29 St Johns NL", LocalDate.of(1990,04,10), "289-864-3880",
                "auth0|76ef87f2086f90eo72098005","pnOPQfgiy63@07!--","f_switz@gmail.ca", "Stone", "John");
        Member jane = new Member( null, null, null, null, "singer - soprano", LocalDate.of(2015,02,18),
                "12b-3600 Junper Rains Dr ON", LocalDate.of(1995,05,29), "383-451-9003",
                "auth0|17gh87f8556f90da09834775","8463ayUIhge@$#&))>","jNelly@yahoo.co.uk", "Nelliers", "Jane");
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

    }

    @AfterEach
    public void tearDown() {
        memberRepository.deleteAll();
    }

    @Test
    public void shouldGetAllMembers() {
        List<Member> members = memberService.getAllMembers();
        LOG.info(members.toString());
        assert(members.size() == 2);
    }


    @Test
    public void whenFindById_thenReturnMember() {
        // given in setUp()


        // when
        Member foundJohn = memberService.getMember(1);

        // then
        assertThat(foundJohn.getFirstName())
                .isEqualTo("John");
    }

    @Test
    public void whenAddMember_thenReturnAddedMember() {
        // given
        Member jones = new Member( null, null, null, null, "singer - alto", LocalDate.of(2015,02,18),
                "907b Jarry Junes Blvd, MB", LocalDate.of(1995,05,29), "383-451-9003",
                "auth0|25ng90f8556e80rt09801629","72HG2!0Odfuye*^%","j_Cheerioos9@yahoo.co.uk", "Cherry", "Jones");


        // when
        Member addedJones = memberService.addMember(jones);

        // then
        assertThat(addedJones.getFirstName())
                .isEqualTo("Jones");

        //then all members = 3
        List<Member> allMembers = memberService.getAllMembers();
        LOG.info(allMembers.toString());
        assert(allMembers.size() == 3);
    }

    @Test
    public void whenUpdateMember_thenReturnUpdatedMember(){
        //given
        Map<String, Object> partialJane = new HashMap<String,Object>();
        partialJane.put("lastName","Newmann");
        partialJane.put("address","29 Juniper St SK");

        //when
        Member updatedJane = memberService.updateMember(2,partialJane);

        //then
        assertEquals("Newmann",updatedJane.getLastName());
        LOG.info(updatedJane.toString());

    }

    @Test
    public void whenDeleteMember_thenReturnException(){

        memberService.deleteMember(1);

        try {
            Member deletedMember = memberService.getMember(1);
        }catch (ObjectNotFoundException e) {
            assertEquals("No row with the given identifier exists: [Member with id {1} not found#Optional.empty]",e.getMessage());

        }
    }
}