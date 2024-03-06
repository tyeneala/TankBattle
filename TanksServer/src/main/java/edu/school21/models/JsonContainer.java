package edu.school21.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;

public class JsonContainer {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final String json;
    private final ClassType classType;

    public JsonContainer(@JsonProperty("json") String json, @JsonProperty("class_type") ClassType classType) {
        this.json = json;
        this.classType = classType;
    }

    public static ClassType getClassType(Class<?> aClass) {
        if (aClass == Player.class) {
            return ClassType.PLAYER;
        } else if (aClass == Bullet.class) {
            return ClassType.BULLET;
        } else if (aClass == KeyCode.class) {
            return ClassType.KEY_CODE;
        } else if (aClass == PlayerStat.class) {
            return ClassType.PLAYER_STAT;
        } else {
            throw new UnsupportedOperationException("Supported classes are Player.class, Bullet.class, KeyCode.class and PlayerStat.class");
        }
    }

    public ClassType getClassType() {
        return classType;
    }

    public String getJson() {
        return json;
    }

    public enum ClassType {PLAYER, BULLET, KEY_CODE, PLAYER_STAT}

}
