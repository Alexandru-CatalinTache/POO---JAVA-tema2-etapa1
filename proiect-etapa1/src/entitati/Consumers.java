package entitati;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;

@JsonPropertyOrder({"id", "isBankrupt", "budget"})
public class Consumers {
    private int id;
    private int budget;
    private int monthlyIncome;
    private int monthlyTax;
    private int contractLength;
    private boolean isBankrupt;
    private int numarLuniNeplatite;
    private int sumaRestanta;
    private Distributors exDistributor;

    public Consumers(final int id, final  int budget, final int monthlyIncome) {
        this.id = id;
        this.budget = budget;
        this.monthlyIncome = monthlyIncome;
        this.setIsBankrupt(false);
        this.setNumarLuniNeplatite(0);
        this.setSumaRestanta(0);

    }

    
    public final Distributors getExDistributor() {
        return exDistributor;
    }

    public final void setExDistributor(final Distributors exDistributor) {
        this.exDistributor = exDistributor;
    }

    
    public final int getSumaRestanta() {
        return sumaRestanta;
    }

    public final void setSumaRestanta(final int sumaRestanta) {
        this.sumaRestanta = sumaRestanta;
    }

    
    public final int getNumarLuniNeplatite() {
        return numarLuniNeplatite;
    }

    public final void setNumarLuniNeplatite(final int numarLuniNeplatite) {
        this.numarLuniNeplatite = numarLuniNeplatite;
    }

    
    public  final int getMonthlyTax() {
        return monthlyTax;
    }

    public final void setMonthlyTax(final int monthlyTax) {
        this.monthlyTax = monthlyTax;
    }

    
    public  final int getContractLength() {
        return contractLength;
    }

    public  final void setContractLength(final int contractLength) {
        this.contractLength = contractLength;
    }

    public  final boolean getIsBankrupt() {
        return isBankrupt;
    }

    public final void setIsBankrupt(final boolean isBankrupt) {
        this.isBankrupt = isBankrupt;
    }

    @Override
    public String toString() {
        return "Consumers{"
                + "id=" + id
                + ", budget=" + budget
                + ", monthlyIncome=" + monthlyIncome
                + ", isBankrupt='" + isBankrupt + '\''
                + '}';
    }

    public final int getId() {
        return id;
    }

    public final void setId(final int id) {
        this.id = id;
    }

    public final int getBudget() {
        return budget;
    }

    public final void setBudget(final int budget) {
        this.budget = budget;
    }

    
    public final int getMonthlyIncome() {
        return monthlyIncome;
    }

    public final void setMonthlyIncome(final int monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    /**
     * adaugare salariu
     * @param c
     */
    public final void adaugareSalariuLaBuget(final Consumers c) {
        c.setBudget(c.getBudget() + c.getMonthlyIncome());

    }

    /**
     * calculare buget dupa scaderea taxei si verificarea falimentului consumatorului
     * @param c
     * @param distributors
     */
    public final void scadereTaxaDinBuget(final Consumers c,
                                          final  ArrayList<Distributors> distributors) {
        int ok = 0;
        if (c.getContractLength() >= 1) {
            if (c.getNumarLuniNeplatite() == 0) {
                if ((c.getBudget() - c.getMonthlyTax()) > 0) {
                    c.setBudget(c.getBudget() - c.getMonthlyTax());
                    c.setContractLength(c.getContractLength() - 1);
                } else {
                    c.setNumarLuniNeplatite(1);
                    ok = 1;
                    for (Distributors d : distributors) {
                        Iterator<Consumers> it = d.clients.iterator();
                        while (it.hasNext()) {
                            Consumers client = it.next();
                            if (client.getId() == c.getId()) {
                                d.setBudget(d.getBudget() - c.getMonthlyTax());
                                c.setExDistributor(d);
                            }
                        }
                    }

                    c.setSumaRestanta(c.getMonthlyTax());
                }

            }
            if (ok != 1) {
                if (c.getNumarLuniNeplatite() == 1) {
                    if ((c.getBudget() - (int) (Math.round(Math.floor(1.2 * c.getSumaRestanta()))
                            + c.getMonthlyTax())) > 0) {
                        c.setBudget(c.getBudget() - (int) (Math.round(Math.floor(1.2
                                * c.getSumaRestanta()))
                                + c.getMonthlyTax()));
                        for (Distributors d : distributors) {
                            Iterator<Consumers> it = d.clients.iterator();
                            while (it.hasNext()) {
                                Consumers client = it.next();
                                if (client.getId() == c.getId()) {
                                    int bani_restanti = (int) (c.getSumaRestanta() * 1.2);
                                    d.setBudget(d.getBudget() + bani_restanti);
                                }
                            }
                        }
                        //c.getExDistributor().setBudget(c.getExDistributor().getBudget()
                        //  + (int) (1.2 * c.getSumaRestanta()));
                        c.setSumaRestanta(0);
                        c.setNumarLuniNeplatite(0);
                        c.setContractLength(c.getContractLength() - 1);

                    } else {
                        c.setNumarLuniNeplatite(2);
                        c.setIsBankrupt(true);
                        Distributors.eliminareClient(c, distributors);
                    }
                }

            }
        }
        //  cand se termina contractul, este ales unul nou
        if (c.getContractLength() == 0) {
            c.alegereContract(c, distributors);
        }
    }

    /**
     * alegere nou contract
     * @param c
     * @param distributors
     */
    public final void alegereContract(final Consumers c,
                                      final ArrayList<Distributors> distributors) {
        if (Objects.equals(c.getIsBankrupt(), false)) {
            ArrayList<Integer> pretContracte = new ArrayList<>();
            for (Distributors d : distributors) {
                if (Objects.equals(d.getIsBankrupt(), false)) {
                    pretContracte.add(d.getContractCost());
                }
            }
            //compar valorile contractelor din care are de ales consumatorul
            // si ordonez lista in orine descrescatoare
            Collections.sort(pretContracte, new Comparator<Integer>() {
                @Override
                public int compare(final Integer o1, final Integer o2) {
                    return o2 - o1;
                }
            });

            // acum pe ultima pozitie a listei cu contractele am contractul cel mai ieftin

            for (Distributors d : distributors) {
                if (pretContracte.size() > 0) {
                    if (d.getContractCost() == pretContracte.get(pretContracte.size() - 1)) {
                        c.setMonthlyTax(d.getContractCost());
                        c.setContractLength(d.getContractLength());

                        d.clients.add(c);
                        break;
                    }
                }
            }
        }
    }
}
