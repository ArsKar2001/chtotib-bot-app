package com.karmanchik.chtotibtelegrambot.entity;

public class State {
    public enum User {
        NONE(200),
        SELECT_COURSE(201),
        SELECT_GROUP(202),
        SELECT_ROLE(203),
        ENTER_NAME(205),
        SELECT_TEACHER(206);

        private final int id;

        User(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
    public enum Bot {
        START(100),
        REG(101),
        AUTHORIZED(102);

        private final int id;

        Bot(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public enum Role {
        NONE(100),
        TEACHER(101),
        STUDENT(102);

        private final int id;

        Role(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}
