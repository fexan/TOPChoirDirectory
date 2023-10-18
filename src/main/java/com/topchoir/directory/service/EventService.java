package com.topchoir.directory.service;

import com.topchoir.directory.domain.Event;
import com.topchoir.directory.repository.EventRepository;
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
public class EventService {
    @Autowired
    private EventRepository repo;

    public EventService() {
        super();
    }

    @Transactional
    public List<Event> getAllEvents(Pageable pageable) {
        return repo.findAll(pageable).toList();
    }

    @Transactional
    public Event getEvent(int id){
        Optional<Event> tempEvent = repo.findById(id);

        if(tempEvent.isEmpty())
            throw new ObjectNotFoundException(tempEvent, "Event with id {"+ id +"} not found");

        return repo.findById(id).get();
    }

    @Transactional
    public Event addEvent(Event event) {
        event.setId(0);
        return repo.save(event);
    }

    @Transactional
    public Event updateEvent( int id, Map<String, Object> event) {

        Optional<Event> tempEvent = repo.findById(id);

        if(tempEvent.isPresent()) {
            event.forEach( (key, value) -> {

                Field field = ReflectionUtils.findField(Event.class, key); //find the field of interest
                ReflectionUtils.makeAccessible(field); //make the field accessible
                ReflectionUtils.setField(field, tempEvent.get(), value); //update the field
            });
        }
        else
            throw new ObjectNotFoundException(tempEvent, "Event with id {"+ id +"} not found");

        return repo.save(tempEvent.get());
    }

    @Transactional
    public void deleteEvent(int id) {
        Optional<Event> tempEvent = repo.findById(id);

        if(tempEvent.isEmpty())
            throw new ObjectNotFoundException(tempEvent, "Event with id {"+ id +"} not found");

        repo.delete(tempEvent.get());
    }

}
