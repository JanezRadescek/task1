package si.janez.services.tax;

import jakarta.enterprise.context.ApplicationScoped;
import si.janez.api.model.TaxRequest;
import si.janez.api.model.TaxResponse;
import si.janez.entities.Trader;

import java.math.RoundingMode;

@ApplicationScoped
public class GeneralAmount implements TaxCalculator {
    @Override
    public TaxResponse calculateTax(TaxRequest taxRequest, Trader trader) {
        // Arbitrary decided for those tax calculation(rounding) rules.
        var totalBeforeTax = taxRequest.getPlayedAmount().multiply(taxRequest.getOdd());
        var totalBeforeTaxRounded = totalBeforeTax.setScale(2, RoundingMode.HALF_DOWN);
        var taxAmountRounded = trader.taxAmount.setScale(2, RoundingMode.HALF_UP);
        var totalAfterTaxRounded = totalBeforeTaxRounded.subtract(taxAmountRounded);
        var taxRate = trader.taxRate != null ? trader.taxRate : taxAmountRounded.divide(totalBeforeTaxRounded);
        return new TaxResponse()
                .possibleReturnAmount(totalBeforeTaxRounded)
                .possibleReturnAmountBefTax(totalBeforeTaxRounded)
                .possibleReturnAmountAfterTax(totalAfterTaxRounded)
                .taxRate(taxRate)
                .taxAmount(taxAmountRounded);
    }
}
