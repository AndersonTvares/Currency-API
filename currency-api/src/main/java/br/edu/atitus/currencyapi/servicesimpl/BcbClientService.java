package br.edu.atitus.currencyapi.servicesimpl;

import br.edu.atitus.currencyapi.clients.BcbClient;
import br.edu.atitus.currencyapi.dtos.BcbCotacao;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class BcbClientService {

    private final BcbClient bcbClient;

    public BcbClientService(BcbClient bcbClient) {
        this.bcbClient = bcbClient;
    }

    /**
     * Retorna a cotação de venda (em BRL) da moeda na data informada,
     * usando o último boletim do dia (normalmente o de Fechamento).
     * Retorna null quando o BCB responde sem cotação (moeda/data inexistente).
     * Falhas de comunicação são propagadas como exceção.
     * O "unless" impede que respostas sem cotação sejam armazenadas no cache.
     */
    @Cacheable(
            value = "bcb-api",
            key = "#moeda + '-' + #dataCotacao",
            unless = "#result == null"
    )
    @Retry(name = "bcb-api")
    @CircuitBreaker(name = "bcb-api")
    public Double getCotacaoEmReal(String moeda, String dataCotacao) {
        var response = bcbClient.getCotacaoMoedaDia(moeda, dataCotacao);
        if (response == null || response.value() == null || response.value().isEmpty())
            return null;
        BcbCotacao ultimoBoletim = response.value().getLast();
        return ultimoBoletim.cotacaoVenda();
    }
}
