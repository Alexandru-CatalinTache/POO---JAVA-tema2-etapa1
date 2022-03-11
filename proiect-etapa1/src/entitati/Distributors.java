package entitati;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

@JsonPropertyOrder({"id", "budget", "isBankrupt", "contracts"})
public class Distributors {
    public ArrayList<Consumers> clients = new ArrayList<>();
    public  ArrayList<Contract> contracts = new ArrayList<>();
    private int id;
    private int infrastructureCost;
    private int productionCost;
    private int contractLength;
    private int contractCost;
    private int budget;
    private boolean isBankrupt;
    private int profit;
    private int cheltuieli;

    public Distributors(final int id, final int infrastructureCost,
                        final int productionCost, final int contractLength, final int budget) {
        this.id = id;
        this.infrastructureCost = infrastructureCost;
        this.productionCost = productionCost;
        this.contractLength = contractLength;
        this.budget = budget;
        setIsBankrupt(false);
    }

    public Distributors() {
    }

    /**
     * eliminare consumator din lista de clienti
     * @param c
     * @param distributors
     */
    public final static void eliminareClient(final Consumers c,
                                             final ArrayList<Distributors> distributors) {
        int ok = 0;
        for (Distributors d : distributors) {
            Iterator<Consumers> it = d.clients.iterator();
            while (it.hasNext()) {
                Consumers client = it.next();
                if (client.getId() == c.getId()) {
                    it.remove();
                    ok = 1;
                    d.calculateProfit(d);
                    FactoryCalculatePretContrct f = new FactoryCalculatePretContrct();
                    f.pretContractCalculate(d);

                    break;
                }
            }
            if (ok == 1) {
                break;
            }
        }
    }

    public final ArrayList<Contract> getContracts() {
        return contracts;
    }

    public final void setContracts(final ArrayList<Contract> contracts) {

        this.contracts = contracts;
    }

    @JsonIgnore
    public final ArrayList<Consumers> getClients() {
        return clients;
    }

    public final void setClients(final ArrayList<Consumers> clients) {

        this.clients = clients;
    }

    @JsonIgnore
    public final int getCheltuieli() {
        return cheltuieli;
    }

    public final void setCheltuieli(final int cheltuieli) {
        this.cheltuieli = cheltuieli;
    }

    @JsonIgnore
    public final int getContractCost() {
        return contractCost;
    }

    public final void setContractCost(final int contractCost) {

        this.contractCost = contractCost;
    }

    public final boolean getIsBankrupt() {
        return isBankrupt;
    }

    public final void setIsBankrupt(final boolean isBankrupt) {

        this.isBankrupt = isBankrupt;
    }

    @JsonIgnore
    public final int getProfit() {
        return profit;
    }

    public  final void setProfit(final int profit) {
        this.profit = profit;
    }

    public final int getId() {
        return id;
    }

    public final void setId(final int id) {
        this.id = id;
    }

    @JsonIgnore
    public final int getInfrastructureCost() {
        return infrastructureCost;
    }

    public final void setInfrastructureCost(final int infrastructureCost) {
        this.infrastructureCost = infrastructureCost;
    }

    @JsonIgnore
    public final int getProductionCost() {
        return productionCost;
    }

    public final void setProductionCost(final int productionCost) {

        this.productionCost = productionCost;
    }

    @JsonIgnore
    public final int getContractLength() {
        return contractLength;
    }

    public final void setContractLength(final int contractLength) {

        this.contractLength = contractLength;
    }

    public final int getBudget() {
        return budget;
    }

    public final void setBudget(final int budget) {
        this.budget = budget;
    }

    @Override
    public String toString() {
        return "Distributors{"
                + "id=" + id
                + ", infrastructureCost=" + infrastructureCost
                + ", productionCost=" + productionCost
                + ", contractLength=" + contractLength
                + ", budget=" + budget
                + ", clients=" + clients
                + ",isBankrupt=" + isBankrupt
                + '}';
    }

    /**
     * calculare profit pentru distributor
     * @param d
     */
    public final void calculateProfit(final Distributors d) {
        if (Objects.equals(d.getIsBankrupt(), false)) {
            int profit;
            profit = (int) Math.round(Math.floor(0.2 * d.getProductionCost()));
            d.setProfit(profit);
        }
    }

    /**
     * calculare cheltuieli
     * @param d
     */
    public final void calculateCheltuieli(final Distributors d) {
        if (Objects.equals(d.getIsBankrupt(), false)) {
            int cheltuieli;
            cheltuieli = d.getInfrastructureCost()
                    + d.getProductionCost() * d.clients.size();
            d.setCheltuieli(cheltuieli);
        }

    }

    /**
     * adaugare taxa clientilor in bugetul distribuitorului
     * @param d
     */
    public final void adaugareProfitlaBuget(final Distributors d) {
        if (Objects.equals(d.getIsBankrupt(), false)) {
            Iterator<Consumers> it = d.clients.iterator();
            while (it.hasNext()) {
                Consumers client = it.next();
                if (client.getNumarLuniNeplatite() == 0) {
                    d.setBudget(d.getBudget() + (client.getMonthlyTax()));
                }
            }

        }
    }

    /**
     * verificare faliment distribuitor
     * @param d
     * @param distributors
     */
    public final void verificareFaliment(final Distributors d,
                                         final ArrayList<Distributors> distributors) {
        if ((d.getBudget() - d.getCheltuieli()) > 0) {
            d.setBudget(d.getBudget() - d.getCheltuieli());
        } else {
            d.setIsBankrupt(true);

            // pentru ca distributorul actual a dat faliment
            // aleg un alt contract pentru clientii acestuia

            Iterator<Consumers> it = d.clients.iterator();
            while (it.hasNext()) {
                Consumers c = it.next();
                it.next().setNumarLuniNeplatite(0);
                it.remove();
                it.next().alegereContract(c, distributors);

            }
        }
    }
}
