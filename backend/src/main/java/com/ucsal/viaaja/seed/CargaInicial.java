package com.ucsal.viaaja.seed;

import com.ucsal.viaaja.catalogo.domain.Atividade;
import com.ucsal.viaaja.catalogo.domain.Destino;
import com.ucsal.viaaja.catalogo.domain.Hospedagem;
import com.ucsal.viaaja.catalogo.domain.ModalTransporte;
import com.ucsal.viaaja.catalogo.domain.Passagem;
import com.ucsal.viaaja.catalogo.domain.Quarto;
import com.ucsal.viaaja.catalogo.repository.AtividadeRepository;
import com.ucsal.viaaja.catalogo.repository.DestinoRepository;
import com.ucsal.viaaja.catalogo.repository.HospedagemRepository;
import com.ucsal.viaaja.catalogo.repository.PassagemRepository;
import com.ucsal.viaaja.identidade.domain.Administrador;
import com.ucsal.viaaja.identidade.domain.AtendenteSuporte;
import com.ucsal.viaaja.identidade.domain.ParceiroFornecedor;
import com.ucsal.viaaja.identidade.domain.PreferenciaPrivacidade;
import com.ucsal.viaaja.identidade.domain.Viajante;
import com.ucsal.viaaja.identidade.repository.ParceiroRepository;
import com.ucsal.viaaja.identidade.repository.UsuarioRepository;
import com.ucsal.viaaja.identidade.repository.ViajanteRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!test")
public class CargaInicial implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CargaInicial.class);
    private static final String SENHA_PADRAO = "viaaja2026";

    private final UsuarioRepository usuarioRepository;
    private final ViajanteRepository viajanteRepository;
    private final ParceiroRepository parceiroRepository;
    private final DestinoRepository destinoRepository;
    private final PassagemRepository passagemRepository;
    private final HospedagemRepository hospedagemRepository;
    private final AtividadeRepository atividadeRepository;
    private final PasswordEncoder passwordEncoder;

    public CargaInicial(UsuarioRepository usuarioRepository,
                        ViajanteRepository viajanteRepository,
                        ParceiroRepository parceiroRepository,
                        DestinoRepository destinoRepository,
                        PassagemRepository passagemRepository,
                        HospedagemRepository hospedagemRepository,
                        AtividadeRepository atividadeRepository,
                        PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.viajanteRepository = viajanteRepository;
        this.parceiroRepository = parceiroRepository;
        this.destinoRepository = destinoRepository;
        this.passagemRepository = passagemRepository;
        this.hospedagemRepository = hospedagemRepository;
        this.atividadeRepository = atividadeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        String hash = passwordEncoder.encode(SENHA_PADRAO);

        Administrador admin = new Administrador("Administrador vIAaja", "admin@viaaja.com", hash,
                "71999990000");
        admin.confirmar();
        usuarioRepository.save(admin);

        AtendenteSuporte suporte = new AtendenteSuporte("Equipe de Suporte", "suporte@viaaja.com", hash,
                "71999990001", "SUP-001");
        suporte.confirmar();
        usuarioRepository.save(suporte);

        Viajante yan = criarViajante("Yan Leal Nascimento", "yan@viaaja.com", hash, "71999991000",
                "11122233344", LocalDate.of(2001, 5, 14), "Salvador", true,
                PreferenciaPrivacidade.QuemPodeConvidar.TODOS);
        Viajante davi = criarViajante("Davi de Sa Luquini", "davi@viaaja.com", hash, "71999991001",
                "22233344455", LocalDate.of(2000, 3, 2), "Salvador", true,
                PreferenciaPrivacidade.QuemPodeConvidar.MESMO_DESTINO);
        criarViajante("Erick de Souza Pereira", "erick@viaaja.com", hash, "71999991002",
                "33344455566", LocalDate.of(1999, 11, 20), "Lauro de Freitas", true,
                PreferenciaPrivacidade.QuemPodeConvidar.TODOS);
        criarViajante("Joao Henrique Santos Nascimento Souza", "joao@viaaja.com", hash, "71999991003",
                "44455566677", LocalDate.of(2001, 8, 9), "Salvador", false,
                PreferenciaPrivacidade.QuemPodeConvidar.NINGUEM);
        criarViajante("Kauan Aparecido Costa Casemiro", "kauan@viaaja.com", hash, "71999991004",
                "55566677788", LocalDate.of(2000, 1, 30), "Feira de Santana", true,
                PreferenciaPrivacidade.QuemPodeConvidar.TODOS);

        ParceiroFornecedor aerea = criarParceiro("Voe Bahia", "parceiro.aereo@viaaja.com", hash,
                "Voe Bahia Linhas Aereas S.A.", "11222333000144",
                ParceiroFornecedor.TipoParceiro.COMPANHIA_AEREA);
        ParceiroFornecedor rodoviaria = criarParceiro("Expresso Atlantico", "parceiro.onibus@viaaja.com", hash,
                "Expresso Atlantico Transportes Ltda", "22333444000155",
                ParceiroFornecedor.TipoParceiro.VIACAO_RODOVIARIA);
        ParceiroFornecedor hotelaria = criarParceiro("Rede Mar Azul", "parceiro.hotel@viaaja.com", hash,
                "Rede Mar Azul Hotelaria Ltda", "33444555000166",
                ParceiroFornecedor.TipoParceiro.HOTELARIA);

        Destino portoSeguro = destinoRepository.save(new Destino("Porto Seguro", "BA", "Brasil",
                "Litoral sul da Bahia, com praias extensas, centro historico e vida noturna.", 707));
        Destino lencois = destinoRepository.save(new Destino("Lencois", "BA", "Brasil",
                "Porta de entrada da Chapada Diamantina, com trilhas, cachoeiras e grutas.", 410));
        Destino ilheus = destinoRepository.save(new Destino("Ilheus", "BA", "Brasil",
                "Cidade do cacau, com praias urbanas e patrimonio literario.", 462));
        Destino recife = destinoRepository.save(new Destino("Recife", "PE", "Brasil",
                "Capital pernambucana, com Recife Antigo, museus e praias urbanas.", 839));

        criarAtividades(portoSeguro, lencois, ilheus, recife);
        criarPassagens(aerea, rodoviaria, portoSeguro, lencois, ilheus, recife);
        criarHospedagens(hotelaria, portoSeguro, lencois, ilheus, recife);

        log.info("Carga inicial concluida. Usuarios de teste com a senha {}", SENHA_PADRAO);
        log.info("Acesso rapido: {} e {}", yan.getEmail(), davi.getEmail());
    }

    private Viajante criarViajante(String nome, String email, String hash, String telefone, String cpf,
                                   LocalDate nascimento, String cidade, boolean visivel,
                                   PreferenciaPrivacidade.QuemPodeConvidar quemPodeConvidar) {
        Viajante viajante = new Viajante(nome, email, hash, telefone, cpf, nascimento, cidade);
        viajante.confirmar();
        PreferenciaPrivacidade preferencia = viajante.getPreferenciaPrivacidade();
        preferencia.setVisivelComoProximo(visivel);
        preferencia.setQuemPodeConvidar(quemPodeConvidar);
        viajante.setPreferenciaPrivacidade(preferencia);
        return viajanteRepository.save(viajante);
    }

    private ParceiroFornecedor criarParceiro(String nome, String email, String hash, String razaoSocial,
                                             String cnpj, ParceiroFornecedor.TipoParceiro tipo) {
        ParceiroFornecedor parceiro = new ParceiroFornecedor(nome, email, hash, "7133330000",
                razaoSocial, cnpj, tipo);
        parceiro.confirmar();
        parceiro.aprovar();
        return parceiroRepository.save(parceiro);
    }

    private void criarAtividades(Destino portoSeguro, Destino lencois, Destino ilheus, Destino recife) {
        atividadeRepository.saveAll(List.of(
                new Atividade("Centro Historico de Porto Seguro", "cultura", new BigDecimal("0"), portoSeguro),
                new Atividade("Praia de Taperapuan", "praia", new BigDecimal("80"), portoSeguro),
                new Atividade("Passarela do Descobrimento", "vida noturna", new BigDecimal("120"), portoSeguro),
                new Atividade("Recife de Fora", "natureza", new BigDecimal("150"), portoSeguro),
                new Atividade("Cachoeira da Fumaca", "natureza", new BigDecimal("180"), lencois),
                new Atividade("Gruta da Lapa Doce", "aventura", new BigDecimal("120"), lencois),
                new Atividade("Poco do Diabo", "natureza", new BigDecimal("90"), lencois),
                new Atividade("Mercado Municipal de Lencois", "gastronomia", new BigDecimal("60"), lencois),
                new Atividade("Casa de Jorge Amado", "cultura", new BigDecimal("30"), ilheus),
                new Atividade("Praia dos Milionarios", "praia", new BigDecimal("70"), ilheus),
                new Atividade("Fazenda de cacau", "gastronomia", new BigDecimal("110"), ilheus),
                new Atividade("Recife Antigo", "cultura", new BigDecimal("40"), recife),
                new Atividade("Instituto Ricardo Brennand", "cultura", new BigDecimal("60"), recife),
                new Atividade("Praia de Boa Viagem", "praia", new BigDecimal("50"), recife)));
    }

    private void criarPassagens(ParceiroFornecedor aerea, ParceiroFornecedor rodoviaria,
                                Destino portoSeguro, Destino lencois, Destino ilheus, Destino recife) {
        LocalDate base = LocalDate.now().plusDays(30);

        for (int dia = 0; dia < 20; dia++) {
            LocalDate data = base.plusDays(dia);

            passagemRepository.save(new Passagem(new BigDecimal("489.90").add(BigDecimal.valueOf(dia * 7L)),
                    60, portoSeguro, aerea, ModalTransporte.AVIAO, "Salvador", "Porto Seguro",
                    LocalDateTime.of(data, LocalTime.of(7, 20)), LocalDateTime.of(data, LocalTime.of(8, 25)),
                    0, 60, "Voe Bahia"));
            passagemRepository.save(new Passagem(new BigDecimal("615.00").add(BigDecimal.valueOf(dia * 5L)),
                    40, portoSeguro, aerea, ModalTransporte.AVIAO, "Salvador", "Porto Seguro",
                    LocalDateTime.of(data, LocalTime.of(16, 10)), LocalDateTime.of(data, LocalTime.of(18, 55)),
                    1, 40, "Voe Bahia"));
            passagemRepository.save(new Passagem(new BigDecimal("189.50").add(BigDecimal.valueOf(dia * 3L)),
                    44, portoSeguro, rodoviaria, ModalTransporte.ONIBUS, "Salvador", "Porto Seguro",
                    LocalDateTime.of(data, LocalTime.of(20, 0)),
                    LocalDateTime.of(data.plusDays(1), LocalTime.of(7, 30)),
                    2, 44, "Expresso Atlantico"));

            passagemRepository.save(new Passagem(new BigDecimal("420.00").add(BigDecimal.valueOf(dia * 4L)),
                    30, lencois, aerea, ModalTransporte.AVIAO, "Salvador", "Lencois",
                    LocalDateTime.of(data, LocalTime.of(9, 0)), LocalDateTime.of(data, LocalTime.of(10, 5)),
                    0, 30, "Voe Bahia"));
            passagemRepository.save(new Passagem(new BigDecimal("129.90").add(BigDecimal.valueOf(dia * 2L)),
                    46, lencois, rodoviaria, ModalTransporte.ONIBUS, "Salvador", "Lencois",
                    LocalDateTime.of(data, LocalTime.of(22, 30)),
                    LocalDateTime.of(data.plusDays(1), LocalTime.of(5, 45)),
                    1, 46, "Expresso Atlantico"));

            passagemRepository.save(new Passagem(new BigDecimal("355.00").add(BigDecimal.valueOf(dia * 6L)),
                    50, ilheus, aerea, ModalTransporte.AVIAO, "Salvador", "Ilheus",
                    LocalDateTime.of(data, LocalTime.of(11, 40)), LocalDateTime.of(data, LocalTime.of(12, 35)),
                    0, 50, "Voe Bahia"));
            passagemRepository.save(new Passagem(new BigDecimal("149.00").add(BigDecimal.valueOf(dia * 2L)),
                    44, ilheus, rodoviaria, ModalTransporte.ONIBUS, "Salvador", "Ilheus",
                    LocalDateTime.of(data, LocalTime.of(21, 0)),
                    LocalDateTime.of(data.plusDays(1), LocalTime.of(4, 30)),
                    2, 44, "Expresso Atlantico"));

            passagemRepository.save(new Passagem(new BigDecimal("398.00").add(BigDecimal.valueOf(dia * 5L)),
                    70, recife, aerea, ModalTransporte.AVIAO, "Salvador", "Recife",
                    LocalDateTime.of(data, LocalTime.of(6, 50)), LocalDateTime.of(data, LocalTime.of(8, 5)),
                    0, 70, "Voe Bahia"));
            passagemRepository.save(new Passagem(new BigDecimal("210.00").add(BigDecimal.valueOf(dia * 3L)),
                    44, recife, rodoviaria, ModalTransporte.ONIBUS, "Salvador", "Recife",
                    LocalDateTime.of(data, LocalTime.of(19, 0)),
                    LocalDateTime.of(data.plusDays(1), LocalTime.of(8, 0)),
                    3, 44, "Expresso Atlantico"));
        }
    }

    private void criarHospedagens(ParceiroFornecedor hotelaria, Destino portoSeguro, Destino lencois,
                                  Destino ilheus, Destino recife) {
        Hospedagem pousadaPorto = new Hospedagem(new BigDecimal("230.00"), 12, portoSeguro, hotelaria,
                "Pousada Mar Azul", 3, "Av. Beira Mar, 1200 - Porto Seguro",
                List.of("wifi", "cafe da manha", "piscina"));
        pousadaPorto.adicionarQuarto(new Quarto("Standard duplo", 2, new BigDecimal("230.00")));
        pousadaPorto.adicionarQuarto(new Quarto("Familia", 4, new BigDecimal("390.00")));
        hospedagemRepository.save(pousadaPorto);

        Hospedagem resortPorto = new Hospedagem(new BigDecimal("640.00"), 8, portoSeguro, hotelaria,
                "Resort Costa do Descobrimento", 5, "Rod. BR-367, km 65 - Porto Seguro",
                List.of("wifi", "all inclusive", "spa", "piscina"));
        resortPorto.adicionarQuarto(new Quarto("Suite luxo", 2, new BigDecimal("640.00")));
        hospedagemRepository.save(resortPorto);

        Hospedagem pousadaLencois = new Hospedagem(new BigDecimal("180.00"), 10, lencois, hotelaria,
                "Pousada Chapada Viva", 3, "Rua das Pedras, 45 - Lencois",
                List.of("wifi", "cafe da manha", "guia local"));
        pousadaLencois.adicionarQuarto(new Quarto("Chale duplo", 2, new BigDecimal("180.00")));
        hospedagemRepository.save(pousadaLencois);

        Hospedagem hotelIlheus = new Hospedagem(new BigDecimal("255.00"), 14, ilheus, hotelaria,
                "Hotel Cacau Palace", 4, "Av. Soares Lopes, 900 - Ilheus",
                List.of("wifi", "estacionamento", "cafe da manha"));
        hotelIlheus.adicionarQuarto(new Quarto("Standard", 2, new BigDecimal("255.00")));
        hospedagemRepository.save(hotelIlheus);

        Hospedagem hotelRecife = new Hospedagem(new BigDecimal("310.00"), 20, recife, hotelaria,
                "Hotel Boa Viagem Prime", 4, "Av. Boa Viagem, 3000 - Recife",
                List.of("wifi", "academia", "piscina", "cafe da manha"));
        hotelRecife.adicionarQuarto(new Quarto("Superior", 2, new BigDecimal("310.00")));
        hotelRecife.adicionarQuarto(new Quarto("Executivo", 3, new BigDecimal("445.00")));
        hospedagemRepository.save(hotelRecife);
    }
}
