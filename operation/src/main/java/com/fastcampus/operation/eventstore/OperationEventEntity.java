package com.fastcampus.operation.eventstore;

import com.fastcampus.common.event.AbstractEventEntity;
import javax.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "operation_event_store")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class OperationEventEntity extends AbstractEventEntity {

}

