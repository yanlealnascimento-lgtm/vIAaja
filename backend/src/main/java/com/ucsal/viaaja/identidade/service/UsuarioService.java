package com.ucsal.viaaja.identidade.service;

import com.ucsal.viaaja.common.exception.RecursoNaoEncontradoException;
import com.ucsal.viaaja.common.exception.RegraDeNegocioException;
import com.ucsal.viaaja.identidade.domain.PreferenciaPrivacidade;
import com.ucsal.viaaja.identidade.domain.Usuario;
import com.ucsal.viaaja.identidade.domain.Viajante;
import com.ucsal.viaaja.identidade.repository.UsuarioRepository;
import com.ucsal.viaaja.identidade.repository.ViajanteRepository;
import com.ucsal.viaaja.notificacao.domain.Notificacao;
import com.ucsal.viaaja.notificacao.service.NotificacaoService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private static final int IDADE_MINIMA = 18;
    private static final int HORAS_VALIDADE_RECUPERACAO = 2;

    private final UsuarioRepository usuarioRepository;
    private final ViajanteRepository viajanteRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final NotificacaoService notificacaoService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          ViajanteRepository viajanteRepository,
                          PasswordEncoder passwordEncoder,
                          EmailService emailService,
                          NotificacaoService notificacaoService) {
        this.usuarioRepository = usuarioRepository;
        this.viajanteRepository = viajanteRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.notificacaoService = notificacaoService;
    }

    @Transactional
    public Viajante cadastrarViajante(String nome, String email, String senha, String cpf,
                                      String telefone, LocalDate dataNascimento, String cidadeAproximada) {
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new RegraDeNegocioException("Este e-mail já está cadastrado");
        }
        if (viajanteRepository.existsByCpf(cpf)) {
            throw new RegraDeNegocioException("Este CPF já está cadastrado");
        }
        if (dataNascimento == null || Period.between(dataNascimento, LocalDate.now()).getYears() < IDADE_MINIMA) {
            throw new RegraDeNegocioException("RN-001",
                    "É preciso ter pelo menos " + IDADE_MINIMA + " anos para criar uma conta");
        }

        Viajante viajante = new Viajante(nome, email, passwordEncoder.encode(senha), telefone,
                cpf, dataNascimento, cidadeAproximada);
        String token = UUID.randomUUID().toString();
        viajante.registrarTokenConfirmacao(token);
        Viajante salvo = viajanteRepository.save(viajante);

        emailService.enviarConfirmacaoDeConta(salvo.getEmail(), token);
        notificacaoService.enviar(salvo.getId(), Notificacao.TipoNotificacao.CONTA,
                "Conta criada. Confirme seu e-mail para começar a usar a plataforma.");
        return salvo;
    }

    @Transactional
    public void confirmarConta(String token) {
        Usuario usuario = usuarioRepository.findByTokenConfirmacao(token)
                .orElseThrow(() -> new RegraDeNegocioException("Código de confirmação inválido"));
        usuario.confirmar();
        notificacaoService.enviar(usuario.getId(), Notificacao.TipoNotificacao.CONTA,
                "Conta confirmada com sucesso.");
    }

    @Transactional
    public void solicitarRecuperacaoDeSenha(String email) {
        usuarioRepository.findByEmailIgnoreCase(email).ifPresent(usuario -> {
            String token = UUID.randomUUID().toString();
            usuario.registrarTokenRecuperacao(token, LocalDateTime.now().plusHours(HORAS_VALIDADE_RECUPERACAO));
            emailService.enviarRecuperacaoDeSenha(usuario.getEmail(), token);
        });
    }

    @Transactional
    public void redefinirSenha(String token, String novaSenha) {
        Usuario usuario = usuarioRepository.findByTokenRecuperacao(token)
                .orElseThrow(() -> new RegraDeNegocioException("Código de recuperação inválido"));
        if (!usuario.recuperacaoValida(LocalDateTime.now())) {
            throw new RegraDeNegocioException("Código de recuperação expirado. Peça um novo.");
        }
        usuario.trocarSenha(passwordEncoder.encode(novaSenha));
    }

    @Transactional(readOnly = true)
    public Usuario porId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Usuário", id));
    }

    @Transactional(readOnly = true)
    public Viajante viajantePorId(Long id) {
        return viajanteRepository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Viajante", id));
    }

    @Transactional
    public Viajante atualizarPrivacidade(Long viajanteId, boolean visivelComoProximo,
                                         PreferenciaPrivacidade.QuemPodeConvidar quemPodeConvidar) {
        Viajante viajante = viajantePorId(viajanteId);
        PreferenciaPrivacidade preferencia = viajante.getPreferenciaPrivacidade();
        preferencia.setVisivelComoProximo(visivelComoProximo);
        preferencia.setQuemPodeConvidar(quemPodeConvidar);
        viajante.setPreferenciaPrivacidade(preferencia);
        return viajante;
    }

    @Transactional
    public Viajante atualizarPerfil(Long viajanteId, String nome, String telefone, String cidadeAproximada) {
        Viajante viajante = viajantePorId(viajanteId);
        viajante.setNome(nome);
        viajante.setTelefone(telefone);
        viajante.setCidadeAproximada(cidadeAproximada);
        return viajante;
    }
}
