package com.topchoir.directory.service;

import com.topchoir.directory.domain.Equipment;
import com.topchoir.directory.domain.Event;
import com.topchoir.directory.repository.EquipmentRepository;
import jakarta.transaction.Transactional;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EquipmentService {

    @Autowired
    private EquipmentRepository repo;

    public EquipmentService() {
        super();
    }

    @Transactional
    public List<Equipment> getAllEquipment(Pageable pageable) {
        return repo.findAll(pageable).toList();
    }

    @Transactional
    public Equipment getOneEquipment(int id){
        Optional<Equipment> tempOneEquipment = repo.findById(id);

        if(tempOneEquipment.isEmpty())
            throw new ObjectNotFoundException(tempOneEquipment, "Equipment with id {"+ id +"} not found");

        return repo.findById(id).get();
    }

    @Transactional
    public Equipment addOneEquipment(Equipment oneEquipment) {
        oneEquipment.setId(0);
        return repo.save(oneEquipment);
    }

    @Transactional
    public Equipment updateOneEquipment( int id, Map<String, Object> oneEquipment) {

        Optional<Equipment> tempOneEquipment = repo.findById(id);

        if(tempOneEquipment.isPresent()) {
            oneEquipment.forEach( (key, value) -> {

                Field field = ReflectionUtils.findField(Equipment.class, key); //find the field of interest
                ReflectionUtils.makeAccessible(field); //make the field accessible
                ReflectionUtils.setField(field, tempOneEquipment.get(), value); //update the field
            });
        }
        else
            throw new ObjectNotFoundException(tempOneEquipment, "Equipment with id {"+ id +"} not found");

        return repo.save(tempOneEquipment.get());
    }

    @Transactional
    public Equipment addEvent(int id, Event event) {
        Equipment equipment = repo.findById(id).get();
        equipment.addEvent(event);
        return repo.save(equipment);
    }

    @Transactional
    public Equipment removeEvent(int id, Event event) {
        Equipment equipment = repo.findById(id).get();
        equipment.removeEvent(event);
        return repo.save(equipment);
    }

    @Transactional
    public void deleteOneEquipment(int id) {
        Optional<Equipment> tempOneEquipment = repo.findById(id);

        if(tempOneEquipment.isEmpty())
            throw new ObjectNotFoundException(tempOneEquipment, "Equipment with id {"+ id +"} not found");

        repo.delete(tempOneEquipment.get());
    }
}
