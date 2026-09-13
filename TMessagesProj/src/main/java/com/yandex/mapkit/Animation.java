package com.yandex.mapkit;

public class Animation {
    public enum Type {
        SMOOTH,
        LINEAR
    }

    private Type type;
    private float duration;

    public Animation() {}

    public Animation(Type type, float duration) {
        this.type = type;
        this.duration = duration;
    }

    public Type getType() { return this.type; }
    public float getDuration() { return this.duration; }
}
