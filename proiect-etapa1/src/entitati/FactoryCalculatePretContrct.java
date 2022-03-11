package entitati;

import java.util.Objects;

public class FactoryCalculatePretContrct {
    //utilizare Singleton
    private static final FactoryCalculatePretContrct FACTORY_CALCULATE_PRET_CONTRCT
            = new FactoryCalculatePretContrct();

    public FactoryCalculatePretContrct() {

    }

    public final static FactoryCalculatePretContrct getF() {
        return FACTORY_CALCULATE_PRET_CONTRCT;
    }

    /**
     * calculeaza pretul contractului in functie de numarul de clienti
     * @param  d
     */
    public final void pretContractCalculate(final Distributors d) {
        if (Objects.equals(d.getIsBankrupt(), false)) {
            int pretContract;
            if (d.clients.size() > 0) {
                pretContract = (int) Math.round(Math.floor(
                        d.getInfrastructureCost() / d.clients.size())
                        + d.getProductionCost() + d.getProfit());
                d.setContractCost(pretContract);
            }

            if (d.clients.size() == 0) {
                pretContract = d.getInfrastructureCost()
                        + d.getProductionCost() + d.getProfit();
                d.setContractCost(pretContract);
            }
        }
    }


}
