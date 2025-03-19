package si.janez.services.tax;

import jakarta.inject.Inject;
import si.janez.api.model.TaxRequest;
import si.janez.api.model.TaxResponse;
import si.janez.entities.Trader;

import java.math.BigDecimal;

public class WinningsRate implements  TaxCalculator{

    @Inject
    WinningsAmount winningsAmount;

    @Override
    public TaxResponse calculateTax(TaxRequest taxRequest, Trader trader) {
        var winninsBeforeTax = taxRequest.getPlayedAmount().multiply(taxRequest.getOdd().subtract(new BigDecimal(1)));
        trader.taxAmount = winninsBeforeTax.multiply(trader.taxRate);
        return winningsAmount.calculateTax(taxRequest, trader);
    }
}
