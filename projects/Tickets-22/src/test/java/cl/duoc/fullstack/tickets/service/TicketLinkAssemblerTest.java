package cl.duoc.fullstack.tickets.service;

import static org.assertj.core.api.Assertions.assertThat;

import cl.duoc.fullstack.tickets.dto.TicketResult;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

class TicketLinkAssemblerTest {

  private final TicketLinkAssembler assembler = new TicketLinkAssembler();

  @Test
  void toModel_shouldIncludeUpdateAndDeleteLinks_whenTicketIsOpen() {
    EntityModel<TicketResult> model = assembler.toModel(ticket("OPEN"));

    assertThat(model.getLink("self")).isPresent();
    assertThat(model.getLink("all")).isPresent();
    assertThat(model.getLink("update")).isPresent();
    assertThat(model.getLink("delete")).isPresent();
  }

  @Test
  void toModel_shouldOmitUpdateAndDeleteLinks_whenTicketIsNotOpen() {
    EntityModel<TicketResult> model = assembler.toModel(ticket("NEW"));

    assertThat(model.getLink("self")).isPresent();
    assertThat(model.getLink("all")).isPresent();
    assertThat(model.getLink("update")).isEmpty();
    assertThat(model.getLink("delete")).isEmpty();
  }

  private TicketResult ticket(String status) {
    return new TicketResult(1L, "Problema", "Detalle", status, null, null, null, null, null, null, null);
  }
}
