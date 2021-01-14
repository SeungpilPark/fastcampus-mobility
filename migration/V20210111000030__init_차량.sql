-- vehicle Table Create SQL
CREATE TABLE vehicle
(
    `id`          VARCHAR(36)  NOT NULL COMMENT '차량아이디',
    `car_number`  VARCHAR(20)  NOT NULL COMMENT '차량이름',
    `coordinates` VARCHAR(200) NOT NULL COMMENT '좌표',
    `status`      VARCHAR(20)  NOT NULL COMMENT '차량상태',
    `create_date` DATETIME(6)  NOT NULL COMMENT '저장일시',
    `update_date` DATETIME(6)  NOT NULL COMMENT '수정일시',
    PRIMARY KEY (id)
);

ALTER TABLE vehicle
    COMMENT '차량정보';

ALTER TABLE vehicle
    ADD CONSTRAINT ux_vehicle_car_number UNIQUE (car_number);

-- vehicle_route Table Create SQL

CREATE TABLE vehicle_route
(
    `id`                      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '차량운행경로 아이디',
    `vehicle_id`              VARCHAR(36)  NOT NULL COMMENT '차량아이디',
    `operation_id`            VARCHAR(36)  NOT NULL COMMENT '운행아이디',
    `paths`                   LONGTEXT     NOT NULL COMMENT '경로',
    `origin_coordinates`      VARCHAR(200) NOT NULL COMMENT '출발지 좌표',
    `passenger_coordinates`   VARCHAR(200) NOT NULL COMMENT '승객 좌표',
    `passenger_path_index`    SMALLINT     NOT NULL COMMENT '승객 경로 인덱스',
    `destination_coordinates` VARCHAR(200) NOT NULL COMMENT '목적지 좌표',
    `create_date`             DATETIME(6)  NOT NULL COMMENT '저장일시',
    `update_date`             DATETIME(6)  NOT NULL COMMENT '수정일시',
    PRIMARY KEY (id)
);

ALTER TABLE vehicle_route
    ADD CONSTRAINT ux_vehicle_operation UNIQUE (vehicle_id, operation_id);

ALTER TABLE vehicle_route
    COMMENT '차량 운행 경로 정보';

-- vehicle_event Table Create SQL
CREATE TABLE vehicle_event_store
(
    `id`             BIGINT      NOT NULL AUTO_INCREMENT COMMENT '이벤트 아이디',
    `correlation_id` VARCHAR(36) NOT NULL COMMENT '코릴레이션 키 (차량아이디)',
    `payload`        TEXT        NOT NULL COMMENT '이벤트 내용',
    `create_date`    DATETIME(6) NOT NULL COMMENT '이벤트 저장일시',
    PRIMARY KEY (id)
);

ALTER TABLE vehicle_event_store
    COMMENT '차량 이벤트스토어';

CREATE INDEX ix_vehicle_correlation_id ON vehicle_event_store (correlation_id);
CREATE INDEX ix_vehicle_create_date ON vehicle_event_store (create_date);
