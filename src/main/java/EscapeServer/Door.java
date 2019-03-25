package EscapeServer;

class Door {
    private Integer reference;
    private Integer roomReference;
    private Boolean sound;
    private Integer openCount;

    Door(Integer reference, Integer roomReference, Boolean sound, Integer openCount) {
        this.reference = reference;
        this.roomReference = roomReference;
        this.sound = sound;
        this.openCount = openCount;
    }

    Integer getReference() {
        return this.reference;
    }

    Integer getRoomReference() {
        return this.roomReference;
    }

    Boolean getSound() {
        return this.sound;
    }

    Integer getOpenCount() {
        return this.openCount;
    }

    void addOpenCount(){
        this.openCount++;
    }
}