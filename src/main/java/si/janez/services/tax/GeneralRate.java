package si.janez.services.tax;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import si.janez.api.model.TaxRequest;
import si.janez.api.model.TaxResponse;
import si.janez.entities.Trader;

@ApplicationScoped
public class GeneralRate implements TaxCalculator {

    @Inject
    GeneralAmount generalAmount;

    @Override
    public TaxResponse calculateTax(TaxRequest taxRequest, Trader trader) {
        var totalBeforeTax = taxRequest.getPlayedAmount().multiply(taxRequest.getOdd());
        trader.taxAmount = totalBeforeTax.multiply(trader.taxRate);
        return generalAmount.calculateTax(taxRequest, trader);
    }
}
