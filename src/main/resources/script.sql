create sequence global_seq;

alter sequence global_seq owner to kwdggruqsaufbb;

create sequence hibernate_sequence;

alter sequence hibernate_sequence owner to kwdggruqsaufbb;

create table groups
(
    id         integer default nextval('global_seq'::regclass) not null,
    group_name text                                            not null
        constraint groups_group_name_uindex
            unique,
    timetable  json
);

alter table groups
    owner to kwdggruqsaufbb;

create table users
(
    id                 integer default nextval('global_seq'::regclass) not null
        constraint users_pkey
            primary key,
    chat_id            integer                                         not null
        constraint users_chat_id_key
            unique
        constraint users_unique_chatid_idx
            unique,
    name               varchar                                         not null,
    bot_state          varchar                                         not null,
    group_id           integer
        constraint users_schedule_id_fk
            references groups (id)
            on update cascade on delete cascade,
    role_name          varchar(20)                                     not null,
    user_state         varchar                                         not null,
    bot_lat_message_id integer                                         not null
);

alter table users
    owner to kwdggruqsaufbb;

create unique index users_bot_lat_message_id_uindex
    on users (bot_lat_message_id);

create unique index groups_id_uindex
    on groups (id);

create unique index schedule_group_name_uindex
    on groups (group_name);

create table replacement
(
    id        integer not null
        constraint replacement_pk
            primary key,
    group_id  integer not null
        constraint replacement_schedule_id_fk
            references groups (id)
            on update cascade on delete cascade,
    timetable json    not null,
    date      date    not null
);

alter table replacement
    owner to kwdggruqsaufbb;

create unique index replacement_id_uindex
    on replacement (id);

create function getdayofweek(day_number integer) returns character varying
    language plpgsql
as
$$
BEGIN
    CASE day_number
        WHEN 0 THEN RETURN 'Понедельник';
        WHEN 1 THEN RETURN 'Вторник';
        WHEN 2 THEN RETURN 'Среда';
        WHEN 3 THEN RETURN 'Четверг';
        WHEN 4 THEN RETURN 'Пятница';
        WHEN 5 THEN RETURN 'Суббота';
        WHEN 6 THEN RETURN 'Воскресенье';
        END CASE;
    RETURN '-';
END
$$;

alter function getdayofweek(integer) owner to kwdggruqsaufbb;

create function get_json_fields_from_text()
    returns TABLE
            (
                id            integer,
                group_name    text,
                auditorium    text,
                teacher       text,
                week_type     text,
                discipline    text,
                lesson_number text,
                day_of_week   text
            )
    language plpgsql
as
$$
BEGIN
    RETURN QUERY
        SELECT s.id,
               s.group_name,
               json_array_elements(s.timetable)::json ->> 'auditorium'    as auditorium,
               json_array_elements(s.timetable)::json ->> 'teacher'       as teacher,
               json_array_elements(s.timetable)::json ->> 'week_type'     as week_type,
               json_array_elements(s.timetable)::json ->> 'discipline'    as discipline,
               json_array_elements(s.timetable)::json ->> 'lesson_number' as lesson_number,
               json_array_elements(s.timetable)::json ->> 'day_of_week'   as day_of_week
        FROM groups s;
END
$$;

alter function get_json_fields_from_text() owner to kwdggruqsaufbb;


