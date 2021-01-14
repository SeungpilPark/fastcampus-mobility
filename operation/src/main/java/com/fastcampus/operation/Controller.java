package com.fastcampus.operation;

import com.fastcampus.common.exception.EntityNotFoundException;
import com.fastcampus.operation.command.AddCommand;
import com.fastcampus.operation.domain.OperationEntity;
import com.fastcampus.operation.domain.OperationRepository;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/operation")
@Validated
public class Controller {

  private final CommandHandler commandHandler;
  private final QueryHandler queryHandler;
  private final OperationRepository operationRepository;

  @Autowired
  public Controller(
      final CommandHandler commandHandler,
      final QueryHandler queryHandler,
      final OperationRepository operationRepository
  ) {
    this.commandHandler = commandHandler;
    this.queryHandler = queryHandler;
    this.operationRepository = operationRepository;
  }

  @PostMapping("")
  public OperationEntity save(
      final @RequestBody @Valid AddCommand addCommand) {
    return commandHandler.execute(addCommand);
  }

  @GetMapping("/{id}")
  public OperationEntity get(
      final @PathVariable("id") @NotEmpty String id) {
    return queryHandler.get(id)
        .orElseThrow(EntityNotFoundException::new);
  }

  @GetMapping("")
  public Page<OperationEntity> page(final @PageableDefault(
      sort = {"createDate"}, direction = Direction.DESC, size = 20
  ) Pageable pageable) {
    return operationRepository.findAll(pageable);
  }
}

