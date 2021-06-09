create table if not exists client (
    id int,
    name varchar(120) not null,
    country varchar(120) not null,
    createdAt date not null,
    isPartner bool not null,
    primary key (id)
);

create table if not exists videogame (
    id int,
    name varchar(120) not null unique,
    platform int not null,
    releaseDate date not null,
    price int not null,
    primary key (id)
);

create table if not exists client_videogames (
    client_id int not null,
    videogame_id int not null,
    foreign key (client_id) references client(id) on delete cascade,
    foreign key (videogame_id) references videogame(id) on delete cascade
);