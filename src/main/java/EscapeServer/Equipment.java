package EscapeServer;

class Equipment {

    private Integer reference;
    private Integer usage;
    private Integer status;
    private Integer level;

    Equipment(Integer reference, Integer usage, Integer status, Integer level) {
        this.reference = reference;
        this.usage = usage;
        this.status = status;
        this.level = level;
    }

    Integer getReference() {
        return this.reference;
    }

    Integer getUsage() {
        return this.usage;
    }

    Integer getStatus() {
        return this.status;
    }

    Integer getLevel() {
        return this.level;
    }

}
