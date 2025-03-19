package si.janez.services.tax;

import si.janez.api.model.TaxRequest;
import si.janez.api.model.TaxResponse;
import si.janez.entities.Trader;

public interface TaxCalculator {
    TaxResponse calculateTax(TaxRequest taxRequest, Trader trader);
}
