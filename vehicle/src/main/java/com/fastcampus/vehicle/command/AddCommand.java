package com.fastcampus.vehicle.command;

import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddCommand {

  @NotEmpty
  private String carNumber;
  @NotEmpty
  private String coordinates;
}
