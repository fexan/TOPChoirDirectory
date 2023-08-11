package com.topchoir.directory.service;

import com.topchoir.directory.domain.Equipment;
import com.topchoir.directory.repository.EquipmentRepository;
import org.hibernate.ObjectNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EquipmentServiceIntegrationTests {

    private static final Logger LOG = Logger.getLogger(EquipmentServiceIntegrationTests.class.toString());
    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Before
    public void setUp() {
        Equipment piano = new Equipment( null, null, "R37534-63439", "Yamaha: Perfection", "Piano");
        Equipment frontSpeaker1 = new Equipment( null, null, "X03432-64344", "Bose, Located near instruments", "Front Speaker 1");
        equipmentRepository.save(piano);
        equipmentRepository.save(frontSpeaker1);
    }

    @After
    public void tearDown() {
        equipmentRepository.deleteAll();
    }

    @Test
    @Order(1)
    public void shouldGetAllEquipment() {
        List<Equipment> equipment = equipmentService.getAllEquipment();
        LOG.info(equipment.toString());
        assert(equipment.size() == 2);
    }


    @Test
    @Order(2)
    public void whenFindById_thenReturnOneEquipment() {
        // given in setUp()


        // when
        Equipment foundPiano = equipmentService.getOneEquipment(1);

        // then
        assertThat(foundPiano.getName())
                .isEqualTo("Piano");
    }

    @Test
    @Order(3)
    public void whenAddOneEquipment_thenReturnAddedOneEquipment() {
        // given
        Equipment drums = new Equipment( null, null, "X03432-64344", "Yamaha", "Drums");

        // when
        Equipment addedDrums = equipmentService.addOneEquipment(drums);

        // then
        assertThat(addedDrums.getName())
                .isEqualTo("Drums");

        //then all equipment = 3
        List<Equipment> allEquipment = equipmentService.getAllEquipment();
        LOG.info(allEquipment.toString());
        assert(allEquipment.size() == 3);
    }

    @Test
    @Order(4)
    public void whenUpdateOneEquipment_thenReturnUpdatedOneEquipment(){
        //given
        Map<String, Object> partialPiano = new HashMap<String,Object>();
        partialPiano.put("name","Piano-15678");
        partialPiano.put("referenceNumber","X03762-10864");

        //when
        Equipment updatedPiano = equipmentService.updateOneEquipment(1,partialPiano);

        //then
        assertEquals("Piano-15678",updatedPiano.getName());
        LOG.info(updatedPiano.toString());

    }

    @Test
    @Order(5)
    public void whenDeleteOneEquipment_thenReturnException(){

        equipmentService.deleteOneEquipment(1);

        try {
            Equipment deletedOneEquipment = equipmentService.getOneEquipment(1);
        }catch (ObjectNotFoundException e) {
            assertEquals("No row with the given identifier exists: [Equipment with id {1} not found#Optional.empty]",e.getMessage());

        }
    }
}
