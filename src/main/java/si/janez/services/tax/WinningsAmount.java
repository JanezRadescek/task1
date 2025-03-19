package si.janez.services.tax;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import si.janez.api.model.TaxRequest;
import si.janez.api.model.TaxResponse;
import si.janez.entities.Trader;

@ApplicationScoped
public class WinningsAmount implements TaxCalculator {

    @Inject
    GeneralAmount generalAmount;

    @Override
    public TaxResponse calculateTax(TaxRequest taxRequest, Trader trader) {
        // tax on winnings = (7.5 + -5) + -1
        // =Associativity = 7.5 + (-5 + -1)
        // =Commutativity = 7.5 + (-1 + -5)
        // =Associativity = (7.5 + -1) + -5
        // tax on total
        return generalAmount.calculateTax(taxRequest, trader);
        //also assuming error in task description
//        amount: 1EUR
//        2.5EUR - 1EUR = 1.5EUR => possible return amount is 1.5EUR
//        ->
//        amount: 1EUR
//        2.5EUR - 1EUR = 1.5EUR => possible return amount is 5€ + 1.5EUR
    }
}
