package si.janez.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Trader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;
    @Enumerated(EnumType.STRING)
    public TaxType taxType;
    public BigDecimal taxRate;
    public BigDecimal taxAmount;
}
