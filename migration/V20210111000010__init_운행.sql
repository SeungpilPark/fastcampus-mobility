-- vehicle Table Create SQL
CREATE TABLE operation
(
    `id`                      VARCHAR(36)  NOT NULL COMMENT '운행 아이디',
    `passenger_coordinates`   VARCHAR(200) NOT NULL COMMENT '승객 좌표',
    `destination_coordinates` VARCHAR(200) NOT NULL COMMENT '목적지 좌표',
    `status`                  VARCHAR(20)  NOT NULL COMMENT '운행상태',
    `create_date`             DATETIME(6)  NOT NULL COMMENT '저장일시',
    `update_date`             DATETIME(6)  NOT NULL COMMENT '수정일시',
    PRIMARY KEY (id)
);

ALTER TABLE operation
    COMMENT '운행정보';

-- operation_event Table Create SQL
CREATE TABLE operation_event_store
(
    `id`             BIGINT      NOT NULL AUTO_INCREMENT COMMENT '이벤트 아이디',
    `correlation_id` VARCHAR(36) NOT NULL COMMENT '코릴레이션 키 (운행 아이디)',
    `payload`        TEXT        NOT NULL COMMENT '이벤트 내용',
    `create_date`    DATETIME(6) NOT NULL COMMENT '이벤트 저장일시',
    PRIMARY KEY (id)
);

ALTER TABLE operation_event_store
    COMMENT '운행 이벤트스토어';

CREATE INDEX ix_operation_correlation_id ON operation_event_store (correlation_id);
CREATE INDEX ix_operation_create_date ON operation_event_store (create_date);
