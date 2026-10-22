package com.filmmax.model;

public enum ResourceType {
    ACTOR("actors", "CASTING_MANAGER", "Actor"),
    EQUIPMENT("equipment", "EQUIPMENT_MANAGER", "Equipment"),
    LOCATION("locations", "LOCATION_MANAGER", "Location"),
    COSTUME("costumes", "STYLIST", "Costume"),
    SHOT("shots", "DIRECTOR", "Shot");

    private final String path;        // URL segment and table name
    private final String managerRole; // role that manages this resource
    private final String label;       // used in messages

    ResourceType(String path, String managerRole, String label) {
        this.path = path;
        this.managerRole = managerRole;
        this.label = label;
    }

    public String getPath() { return path; }
    public String getManagerRole() { return managerRole; }
    public String getLabel() { return label; }

    public static ResourceType from(String path) {
        for (ResourceType t : values()) {
            if (t.path.equals(path)) return t;
        }
        throw new IllegalArgumentException("Unknown resource type: " + path);
    }
}