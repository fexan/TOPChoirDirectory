package com.topchoir.directory.resource;

import com.topchoir.directory.domain.Equipment;
import com.topchoir.directory.domain.Event;
import com.topchoir.directory.domain.Member;
import com.topchoir.directory.service.EquipmentService;
import com.topchoir.directory.service.EventService;
import com.topchoir.directory.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    @Autowired
    EquipmentService equipmentService;

    @Autowired
    EventService eventService;

    Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    MemberService memberService;

    @GetMapping
    public List<Equipment> getAllEquipment(Principal principal, @ParameterObject Pageable pageable) {

        Member currentMember = getCurrentMember(principal);

        if (currentMember.getAdminLevel().equals(Member.MemberType.ADMIN)) {
            return equipmentService.getAllEquipment(pageable);
        }else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,"Member does not have the permissions to view this information.");
        }
    }

    @GetMapping("/{id}")
    public Equipment getOneEquipment(@PathVariable int id) {
        return equipmentService.getOneEquipment(id);
    }

    @PostMapping
    public Equipment addOneEquipment(@RequestBody Equipment oneEquipment, Principal principal) {

        Member currentMember = getCurrentMember(principal);

        if (currentMember.getAdminLevel().equals(Member.MemberType.ADMIN)) {
            oneEquipment.setId(0);
            return equipmentService.addOneEquipment(oneEquipment);
        }else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,"Member does not have the permissions to perform this operation.");
        }

    }

    @PatchMapping("/{id}")
    public Equipment updateOneEquipment(@PathVariable int id, @RequestBody Map<String, Object> oneEquipmentPatch, Principal principal) {

        Member currentMember = getCurrentMember(principal);

        if (currentMember.getAdminLevel().equals(Member.MemberType.ADMIN)) {
            return equipmentService.updateOneEquipment(id, oneEquipmentPatch);
        }else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,"Member does not have the permissions to perform this operation.");
        }
    }


    @PutMapping("/{id}/events/{event_id}")
    public Equipment addEvent(@PathVariable int id, @PathVariable int event_id, Principal principal) {

        Member currentMember = getCurrentMember(principal);

        if (currentMember.getAdminLevel().equals(Member.MemberType.ADMIN)) {
            Event event = eventService.getEvent(event_id);
            return equipmentService.addEvent(id, event);
        }else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,"Member does not have the permissions to perform this operation.");
        }

    }

    @PutMapping("/{id}/remove_events/{event_id}")
    public Equipment removeEvent(@PathVariable int id, @PathVariable int event_id, Principal principal) {

        Member currentMember = getCurrentMember(principal);

        if (currentMember.getAdminLevel().equals(Member.MemberType.ADMIN)) {
            Event event = eventService.getEvent(event_id);
            System.out.println(event);
            return equipmentService.removeEvent(id, event);
        }else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,"Member does not have the permissions to perform this operation.");
        }

    }

    @DeleteMapping("/{id}")
    public void deleteOneEquipment(@PathVariable int id,Principal principal) {

        Member currentMember = getCurrentMember(principal);

        if (currentMember.getAdminLevel().equals(Member.MemberType.ADMIN)) {
            equipmentService.deleteOneEquipment(id);
        }else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,"Member does not have the permissions to perform this operation.");
        }

    }

    public Member getCurrentMember(Principal principal) {
        logger.info("Principal: {}", principal);
        logger.info("Principal name: {}", principal.getName());

        String userIdPrincipal = principal.getName().substring(6);
        Member currentMember = memberService.getMemberByUserId(userIdPrincipal);

        return currentMember;
    }
}