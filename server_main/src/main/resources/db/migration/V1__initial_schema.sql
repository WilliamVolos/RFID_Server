create table reader_models -- Таблица моделей считывателей
(
    id   bigserial not null primary key,
    name varchar(50) unique not null,     -- имя модели считывателя
    producer varchar(50),                 -- производитель
    login varchar(50),                    -- логин по умолчанию
    password varchar(50),                 -- пароль по умолчанию
    count_ports int not null,             -- количество портов для подключения антенн на устройстве
    description varchar(150)              -- описание
);

create table reader_status -- Таблица состояний считывателя
(
    id   bigserial not null primary key,
    codename varchar(50) not null, -- кодовое имя состояния
    name varchar(50) not null      -- название состояния утройства
);

create table readers -- Таблица стационарных считывателей в системе
(
    id   bigserial not null primary key,
    name varchar(50) unique not null,     -- аппаратное имя устройства
    description varchar(150),             -- описание считывателя
    ip_address varchar(50) unique not null, -- IP Адрес устройства в сети
    tcp_port int not null,                  -- TCP PORT соединения
    reader_model_id  bigint not null,       -- модель считывателя
    reader_status_id bigint not null,       -- Состояние считывателя
    is_deleted boolean not null default false,     -- Признак удаленного устройства
    date_created TIMESTAMPTZ(0) not null DEFAULT Now() -- Дата добавления устройства
);

alter table readers add constraint readers_reader_model_id_fk
    foreign key (reader_model_id) references reader_models;

alter table readers add constraint readers_reader_status_id_fk
    foreign key (reader_status_id) references reader_status;

create table ports -- Таблица номеров портов на считывателе (порт = считывающая антенна)
(
    id   bigserial not null primary key,
    number int not null,         -- Номер порта устройства
    reader_id  bigint not null,           -- Считыватель
    order_column int not null    -- служебное поле для сортировки
);

create unique index ports_numb_reader ON ports (number, reader_id);

alter table ports add constraint ports_reader_id_fk
    foreign key (reader_id) references readers;

create table journal -- Таблица журнал считанных меток от всех считывателей
(
    id   bigserial not null primary key,
    datetime TIMESTAMPTZ(3) not null,      -- Точное время события считанной или потерянной метки антенной
    visible boolean not null,                  -- Признак события начала или окончания видимости метки антенной
    port_id bigint not null,                -- Номер антенны считывателя, на котором случилось данное событие
    epc  varchar(128) not null,            -- Идентификатор считанный с чипа RFID метки, банк памяти EPC
    rssi varchar(50)                       -- Уровень сигнала RFID метки (удаленность от антенны)
);

alter table journal add constraint journal_port_id_fk
    foreign key (port_id) references ports;

BEGIN;
    INSERT INTO reader_status
    (codename, name)
    VALUES ('UNAVAILABLE', 'Недоступен');

    INSERT INTO reader_status
    (codename, name)
    VALUES ('RUNNING', 'В работе');

    INSERT INTO reader_status
    (codename, name)
    VALUES ('STOPPED', 'Остановлен');

    INSERT INTO reader_status
    (codename, name)
    VALUES ('DISCONNECTED', 'Нет соединения');

    INSERT INTO reader_models
    (name, producer, login, password, count_ports, description)
    VALUES ('URA4','Chainway','admin','admin',4,'Chainway URA4');

    INSERT INTO reader_models
    (name, producer, login, password, count_ports, description)
    VALUES ('VIRTUAL_READER','ITProject','admin','admin',4,'Virtual reader');
COMMIT;
