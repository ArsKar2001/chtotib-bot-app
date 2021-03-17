package com.karmanchik.chtotibtelegrambot.model;

import com.karmanchik.chtotibtelegrambot.entity.Group;

public class GroupNone extends Group {
    private static final Integer GROUP_NONE_ID = 100;
    private static final String GROUP_NONE_NAME = "NONE";

    private static final GroupNone INSTANCE = new GroupNone();

    private GroupNone() {
        this.id = GROUP_NONE_ID;
        this.groupName = GROUP_NONE_NAME;
    }

    public static Group getInstance() {
        return INSTANCE;
    }
}
