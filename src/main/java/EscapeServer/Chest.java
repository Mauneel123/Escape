package EscapeServer;

class Chest {

    private Integer reference;
    private Integer level;
    private Integer equipmentReference;

    Chest(Integer reference, Integer level, Integer equipmentReference) {
        this.reference = reference;
        this.level = level;
        this.equipmentReference = equipmentReference;
    }

    Integer getReference() {
        return this.reference;
    }

    Integer getLevel() {
        return this.level;
    }

    void regenerateEquipment(){
        this.equipmentReference = this.equipmentReference;
    }

    Integer getEquipmentReference() {
        return this.equipmentReference;
    }

}
