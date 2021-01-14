package com.fastcampus.operation.command;

import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CancelCommand {

  @NotEmpty
  private String operationId;
}
