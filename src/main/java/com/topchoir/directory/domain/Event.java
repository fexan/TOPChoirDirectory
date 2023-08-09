package com.topchoir.directory.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Event {

    @Column(nullable = true)
    @ManyToMany(mappedBy= "events",
            cascade= {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonIgnoreProperties("events")
    private List<Equipment> equipment = new ArrayList<>();

    @Column(nullable = true)
    @ManyToMany(mappedBy= "events",
            cascade= {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonIgnoreProperties("events")
    private List<Member> members = new ArrayList<>();

    @Column(nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime endDateTime;

    @Column(nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime startDateTime;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false) //nullable = false is a required field
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    public Event() {
    }

    public Event(List<Equipment> equipment, List<Member> members, LocalDateTime endDateTime, LocalDateTime startDateTime, String description, String name) {
        this.equipment = equipment;
        this.members = members;
        this.endDateTime = endDateTime;
        this.startDateTime = startDateTime;
        this.description = description;
        this.name = name;
    }

    public List<Equipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<Equipment> equipment) {
        this.equipment = equipment;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
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
        return "Event{" +
                "equipment=" + equipment +
                ", members=" + members +
                ", endDateTime=" + endDateTime +
                ", startDateTime=" + startDateTime +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
