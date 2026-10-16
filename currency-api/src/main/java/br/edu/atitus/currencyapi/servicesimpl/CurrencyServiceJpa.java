package br.edu.atitus.currencyapi.servicesimpl;

import br.edu.atitus.currencyapi.dtos.CurrencyResponse;
import br.edu.atitus.currencyapi.repositories.CurrencyRepository;
import br.edu.atitus.currencyapi.services.CurrencyService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CurrencyServiceJpa implements CurrencyService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyServiceJpa.class);

    private final CurrencyRepository repository;
    private final BcbClientService bcbClientService;

    public CurrencyServiceJpa(CurrencyRepository repository, BcbClientService bcbClientService) {
        this.repository = repository;
        this.bcbClientService = bcbClientService;
    }

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${app.bcb.data-cotacao:10-09-2026}")
    private String dataCotacao;

    @Override
    public CurrencyResponse findBySourceCurrencyAndTargetCurrency(
            String sourceCurrency,
            String targetCurrency
    ) throws Exception {
        String source = sourceCurrency.toUpperCase();
        String target = targetCurrency.toUpperCase();
        String environment = "Currency API running in Port: " + serverPort;

        // 1) Tenta obter a cotação na API do Banco Central (PTAX)
        String motivoFallback;
        try {
            Double rate = getRateFromBcb(source, target);
            if (rate != null)
                return new CurrencyResponse(source, target, rate,
                        environment + " - Source: BCB PTAX " + dataCotacao);
            motivoFallback = "BCB sem cotacao para " + source + "/" + target + " em " + dataCotacao;
        } catch (Exception e) {
            motivoFallback = "falha BCB: " + e.getMessage();
        }

        // 2) Fallback: cotação cadastrada no banco de dados local
        log.warn("Fallback para banco local ({})", motivoFallback);
        var currency = repository.findBySourceCurrencyAndTargetCurrency(source, target)
                .orElseThrow(() -> new EntityNotFoundException("Cotação não encontrada"));
        return CurrencyResponse.fromEntity(currency,
                environment + " - Source: Local DB (fallback: " + motivoFallback + ")");
    }

    /**
     * A PTAX informa quanto 1 unidade da moeda vale em BRL.
     * Taxa source->target = (BRL por source) / (BRL por target).
     * Retorna null se o BCB não tiver cotação para alguma das moedas.
     */
    private Double getRateFromBcb(String source, String target) {
        Double sourceEmReal = getCotacaoEmReal(source);
        if (sourceEmReal == null) return null;
        Double targetEmReal = getCotacaoEmReal(target);
        if (targetEmReal == null) return null;
        return sourceEmReal / targetEmReal;
    }

    private Double getCotacaoEmReal(String moeda) {
        if ("BRL".equals(moeda)) return 1.0;
        return bcbClientService.getCotacaoEmReal(moeda, dataCotacao);
    }
}
