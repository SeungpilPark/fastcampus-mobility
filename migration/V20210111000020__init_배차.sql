-- vehicle Table Create SQL
CREATE TABLE dispatch
(
    `operation_id` VARCHAR(36) NOT NULL COMMENT '운행아이디',
    `vehicle_id`   VARCHAR(36) NOT NULL COMMENT '차량아이디',
    `status`       VARCHAR(20) NOT NULL COMMENT '배차상태',
    `create_date`  DATETIME(6) NOT NULL COMMENT '저장일시',
    `update_date`  DATETIME(6) NOT NULL COMMENT '수정일시',
    PRIMARY KEY (operation_id, vehicle_id)
);

ALTER TABLE dispatch
    COMMENT '배차정보';

-- operation_event Table Create SQL
CREATE TABLE dispatch_event_store
(
    `id`             BIGINT      NOT NULL AUTO_INCREMENT COMMENT '이벤트아이디',
    `correlation_id` VARCHAR(36) NOT NULL COMMENT '코릴레이션 키 (운행아이디)',
    `payload`        TEXT        NOT NULL COMMENT '이벤트 내용',
    `create_date`    DATETIME(6) NOT NULL COMMENT '이벤트 저장일시',
    PRIMARY KEY (id)
);

ALTER TABLE dispatch_event_store
    COMMENT '배차 이벤트스토어';

CREATE INDEX ix_dispatch_correlation_id ON dispatch_event_store (correlation_id);
CREATE INDEX ix_dispatch_create_date ON dispatch_event_store (create_date);
