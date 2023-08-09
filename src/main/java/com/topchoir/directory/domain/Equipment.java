package com.topchoir.directory.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Equipment {
    @Column(nullable = true)
    @ManyToMany(cascade= {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "equipment_for_events",
            joinColumns= @JoinColumn(name ="equipment_id"),  //FK of the owning side
            inverseJoinColumns=@JoinColumn(name="event_id")  //FK of inverse side
    )
    @JsonIgnoreProperties("equipment")
    private List<Event> events = new ArrayList<>();

    @Column(nullable = true)
    @ManyToMany(mappedBy= "equipment",
            cascade= {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonIgnoreProperties("equipment")
    private List<Member> handlers = new ArrayList<>();

    @Column(nullable = false)
    private String referenceNumber;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false) //nullable = false is a required field
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    public Equipment() {
    }

    public Equipment(List<Event> events, List<Member> handlers, String referenceNumber, String description, String name) {
        this.events = events;
        this.handlers = handlers;
        this.referenceNumber = referenceNumber;
        this.description = description;
        this.name = name;
    }

    //set up many-to-many relationship Equipment <-> Event
    public void addEvent(Event event) {
        events.add(event);
        //set up bidirectional relationship
        event.getEquipment().add(this);
    }

    //remove oneEquipment
    public void removeEvent(Event event) {
        if (events != null)
            events.remove(event);
        //update bidirectional relationship
        event.getEquipment().remove(this);
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<Member> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<Member> handlers) {
        this.handlers = handlers;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "Equipment{" +
                "events=" + events +
                ", handlers=" + handlers +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
