package si.janez.resources;

import io.quarkus.logging.Log;
import si.janez.api.TaxApi;
import si.janez.api.model.TaxRequest;
import si.janez.api.model.TaxResponse;

import java.math.BigDecimal;

public class TaxResource implements TaxApi {
    @Override
    public TaxResponse apiTaxPost(TaxRequest taxRequest) {
        Log.info("apiTaxPost");
        Log.info("taxRequest: " + taxRequest);
        return new TaxResponse()
                .possibleReturnAmount(new BigDecimal(42))
                .possibleReturnAmountBefTax(new BigDecimal(42))
                .possibleReturnAmountAfterTax(new BigDecimal(42))
                .taxRate(new BigDecimal(50))
                .taxAmount(new BigDecimal(3));
    }
}
