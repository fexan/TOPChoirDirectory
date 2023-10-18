package com.topchoir.directory.resource;

import com.topchoir.directory.domain.Event;
import com.topchoir.directory.domain.Member;
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
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    EventService eventService;

    Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    MemberService memberService;


    @GetMapping
    public List<Event> getAllEvents(Principal principal, @ParameterObject Pageable pageable) {

        Member currentMember = getCurrentMember(principal);

        if (currentMember.getAdminLevel().equals(Member.MemberType.ADMIN)) {
            return eventService.getAllEvents(pageable);
        }else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,"Member does not have the permissions to view this information.");
        }
    }

    @GetMapping("/{id}")
    public Event getEvent(@PathVariable int id) {
        return eventService.getEvent(id);
    }

    @PostMapping
    public Event addEvent(@RequestBody Event event, Principal principal) {

        Member currentMember = getCurrentMember(principal);

        if (currentMember.getAdminLevel().equals(Member.MemberType.ADMIN)) {
            event.setId(0);
            return eventService.addEvent(event);
        }else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,"Member does not have the permissions to perform this operation.");
        }

    }

    @PatchMapping("/{id}")
    public Event updateEvent(@PathVariable int id, @RequestBody Map<String, Object> eventPatch, Principal principal) {

        Member currentMember = getCurrentMember(principal);

        if (currentMember.getAdminLevel().equals(Member.MemberType.ADMIN)) {
            return eventService.updateEvent(id, eventPatch);
        }else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,"Member does not have the permissions to perform this operation.");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable int id, Principal principal) {

        Member currentMember = getCurrentMember(principal);

        if (currentMember.getAdminLevel().equals(Member.MemberType.ADMIN)) {
            eventService.deleteEvent(id);
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