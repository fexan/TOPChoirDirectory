package com.topchoir.directory.resource;

import com.auth0.exception.Auth0Exception;
import com.topchoir.directory.domain.Equipment;
import com.topchoir.directory.domain.Event;
import com.topchoir.directory.domain.Member;
import com.topchoir.directory.service.AuthService;
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
@RequestMapping("/api/members")
public class MemberController {
    Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    MemberService service;

    @Autowired
    AuthService authService;

    @Autowired
    EquipmentService equipmentService;

    @Autowired
    EventService eventService;


    @GetMapping
    public List<Member> getAllMembers(Principal principal, @ParameterObject Pageable pageable) {

        List<Member> allMembers = service.getAllMembers(pageable);
        Member currentMember = getCurrentMember(principal);

        if (currentMember.getAdminLevel().equals(Member.MemberType.ADMIN)) {
            return allMembers;
        }else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,"Member does not have the permissions to view this information.");
        }

    }

    /**
     * Signed in admin can retrieve details for admins (including itself) and non-admin
     * Signed in non-admin can retrieve details for only itself
     * @param id
     * @param principal -- Spring security feature. Represents the current signed in user.
     * @return Member with specified {id}
     */
    @GetMapping("/{id}")
    public Member getMember(@PathVariable int id, Principal principal) {
        Member currentMember = getCurrentMember(principal);

        if (currentMember.getAdminLevel().equals(Member.MemberType.ADMIN) || currentMember.getId() == id )
            return service.getMember(id);

        throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,"Member does not have the permissions to view this information.");

    }


    @PostMapping
    public Member addMember(@RequestBody Member member) throws Auth0Exception {
        String userId = authService.getUserId(member.getEmail(), member.getPassword());

        member.setId(0);
        member.setUserId(userId);
        return service.addMember(member);
    }

    /**
     * Signed in admin can update details of admins (including itself) and non-admin
     * Signed in non-admin can update details of only itself
     * @param id
     * @param memberPatch
     * @param principal
     * @return
     */
    @PatchMapping("/{id}")
    public Member updateMember(@PathVariable int id, @RequestBody Map<String, Object> memberPatch, Principal principal) {
        Member currentMember = getCurrentMember(principal);

        if (currentMember.getAdminLevel().equals(Member.MemberType.ADMIN) || currentMember.getId() == id )
            return service.updateMember(id, memberPatch);

        throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,"Member does not have the permissions to perform this operation.");


    }

    @PutMapping("/{id}/equipment/{equipment_id}")
    public Member addOneEquipment(@PathVariable int id, @PathVariable int equipment_id, Principal principal) {
        Equipment oneEquipment = equipmentService.getOneEquipment(equipment_id);
        System.out.println(oneEquipment);

        Member currentMember = getCurrentMember(principal);

        if (currentMember.getAdminLevel().equals(Member.MemberType.ADMIN)) {
            return service.addOneEquipment(id, oneEquipment);
        }else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,"Member does not have the permissions to perform this operation.");
        }
    }

    @PutMapping("/{id}/remove_equipment/{equipment_id}")
    public Member removeOneEquipment(@PathVariable int id, @PathVariable int equipment_id, Principal principal) {
        Equipment oneEquipment = equipmentService.getOneEquipment(equipment_id);
        System.out.println(oneEquipment);

        Member currentMember = getCurrentMember(principal);

        if (currentMember.getAdminLevel().equals(Member.MemberType.ADMIN)) {
            return service.removeOneEquipment(id, oneEquipment);
        }else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,"Member does not have the permissions to perform this operation.");
        }
    }


    @PutMapping("/{id}/events/{event_id}")
    public Member addEvent(@PathVariable int id, @PathVariable int event_id, Principal principal) {
        Event event = eventService.getEvent(event_id);

        Member currentMember = getCurrentMember(principal);

        if (currentMember.getAdminLevel().equals(Member.MemberType.ADMIN)) {
            return service.addEvent(id, event);
        }else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,"Member does not have the permissions to perform this operation.");
        }
    }

    @PutMapping("/{id}/remove_events/{event_id}")
    public Member removeEvent(@PathVariable int id, @PathVariable int event_id, Principal principal) {

        Event event = eventService.getEvent(event_id);
        System.out.println(event);

        Member currentMember = getCurrentMember(principal);

        if (currentMember.getAdminLevel().equals(Member.MemberType.ADMIN)) {
            return service.removeEvent(id, event);
        }else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,"Member does not have the permissions to perform this operation.");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteMember(@PathVariable int id, Principal principal) {

        Member currentMember = getCurrentMember(principal);

        if (currentMember.getAdminLevel().equals(Member.MemberType.ADMIN)) {
            service.deleteMember(id);
        }else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,"Member does not have the permissions to perform this operation.");
        }
    }

    public Member getCurrentMember(Principal principal) {
        logger.info("Principal: {}", principal);
        logger.info("Principal name: {}", principal.getName());

        String userIdPrincipal = principal.getName().substring(6);
        Member currentMember = service.getMemberByUserId(userIdPrincipal);

        return currentMember;
    }
}
