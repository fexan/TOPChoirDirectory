package com.topchoir.directory.service;

import com.topchoir.directory.domain.Event;
import com.topchoir.directory.repository.EventRepository;
import org.hibernate.ObjectNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class EventServiceIntegrationTests {

    private static final Logger LOG = Logger.getLogger(EventServiceIntegrationTests.class.toString());
    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

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
    public void shouldGetAllEvents() {
        List<Event> events = eventService.getAllEvents();
        LOG.info(events.toString());
        assert(events.size() == 2);
    }


    @Test
    public void whenFindById_thenReturnEvent() {
        // given in setUp()


        // when
        Event foundPractice = eventService.getEvent(1);

        // then
        assertThat(foundPractice.getName())
                .isEqualTo("Practice");
    }

    @Test
    public void whenAddEvent_thenReturnAddedEvent() {
        // given
        Event powerShift = new Event( null, null, LocalDateTime.of(2023,8,18,21,30,00),LocalDateTime.of(2023,8,18,18,30,00), "Annual", "Power Shift");

        // when
        Event addedPowerShift = eventService.addEvent(powerShift);

        // then
        assertThat(addedPowerShift.getName())
                .isEqualTo("Power Shift");

        //then all events = 3
        List<Event> allEvents = eventService.getAllEvents();
        LOG.info(allEvents.toString());
        assert(allEvents.size() == 3);
    }

    @Test
    public void whenUpdateEvent_thenReturnUpdatedEvent(){
        //given
        Map<String, Object> partialEncounter = new HashMap<String,Object>();
        partialEncounter.put("name","Encounter His Power");
        partialEncounter.put("description","Meet Him, Praise Him, Worship Him, Encounter Him");

        //when
        Event updatedEncounter = eventService.updateEvent(2,partialEncounter);

        //then
        assertEquals("Encounter His Power",updatedEncounter.getName());
        LOG.info(updatedEncounter.toString());

    }

    @Test
    public void whenDeleteEvent_thenReturnException(){

        eventService.deleteEvent(1);

        try {
            Event deletedEvent = eventService.getEvent(1);
        }catch (ObjectNotFoundException e) {
            assertEquals("No row with the given identifier exists: [Event with id {1} not found#Optional.empty]",e.getMessage());

        }
    }
}

