package com.topchoir.directory.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Member {

    //The absence of the cascade property, results in the TransientPropertyValueException exception when Hibernate tries
    // to save an object containing a nested object

    @Column(nullable = true)
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @Fetch(FetchMode.JOIN)
    @JoinTable(
            name = "members_handling_equipment",
            joinColumns = @JoinColumn(name = "member_id"),  //FK of the owning side
            inverseJoinColumns = @JoinColumn(name = "equipment_id")  //FK of inverse side
    )
    @JsonIgnoreProperties("handlers")
    private List<Equipment> equipment = new ArrayList<>();

    @Column(nullable = true)
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @Fetch(FetchMode.JOIN)
    @JoinTable(
            name = "members_attending_events",
            joinColumns = @JoinColumn(name = "member_id"),  //FK of the owning side
            inverseJoinColumns = @JoinColumn(name = "event_id")  //FK of inverse side
    )
    @JsonIgnoreProperties("members")
    private List<Event> events = new ArrayList<>();

    @Column(nullable = true)
    private String adminLevel;

    @Column(nullable = true)
    private String choirDept;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate joinDate;

    @Column(nullable = true)
    private String address;

    @Column(nullable = true)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate birthday;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false, unique = true)
    private String userId;

    @Transient
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false) //nullable = false is a required field
    private String firstName;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;



    public Member(List<Equipment> equipment, List<Event> events, String adminLevel,
                  String choirDept, String type, LocalDate joinDate, String address,
                  LocalDate birthday, String phoneNumber,String userId, String password,
                  String email, String lastName, String firstName) {
        this.equipment = equipment;
        this.events = events;
        this.adminLevel = adminLevel;
        this.choirDept = choirDept;
        this.type = type;
        this.joinDate = joinDate;
        this.address = address;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;

    }

    public Member() {
    }

    //set up many-to-many relationship Member <-> Equipment
    public void addOneEquipment(Equipment oneEquipment) {
        equipment.add(oneEquipment);
        //set up bidirectional relationship
        oneEquipment.getHandlers().add(this);
    }

    //remove equipment
    public void removeOneEquipment(Equipment oneEquipment) {
        if (equipment != null)
            equipment.remove(oneEquipment);
        //update bidirectional relationship
        oneEquipment.getHandlers().remove(this);
    }

    //set up many-to-many relationship Member <-> Event
    public void addEvent(Event event) {
        events.add(event);
        //set up bidirectional relationship
        event.getMembers().add(this);
    }

    //remove event
    public void removeEvent(Event event) {
        if (events != null)
            events.remove(event);
        //update bidirectional relationship
        event.getMembers().remove(this);
    }

    public List<Equipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<Equipment> equipment) {
        this.equipment = equipment;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }

    public String getChoirDept() {
        return choirDept;
    }

    public void setChoirDept(String choirDept) {
        this.choirDept = choirDept;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Member{" +
                "equipment=" + equipment +
                ", events=" + events +
                ", adminLevel='" + adminLevel + '\'' +
                ", choirDept='" + choirDept + '\'' +
                ", type='" + type + '\'' +
                ", joinDate=" + joinDate +
                ", address='" + address + '\'' +
                ", birthday=" + birthday +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", id=" + id +
                ", userId=" + userId +
                '}';
    }


}

