package com.ucsal.viaaja.notificacao.service;

import com.ucsal.viaaja.common.exception.RecursoNaoEncontradoException;
import com.ucsal.viaaja.notificacao.domain.Notificacao;
import com.ucsal.viaaja.notificacao.repository.NotificacaoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificacaoService {

    private final NotificacaoRepository repository;

    public NotificacaoService(NotificacaoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Notificacao enviar(Long destinatarioId, Notificacao.TipoNotificacao tipo, String mensagem) {
        return repository.save(new Notificacao(destinatarioId, tipo, mensagem));
    }

    @Transactional(readOnly = true)
    public List<Notificacao> listar(Long destinatarioId) {
        return repository.findByDestinatarioIdOrderByDataDesc(destinatarioId);
    }

    @Transactional(readOnly = true)
    public long naoLidas(Long destinatarioId) {
        return repository.countByDestinatarioIdAndLidaFalse(destinatarioId);
    }

    @Transactional
    public void marcarComoLida(Long id, Long destinatarioId) {
        Notificacao notificacao = repository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Notificacao", id));
        if (!notificacao.getDestinatarioId().equals(destinatarioId)) {
            throw RecursoNaoEncontradoException.de("Notificacao", id);
        }
        notificacao.marcarComoLida();
    }
}
