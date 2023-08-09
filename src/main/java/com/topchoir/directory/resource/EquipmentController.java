package com.topchoir.directory.resource;

import com.topchoir.directory.domain.Equipment;
import com.topchoir.directory.domain.Event;
import com.topchoir.directory.service.EquipmentService;
import com.topchoir.directory.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    @Autowired
    EquipmentService service;

    @Autowired
    EventService eventService;

    @GetMapping
    public List<Equipment> getAllEquipment() {
        return service.getAllEquipment();
    }

    @GetMapping("/{id}")
    public Equipment getOneEquipment(@PathVariable int id) {
        return service.getOneEquipment(id);
    }

    @PostMapping
    public Equipment addOneEquipment(@RequestBody Equipment oneEquipment) {
        oneEquipment.setId(0);
        return service.addOneEquipment(oneEquipment);
    }

    @PatchMapping("/{id}")
    public Equipment updateOneEquipment(@PathVariable int id, @RequestBody Map<String, Object> oneEquipmentPatch) {
        return service.updateOneEquipment(id, oneEquipmentPatch);
    }


    @PutMapping("/{id}/events/{event_id}")
    public Equipment addEvent(@PathVariable int id, @PathVariable int event_id) {
        Event event = eventService.getEvent(event_id);
        return service.addEvent(id, event);
    }

    @PutMapping("/{id}/remove_events/{event_id}")
    public Equipment removeEvent(@PathVariable int id, @PathVariable int event_id) {
        Event event = eventService.getEvent(event_id);
        System.out.println(event);
        return service.removeEvent(id, event);
    }

    @DeleteMapping("/{id}")
    public void deleteOneEquipment(@PathVariable int id) {
        service.deleteOneEquipment(id);
    }
}