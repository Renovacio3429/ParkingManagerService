CREATE TABLE car_types
(
    code        VARCHAR(32) PRIMARY KEY,
    description VARCHAR(128)
);

COMMENT ON TABLE car_types IS 'Справочник типов автомобилей';
COMMENT ON COLUMN car_types.code IS 'Уникальный код типа автомобиля';
COMMENT ON COLUMN car_types.description IS 'Описание типа автомобиля';

INSERT INTO car_types(code, description) VALUES
                                             ('SEDAN', 'Легковой'),
                                             ('TRUCK', 'Грузовой');

CREATE TABLE parking_events
(
    id           BIGSERIAL PRIMARY KEY,
    car_number   VARCHAR(32)              NOT NULL,
    car_type     VARCHAR(32)              NOT NULL,
    entry_time   TIMESTAMP WITH TIME ZONE NOT NULL,
    exit_time    TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_parking_events_car_type
        FOREIGN KEY (car_type) REFERENCES car_types(code)
);

COMMENT ON TABLE parking_events IS 'Таблица для хранения событий въезда и выезда автомобилей на парковке';
COMMENT ON COLUMN parking_events.id IS 'Уникальный идентификатор события';
COMMENT ON COLUMN parking_events.car_number IS 'Государственный регистрационный номер автомобиля';
COMMENT ON COLUMN parking_events.car_type IS 'Код типа автомобиля (ссылка на car_types)';
COMMENT ON COLUMN parking_events.entry_time IS 'Время въезда автомобиля на парковку';
COMMENT ON COLUMN parking_events.exit_time IS 'Время выезда автомобиля с парковки';

CREATE INDEX idx_parking_events_car_number ON parking_events (car_number);
COMMENT ON INDEX idx_parking_events_car_number IS 'Быстрый поиск событий по номеру автомобиля';

CREATE INDEX idx_parking_events_exit_time ON parking_events (exit_time);
COMMENT ON INDEX idx_parking_events_exit_time IS 'Быстрый поиск по времени выезда';

CREATE INDEX idx_active_events ON parking_events (car_number) WHERE exit_time IS NULL;
COMMENT ON INDEX idx_active_events IS 'Быстрый поиск активных (не выехавших) автомобилей';

CREATE TABLE parking_audit
(
    id         BIGSERIAL PRIMARY KEY,
    car_number VARCHAR(32)              NOT NULL,
    action     VARCHAR(16)              NOT NULL,
    message    TEXT,
    event_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE parking_audit IS 'Таблица аудита действий на парковке: хранит историю всех попыток въезда и выезда автомобилей, а также ошибок и успешных операций.';

COMMENT ON COLUMN parking_audit.id IS 'Уникальный идентификатор записи аудита';
COMMENT ON COLUMN parking_audit.car_number IS 'Государственный номер автомобиля';
COMMENT ON COLUMN parking_audit.action IS 'Тип действия: попытка, успех или ошибка (enum)';
COMMENT ON COLUMN parking_audit.message IS 'Дополнительное сообщение или причина ошибки';
COMMENT ON COLUMN parking_audit.event_time IS 'Время события';