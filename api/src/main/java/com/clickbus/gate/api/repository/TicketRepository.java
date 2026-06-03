package com.clickbus.gate.api.repository;

import com.clickbus.gate.api.model.TicketHistorico;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TicketRepository extends MongoRepository<TicketHistorico, String> {
    List<TicketHistorico> findByUsuarioIdOrderByDataHoraValidacaoDesc(String usuarioId);
}
