package com.luckyblock.block.luckyblock.event;

public class WeightedLuckyEvent {

    private final LuckyEvent event;
    private final int weight;

    public WeightedLuckyEvent(LuckyEvent event, int weight) {
        this.event = event;
        this.weight = weight;
    }

    public LuckyEvent getEvent() {
        return event;
    }

    public int getWeight() {
        return weight;
    }
}
