package com.topchoir.directory.service;

import com.topchoir.directory.domain.Equipment;
import com.topchoir.directory.domain.Event;
import com.topchoir.directory.domain.Member;
import com.topchoir.directory.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository repo;

    public MemberService() {
        super();
    }

    @Transactional
    public List<Member> getAllMembers() {
        return repo.findAll();
    }

    @Transactional
    public Member getMember(int id) {
        Optional<Member> tempMember = repo.findById(id);

        if (tempMember.isEmpty())
            throw new ObjectNotFoundException(tempMember, "Member with id {" + id + "} not found");

        return repo.findById(id).get();
    }

    @Transactional
    public Member addMember(Member member) {
        member.setId(0);
        return repo.save(member);
    }

    @Transactional
    public Member updateMember(int id, Map<String, Object> partialMember) {

        Optional<Member> tempMember = repo.findById(id);

        if (tempMember.isPresent()) {
            partialMember.forEach((key, value) -> {

                Field field = ReflectionUtils.findField(Member.class, key); //find the field of interest
                ReflectionUtils.makeAccessible(field); //make the field accessible
                ReflectionUtils.setField(field, tempMember.get(), value); //update the field
            });
        } else
            throw new ObjectNotFoundException(tempMember, "Member with id {" + id + "} not found");

        return repo.save(tempMember.get());
    }

    @Transactional
    public void deleteMember(int id) {
        Optional<Member> tempMember = repo.findById(id);

        if (tempMember.isEmpty())
            throw new ObjectNotFoundException(tempMember, "Member with id {" + id + "} not found");

        repo.delete(tempMember.get());
    }

    @Transactional
    public Member addOneEquipment(int id, Equipment oneEquipment) {
        Member member = repo.findById(id).get();
        member.addOneEquipment(oneEquipment);
        return repo.save(member);
    }

    @Transactional
    public Member removeOneEquipment(int id, Equipment oneEquipment) {
        Member member = repo.findById(id).get();
        member.removeOneEquipment(oneEquipment);
        return repo.save(member);
    }

    @Transactional
    public Member addEvent(int id, Event event) {
        Member member = repo.findById(id).get();
        member.addEvent(event);
        return repo.save(member);
    }

    @Transactional
    public Member removeEvent(int id, Event event) {
        Member member = repo.findById(id).get();
        member.removeEvent(event);
        return repo.save(member);
    }
}
