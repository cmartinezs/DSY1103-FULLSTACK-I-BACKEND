package cl.duoc.fullstack.tickets.service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import cl.duoc.fullstack.tickets.controller.TicketController;
import cl.duoc.fullstack.tickets.dto.TicketResult;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public class TicketLinkAssembler {

    public EntityModel<TicketResult> toModel(TicketResult ticket) {
        EntityModel<TicketResult> model = EntityModel.of(ticket);

        model.add(linkTo(methodOn(TicketController.class)
            .getTicketById(ticket.id())).withSelfRel());

        model.add(linkTo(methodOn(TicketController.class)
            .getAllTickets(null)).withRel("all"));

        if ("OPEN".equals(ticket.status())) {
            model.add(linkTo(methodOn(TicketController.class)
                .updateTicketById(ticket.id(), null)).withRel("update"));

            model.add(linkTo(methodOn(TicketController.class)
                .deleteTicketById(ticket.id())).withRel("delete"));
        }

        return model;
    }
}
