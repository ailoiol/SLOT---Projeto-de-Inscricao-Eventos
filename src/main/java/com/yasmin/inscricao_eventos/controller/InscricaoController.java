package com.yasmin.inscricao_eventos.controller;

import com.yasmin.inscricao_eventos.dto.InscricaoResponseDTO;
import com.yasmin.inscricao_eventos.exception.RecursoNaoEncontradoException;
import com.yasmin.inscricao_eventos.exception.RegraDeNegocioException;
import com.yasmin.inscricao_eventos.model.Evento;
import com.yasmin.inscricao_eventos.model.Inscricao;
import com.yasmin.inscricao_eventos.model.Usuario;
import com.yasmin.inscricao_eventos.repository.EventoRepository;
import com.yasmin.inscricao_eventos.repository.InscricaoRepository;
import com.yasmin.inscricao_eventos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.yasmin.inscricao_eventos.dto.InscricaoCriacaoRequestDTO;
import com.yasmin.inscricao_eventos.model.CriterioPersonalizado;
import com.yasmin.inscricao_eventos.model.RespostaCriterio;
import com.yasmin.inscricao_eventos.repository.CriterioPersonalizadoRepository;
import com.yasmin.inscricao_eventos.repository.RespostaCriterioRepository;
import java.util.ArrayList;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/inscricoes")
public class InscricaoController {

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private CriterioPersonalizadoRepository criterioPersonalizadoRepository;

    @Autowired
    private RespostaCriterioRepository respostaCriterioRepository;

    @GetMapping
    public List<InscricaoResponseDTO> listarTodos() {
        return inscricaoRepository.findAll()
            .stream()
            .map(InscricaoResponseDTO::fromEntity)
            .toList();
    }
    @GetMapping(params = "responsavelId")
    public List<InscricaoResponseDTO> listarPorResponsavel(@RequestParam Integer responsavelId) {
        return inscricaoRepository.findByUsuarioId(responsavelId)
            .stream()
            .map(InscricaoResponseDTO::fromEntity)
            .toList();
}

    @GetMapping(params = "eventoId")
    public List<InscricaoResponseDTO> listarPorEvento(@RequestParam Integer eventoId) {
        return inscricaoRepository.findByEventoId(eventoId)
            .stream()
            .map(InscricaoResponseDTO::fromEntity)
            .toList();
}

    @PostMapping
    public InscricaoResponseDTO criar(@Valid @RequestBody InscricaoCriacaoRequestDTO request) {
        Inscricao inscricao = request.getInscricao();

        Usuario responsavel = usuarioRepository.findById(inscricao.getResponsavel().getId())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário responsável não encontrado"));

        if ("organizador".equals(responsavel.getTipo())) {
            throw new RegraDeNegocioException("Contas de organizador não podem se inscrever em eventos");
    }

        Evento evento = eventoRepository.findById(inscricao.getEvento().getId())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Evento não encontrado"));

        if (!"aberto".equals(evento.getStatus())) {
            throw new RegraDeNegocioException("Este evento não está aberto para inscrições");
    }

        if (evento.getVagasOcupadas() >= evento.getVagasTotais()) {
            evento.setStatus("fechado");
            eventoRepository.save(evento);
            throw new RegraDeNegocioException("As vagas deste evento já se esgotaram");
    }

        boolean jaExiste = inscricaoRepository.existsByCpfParticipanteAndEventoId(
            inscricao.getCpfParticipante(),
            evento.getId()
    );

        if (jaExiste) {
            throw new RegraDeNegocioException("Este CPF já está inscrito neste evento");
    }

    List<CriterioPersonalizado> criteriosDoEvento = criterioPersonalizadoRepository.findByEventoId(evento.getId());
        List<Integer> criteriosRespondidos = new ArrayList<>();
        List<?> respostasRequest = extrairRespostasDaRequest(request);
        if (respostasRequest != null) {
            for (Object respostaResponse : respostasRequest) {
                criteriosRespondidos.add((Integer) lerPropriedade(respostaResponse, "criterioPersonalizadoId"));
            }
        }

        for (CriterioPersonalizado criterio : criteriosDoEvento) {
            if (Boolean.TRUE.equals(criterio.getObrigatorio()) && !criteriosRespondidos.contains(criterio.getId())) {
                throw new RegraDeNegocioException("O critério \"" + criterio.getCampo() + "\" é obrigatório para este evento");
            }
        }

        inscricao.setResponsavel(responsavel);
        inscricao.setEvento(evento);
        Inscricao salva = inscricaoRepository.save(inscricao);

        evento.setVagasOcupadas(evento.getVagasOcupadas() + 1);
        if (evento.getVagasOcupadas() >= evento.getVagasTotais()) {
            evento.setStatus("fechado");
        }
        eventoRepository.save(evento);

        if (respostasRequest != null) {
            List<RespostaCriterio> respostasSalvas = new ArrayList<>();
            for (Object respostaResponse : respostasRequest) {
                Integer criterioId = (Integer) lerPropriedade(respostaResponse, "criterioPersonalizadoId");
                CriterioPersonalizado criterio = criterioPersonalizadoRepository
                    .findById(criterioId)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Critério personalizado não encontrado"));

                RespostaCriterio resposta = new RespostaCriterio();
                resposta.setInscricao(salva);
                resposta.setCriterioPersonalizado(criterio);
                resposta.setValor(String.valueOf(lerPropriedade(respostaResponse, "valor")));
                respostasSalvas.add(respostaCriterioRepository.save(resposta));
            }
        }
    return InscricaoResponseDTO.fromEntity(salva);
}

