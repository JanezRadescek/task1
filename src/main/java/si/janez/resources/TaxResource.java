package si.janez.resources;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import si.janez.api.TaxApi;
import si.janez.api.model.TaxRequest;
import si.janez.api.model.TaxResponse;
import si.janez.services.tax.TaxService;

public class TaxResource implements TaxApi {

    @Inject
    TaxService taxService;

    @Override
    public TaxResponse apiTaxPost(TaxRequest taxRequest) {
        Log.info("Request: " + taxRequest);
        //NOT DOIN mapping to domain model, because the mapping would be identity function
        var response = taxService.calculateTax(taxRequest);
        Log.info("Response: " + response);
        return response;
    }
}
