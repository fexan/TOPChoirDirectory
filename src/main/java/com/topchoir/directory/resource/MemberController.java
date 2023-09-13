package com.topchoir.directory.resource;

import com.auth0.client.auth.AuthAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.net.SignUpRequest;
import com.topchoir.directory.domain.Equipment;
import com.topchoir.directory.domain.Event;
import com.topchoir.directory.domain.Member;
import com.topchoir.directory.service.EquipmentService;
import com.topchoir.directory.service.EventService;
import com.topchoir.directory.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    MemberService service;
    @Autowired
    EquipmentService equipmentService;
    @Autowired
    EventService eventService;
    @Value("${auth0.domain}")
    private String domain;
    @Value("${auth0.client-id}")
    private String clientId;
    @Value("${auth0.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;
    @Value("${auth0.clinet.realm}")
    private String realm;

    @GetMapping
    public List<Member> getAllMembers() {
        return service.getAllMembers();
    }

    @GetMapping("/{id}")
    public Member getMember(@PathVariable int id) {
        return service.getMember(id);
    }

    @PostMapping
    public Member addMember(@RequestBody Member member) throws Auth0Exception {
        // signup new auth0 user
        AuthAPI auth = AuthAPI.newBuilder(domain, clientId, clientSecret).build();
        SignUpRequest signUp = auth.signUp(member.getEmail(), member.getPassword().toCharArray(), realm);
        String userId = signUp.execute().getBody().getUserId();
        logger.info("New user created with id: " + userId + " and email " + member.getEmail());

        member.setId(0);
        member.setUserId(userId);
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
