create table if not exists  countries
(
    id           serial primary key,
    country_code varchar(3)  not null unique,
    country_name varchar(20) not null unique
);

create table if not exists airports
(
    id           serial
        primary key,
    airport_code varchar(3)  not null
        constraint airports_code
            unique,
    city         varchar(20) not null,
    country_code varchar(3)  not null
        references countries (country_code)
);

create index if not exists airports_code_city_country
    on airports using btree (airport_code, city, country_code);


create table if not exists flights
(
    id              serial primary key,
    aircraft_model  varchar(20) not null,
    departure_time  timestamp   not null,
    flying_from     varchar     not null references public.airports (airport_code),
    flying_to       varchar     not null references public.airports (airport_code),
    flight_time     interval    not null,
    number_of_seats int         not null,
    flight_number   varchar     not null
);

create table if not exists customers
(
    id            serial primary key,
    INN           int unique     not null,
    passport_id   varchar unique not null,
    customer_name varchar        not null,
    gender        varchar        not null,
    birthday      date           not null,
    citizenship   varchar        not null references countries (country_name)
);

create table if not exists tickets
(
    id serial primary key,
    client_id              int references customers (id),
    ticket_purchasing_time timestamp not null,
    ticket_number          int       not null unique,
    flight_number          varchar   not null
);