    @PutMapping("/{id}")
    public InscricaoResponseDTO atualizar(@PathVariable Integer id, @Valid @RequestBody Inscricao dadosAtualizados) {
        Inscricao inscricao = inscricaoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Inscrição com id " + id + " não encontrada"));

        inscricao.setResponsavel(dadosAtualizados.getResponsavel());
        inscricao.setEvento(dadosAtualizados.getEvento());
        inscricao.setStatus(dadosAtualizados.getStatus());

        Inscricao atualizada = inscricaoRepository.save(inscricao);
        return InscricaoResponseDTO.fromEntity(atualizada);
    }

    @DeleteMapping("/{id}")
    public InscricaoResponseDTO cancelar(@PathVariable Integer id) {
        Inscricao inscricao = inscricaoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Inscrição com id " + id + " não encontrada"));

        if ("cancelada".equals(inscricao.getStatus())) {
            throw new RegraDeNegocioException("Esta inscrição já está cancelada");
        }

        if (LocalDateTime.now().isAfter(inscricao.getEvento().getDataEvento().minusHours(2))) {
            throw new RegraDeNegocioException("Cancelamento só é permitido até 2h antes do evento");
        }

        inscricao.setStatus("cancelada");
        Inscricao cancelada = inscricaoRepository.save(inscricao);

        Evento evento = inscricao.getEvento();
        evento.setVagasOcupadas(Math.max(0, evento.getVagasOcupadas() - 1));
        if (!"cancelado".equals(evento.getStatus())) {
            evento.setStatus("aberto");
        }
        eventoRepository.save(evento);

        return InscricaoResponseDTO.fromEntity(cancelada);
    }

    private List<?> extrairRespostasDaRequest(InscricaoCriacaoRequestDTO request) {
        if (request == null) {
            return null;
        }

        try {
            Object respostas = request.getClass().getMethod("getRespostas").invoke(request);
            if (respostas instanceof List<?> lista) {
                return lista;
            }
        } catch (Exception ignored) {
            // Ignorado: a propriedade pode ser acessada por outro nome/estrutura
        }

        return null;
    }

    private Object lerPropriedade(Object origem, String nomePropriedade) {
        if (origem == null) {
            return null;
        }

        if (origem instanceof java.util.Map<?, ?> mapa) {
            return mapa.get(nomePropriedade);
        }

        try {
            return new org.springframework.beans.BeanWrapperImpl(origem).getPropertyValue(nomePropriedade);
        } catch (Exception ignored) {
            // Ignorado: tenta leitura via getter caso o BeanWrapper não resolva a propriedade
        }

        String metodo = "get" + Character.toUpperCase(nomePropriedade.charAt(0))
            + nomePropriedade.substring(1);

        try {
            return origem.getClass().getMethod(metodo).invoke(origem);
        } catch (Exception ignored) {
            return null;
        }
    }
}