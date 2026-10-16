package br.edu.atitus.currencyapi.dtos;

import java.util.List;

public record BcbResponse(
        List<BcbCotacao> value
) {
}
