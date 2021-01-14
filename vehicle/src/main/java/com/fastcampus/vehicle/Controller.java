package com.fastcampus.vehicle;

import com.fastcampus.common.exception.EntityNotFoundException;
import com.fastcampus.vehicle.command.AddCommand;
import com.fastcampus.vehicle.domain.VehicleEntity;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vehicle")
@Validated
public class Controller {

  private final CommandHandler commandHandler;
  private final QueryHandler queryHandler;

  @Autowired
  public Controller(
      final CommandHandler commandHandler,
      final QueryHandler queryHandler
  ) {
    this.commandHandler = commandHandler;
    this.queryHandler = queryHandler;
  }

  @PostMapping("")
  public VehicleEntity save(
      final @RequestBody @Valid AddCommand addCommand) {
    return commandHandler.execute(addCommand);
  }

  @GetMapping("/{id}")
  public VehicleEntity get(
      final @PathVariable("id") @NotEmpty String id) {
    return queryHandler.get(id)
        .orElseThrow(EntityNotFoundException::new);
  }
}

