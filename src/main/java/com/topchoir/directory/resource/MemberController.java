package com.topchoir.directory.resource;

import java.util.List;
import java.util.Map;

import com.topchoir.directory.domain.Equipment;
import com.topchoir.directory.domain.Event;
import com.topchoir.directory.domain.Member;
import com.topchoir.directory.service.EquipmentService;
import com.topchoir.directory.service.EventService;
import com.topchoir.directory.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
public class MemberController {

    @Autowired
    MemberService service;

    @Autowired
    EquipmentService equipmentService;

    @Autowired
    EventService eventService;

    @GetMapping
    public List<Member> getAllMembers() {
        return service.getAllMembers();
    }

    @GetMapping("/{id}")
    public Member getMember(@PathVariable int id){
        return service.getMember(id);
    }

    @PostMapping
    public Member addMember(@RequestBody Member member) {
        member.setId(0);
        return service.addMember(member);
    }

    @PatchMapping("/{id}")
    public Member updateMember(@PathVariable int id, @RequestBody Map<String, Object> memberPatch) {
        return service.updateMember(id, memberPatch);
    }

    @PutMapping("/{id}/equipment/{equipment_id}")
    public Member addOneEquipment(@PathVariable int id, @PathVariable int equipment_id) {
        Equipment oneEquipment = equipmentService.getOneEquipment(equipment_id);
        System.out.println(oneEquipment);
        return service.addOneEquipment(id, oneEquipment);
    }

    @PutMapping("/{id}/remove_equipment/{equipment_id}")
    public Member removeOneEquipment(@PathVariable int id, @PathVariable int equipment_id) {
        Equipment oneEquipment = equipmentService.getOneEquipment(equipment_id);
        System.out.println(oneEquipment);
        return service.removeOneEquipment(id, oneEquipment);
    }


    @PutMapping("/{id}/events/{event_id}")
    public Member addEvent(@PathVariable int id, @PathVariable int event_id) {
        Event event = eventService.getEvent(event_id);
        return service.addEvent(id, event);
    }

    @PutMapping("/{id}/remove_events/{event_id}")
    public Member removeEvent(@PathVariable int id, @PathVariable int event_id) {
        Event event = eventService.getEvent(event_id);
        System.out.println(event);
        return service.removeEvent(id, event);
    }

    @DeleteMapping("/{id}")
    public void deleteMember(@PathVariable int id) {
        service.deleteMember(id);
    }
}
