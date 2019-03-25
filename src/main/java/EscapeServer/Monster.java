package EscapeServer;

class Monster {
    private Integer reference;
    private Integer level;
    private Integer health;
    private Integer armor;
    private Integer attack;
    private Integer[] equipmentReferences;

    Monster(Integer reference, Integer level, Integer health, Integer armor, Integer attack) {
        this.reference = reference;
        this.level = level;
        this.health = health;
        this.armor = armor;
        this.attack = attack;
        this.equipmentReferences = new Integer[2];
    }
    Integer getReference() {
        return this.reference;
    }

    Integer getLevel() {
        return this.level;
    }

    Integer getHealth() {
        return this.health;
    }

    Integer getArmor() {
        return this.armor;
    }

    Integer getAttack() {
        return this.attack;
    }

    Integer[] getEquipmentReferences() {
        return this.equipmentReferences;
    }

    void setEquipmentReferences(Integer[] references) {
        this.equipmentReferences = references;
    }

}
