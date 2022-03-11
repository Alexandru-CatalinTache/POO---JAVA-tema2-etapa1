import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import entitati.Consumers;
import entitati.Contract;
import entitati.Distributors;
import entitati.FactoryCalculatePretContrct;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import rounds.Round;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class Main {

    /**
     * citire parametrii, apelare functii si afisare output
     * @param args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {

        JSONParser jsonParser = new JSONParser();
        ArrayList<Consumers> consumers = new ArrayList<>();
        ArrayList<Distributors> distributors = new ArrayList<>();
        ArrayList<Round> rounds = new ArrayList<>();
        int numberOfTurns = 0;
        int countRoundNumber = 0;

        if (args[0] != null) {
            try {
                JSONObject jsonObject = (JSONObject) jsonParser.
                        parse(new FileReader(args[0]));
                numberOfTurns = ((Long) jsonObject.get("numberOfTurns")).intValue();


                JSONObject initialData = (JSONObject) jsonObject.get("initialData");
                JSONArray jsonConsumers = (JSONArray) initialData.get("consumers");
                JSONArray jsonDistributors = (JSONArray) initialData.get("distributors");

                if (jsonConsumers != null) {
                    for (Object jsonConsumer : jsonConsumers) {
                        consumers.add(new Consumers(
                                ((Long) ((JSONObject) jsonConsumer).get("id")).intValue(),
                                ((Long) ((JSONObject) jsonConsumer).
                                        get("initialBudget")).intValue(),
                                ((Long) ((JSONObject) jsonConsumer).get("monthlyIncome")).intValue()
                        ));
                    }
                } else {
                    System.out.println("NU EXISTA CONSUMATORI");
                }

                if (jsonDistributors != null) {
                    for (Object jsonDistributor : jsonDistributors) {
                        distributors.add(new Distributors(
                                ((Long) ((JSONObject) jsonDistributor).get("id")).intValue(),
                                ((Long) ((JSONObject) jsonDistributor).
                                        get("initialInfrastructureCost")).intValue(),
                                ((Long) ((JSONObject) jsonDistributor).
                                        get("initialProductionCost")).intValue(),
                                ((Long) ((JSONObject) jsonDistributor).
                                        get("contractLength")).intValue(),
                                ((Long) ((JSONObject) jsonDistributor).
                                        get("initialBudget")).intValue()
                        ));
                    }
                } else {
                    System.out.println("NU EXISTA DISTRIBUITORI");
                }

                JSONArray jsonMonthlyUpdates = (JSONArray) jsonObject.
                        get("monthlyUpdates");
                if (jsonMonthlyUpdates != null) {

                    for (Object jsonRound : jsonMonthlyUpdates) {

                        ArrayList<Consumers> newConsumers = new ArrayList<>();
                        HashMap<Integer, ArrayList<Integer>> distributorChanges = new HashMap<>();

                        JSONArray jsonNewConsumers = (JSONArray) ((JSONObject)
                                jsonRound).get("newConsumers");


                        if (((JSONObject) jsonRound).get("newConsumers") != null) {
                            for (Object jsonConsumerNew : jsonNewConsumers) {
                                if (jsonConsumerNew != null) {
                                    newConsumers.add(new Consumers(
                                            ((Long) ((JSONObject) jsonConsumerNew).
                                                    get("id")).intValue(),
                                            ((Long) ((JSONObject) jsonConsumerNew).
                                                    get("initialBudget")).intValue(),
                                            ((Long) ((JSONObject) jsonConsumerNew).
                                                    get("monthlyIncome")).intValue()
                                    ));
                                }
                            }


                        } else {
                            newConsumers = null;
                        }
                        JSONArray jsonChanges = (JSONArray) ((JSONObject) jsonRound).
                                get("costsChanges");
                        if (((JSONObject) jsonRound).get("costsChanges") != null) {
                            for (Object jsonCostChange : jsonChanges) {
                                ArrayList<Integer> listaCosturi = new ArrayList<>();
                                listaCosturi.add(
                                        ((Long) ((JSONObject) jsonCostChange).
                                                get("infrastructureCost")).intValue());
                                listaCosturi.add(
                                        ((Long) ((JSONObject) jsonCostChange).
                                                get("productionCost")).intValue());
                                distributorChanges.put(((Long)
                                                ((JSONObject) jsonCostChange).
                                                        get("id")).intValue(),
                                        listaCosturi);


                            }

                        } else {
                            distributorChanges = null;
                        }

                        rounds.add(new Round(
                                countRoundNumber,
                                newConsumers,
                                distributorChanges
                        ));

                        countRoundNumber++;

                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
        System.out.println(consumers);
        System.out.println(distributors);
        System.out.println(rounds);


        for (Distributors d : distributors) {
            d.calculateProfit(d);
            FactoryCalculatePretContrct f = new FactoryCalculatePretContrct();
            f.pretContractCalculate(d);

        }
        for (Consumers cons : consumers) {
            if (cons.getContractLength() == 0) {
                cons.alegereContract(cons, distributors);
            }
            cons.adaugareSalariuLaBuget(cons);
            cons.scadereTaxaDinBuget(cons, distributors);

        }

        for (Distributors d : distributors) {
            System.out.println(d.getBudget());
            System.out.println("Pretttt contract =  " + d.getContractCost());
            d.adaugareProfitlaBuget(d);
            System.out.println(d.getBudget());
            d.calculateCheltuieli(d);
            d.verificareFaliment(d, distributors);
            System.out.println(d.getBudget());
        }


        int runda_curenta;
        for (runda_curenta = 0; runda_curenta < numberOfTurns; runda_curenta++) {

            if (rounds.get(runda_curenta).getNewConsumers() != null) {
                consumers.addAll(rounds.get(runda_curenta).getNewConsumers());
            }
            if (rounds.get(runda_curenta).getDistributorChanges() != null) {
                for (Map.Entry newDistributor : rounds.get(runda_curenta)
                        .getDistributorChanges().entrySet()) {
                    int id = (int) newDistributor.getKey();
                    ArrayList<Integer> costuriNoi = (ArrayList<Integer>) newDistributor.getValue();
                    int newInfrastructureCost = costuriNoi.get(0);
                    int newProductionCost = costuriNoi.get(1);
                    for (Distributors d : distributors) {
                        if (d.getId() == id) {
                            d.setInfrastructureCost(newInfrastructureCost);
                            d.setProductionCost(newProductionCost);
                            d.calculateProfit(d);
                            System.out.println("Profit = " + d.getProfit());
                            FactoryCalculatePretContrct f = new FactoryCalculatePretContrct();
                            f.pretContractCalculate(d);
                            break;
                        }
                    }
                }
            }

            // recalcularea  datelor (profit si valoare contract) pentru distributori
            for (Distributors d : distributors) {
                System.out.println(d.getId());
                System.out.println(d.getBudget());

                System.out.println(d.getInfrastructureCost());
                System.out.println(d.getProductionCost());
                System.out.println("Aflare cost = " + d.getContractCost());

                d.adaugareProfitlaBuget(d);
                System.out.println("Dupa adaugare = " + d.getBudget());

                d.calculateCheltuieli(d);


                System.out.println(d.getCheltuieli());
                System.out.println("-------------------Size = " + d.clients.size());

            }

            // recalculare date(alegere contract, adaugare salariu, plata taxa) consumatori
            for (Consumers c : consumers) {
                //System.out.println(c.getId());
                //System.out.println(c.getBudget());

                if (c.getIsBankrupt() != true) {
                    c.adaugareSalariuLaBuget(c);
                    if ((c.getContractLength() != 0)
                            || (!Objects.equals(c.getIsBankrupt(), false))) {
                    } else {
                        c.alegereContract(c, distributors);
                    }
                    c.scadereTaxaDinBuget(c, distributors);
                } else {
                    for (Distributors d : distributors) {
                        for (int i = 0; i < d.clients.size(); i++) {
                            if (d.clients.get(i).getId() == c.getId()) {
                                d.clients.remove(c);
                            }
                        }
                    }
                }
            }

            // recalculare cheltuieli si verificare faliment pentru distribuitori

            for (Distributors d : distributors) {
                d.verificareFaliment(d, distributors);
                System.out.println("------------New size =" + d.clients.size());
                System.out.println(d.getBudget());
            }

        }


        for (Distributors d : distributors) {
            Iterator<Consumers> it = d.clients.iterator();
            while (it.hasNext()) {
                Consumers c = it.next();
                Contract contract = new Contract(c.getId(),
                        c.getMonthlyTax(), c.getContractLength());
                d.contracts.add(contract);
            }
        }


        Output output1 = new Output(consumers, distributors);


        FileWriter output = new FileWriter(args[1]);
        ObjectWriter jsonWriter = new ObjectMapper()
                .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false)
                .writer(new DefaultPrettyPrinter());

        jsonWriter.writeValue(output, output1);
        output.write('\n');
        output.close();


    }
}

