package si.janez.services.tax;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import si.janez.api.model.TaxRequest;
import si.janez.api.model.TaxResponse;
import si.janez.repositories.TraderRepository;

@ApplicationScoped
public class TaxService {

    @Inject
    TraderRepository traderRepository;

    @Inject
    GeneralRate generalRate;

    @Inject
    GeneralAmount generalAmount;
    @Inject
    WinningsRate winningsRate;
    @Inject
    WinningsAmount winningsAmount;

    public TaxResponse calculateTax(TaxRequest taxRequest) {

        var trader = traderRepository.findById(taxRequest.getTraderId());
        if (trader == null)
            throw new NotFoundException(String.format("Trader with ID %d not found", taxRequest.getTraderId()));

        return switch (trader.taxType) {
            case GENERAL_RATE -> generalRate.calculateTax(taxRequest, trader);
            case GENERAL_AMOUNT -> generalAmount.calculateTax(taxRequest, trader);
            case WINNINGS_RATE -> winningsRate.calculateTax(taxRequest, trader);
            case WINNINGS_AMOUNT -> winningsAmount.calculateTax(taxRequest, trader);
        };
    }
}
