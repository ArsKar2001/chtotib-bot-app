package com.karmanchik.chtotibtelegrambot.entity.constants;

public class Constants {
    public static class Group {
        public static final Integer NONE = 100;
    }

    public static class Bot {
        public static final Integer START = 100;
        public static final Integer REG = 101;
        public static final Integer AUTHORIZED = 102;
    }

    public static class User {
        public static final Integer NONE = 200;
        public static final Integer SELECT_COURSE = 201;
        public static final Integer SELECT_GROUP = 202;
        public static final Integer SELECT_ROLE = 203;
        public static final Integer SELECT_OPTION = 204;
        public static final Integer ENTER_NAME = 205;
        public static final Integer SELECT_TEACHER = 206;
        public static final Integer SELECT_TIMETABLE = 207;
    }

    public static class Role {
        public static final Integer NONE = 100;
        public static final Integer TEACHER = 101;
        public static final Integer STUDENT = 102;
    }
}
