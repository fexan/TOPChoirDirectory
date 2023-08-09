package com.topchoir.directory.resource;

import com.topchoir.directory.domain.Event;
import com.topchoir.directory.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    EventService service;


    @GetMapping
    public List<Event> getAllEvents() {
        return service.getAllEvents();
    }

    @GetMapping("/{id}")
    public Event getEvent(@PathVariable int id) {
        return service.getEvent(id);
    }

    @PostMapping
    public Event addEvent(@RequestBody Event event) {
        event.setId(0);
        return service.addEvent(event);
    }

    @PatchMapping("/{id}")
    public Event updateEvent(@PathVariable int id, @RequestBody Map<String, Object> eventPatch) {
        return service.updateEvent(id, eventPatch);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable int id) {
        service.deleteEvent(id);
    }
}