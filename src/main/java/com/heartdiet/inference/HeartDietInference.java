package com.heartdiet.inference;

import heart.*;
import heart.alsvfd.Formulae;
import heart.alsvfd.SimpleNumeric;
import heart.alsvfd.SimpleSymbolic;
import heart.alsvfd.Value;
import heart.alsvfd.expressions.ExpressionInterface;
import heart.exceptions.*;
import heart.parser.hmr.HMRParser;
import heart.parser.hmr.runtime.SourceFile;
import heart.uncertainty.ConflictSetFireAll;
import heart.xtt.*;
import org.json.simple.JSONObject;
import java.util.LinkedList;

public class HeartDietInference {
    private String modelFilepath;

    public HeartDietInference(String modelFilepath) {
        this.modelFilepath = modelFilepath;
    }

    public JSONObject call(JSONObject jsonMessage) {
        JSONObject results = jsonMessage;

        results.keySet().forEach(keyStr ->
        {
            Object keyvalue = results.get(keyStr);
            System.out.println("key: "+ keyStr + " value: " + keyvalue);
        });

        try {
            //Loading a file with a model
            XTTModel model = null;
            SourceFile hmr_threat_monitor = new SourceFile("src/models/heart-diet.hmr");
            HMRParser parser = new HMRParser();

            //Parsing the file with the model
            parser.parse(hmr_threat_monitor);
            model = parser.getModel();

            //Printing all the types within the model
            LinkedList<Type> types = model.getTypes();
            for (Type t : types) {
                System.out.println("Type id: " + t.getId());
                System.out.println("Type name: " + t.getName());
                System.out.println("Type base: " + t.getBase());
                System.out.println("Type length: " + t.getLength());
                System.out.println("Type scale: " + t.getPrecision());
                System.out.println("desc: " + t.getDescription());

                for (Value v : t.getDomain().getValues()) {
                    System.out.println("Value: " + v);
                }
                System.out.println("==========================");
            }

            //Printing all the attributes within the model
            LinkedList<Attribute> atts = model.getAttributes();
            for (Attribute att : atts) {
                System.out.println("Att Id: " + att.getId());
                System.out.println("Att name: " + att.getName());
                System.out.println("Att typeName: " + att.getTypeId());
                System.out.println("Att abbrev: " + att.getAbbreviation());
                System.out.println("Att comm: " + att.getComm());
                System.out.println("Att desc: " + att.getDescription());
                System.out.println("Att class: " + att.getXTTClass());
                System.out.println("==========================");
            }


            //Printing all the tables and rules within the model
            LinkedList<Table> tables = model.getTables();
            for(Table t : tables){
                System.out.println("Table id:"+t.getId());
                System.out.println("Table name:"+t.getName());
                LinkedList<Attribute> cond = t.getPrecondition();
                for(Attribute a : cond){
                    System.out.println("schm Cond: "+a.getName());
                }
                LinkedList<Attribute> concl = t.getConclusion();
                for(Attribute a : concl){
                    System.out.println("schm Conclusion: "+a.getName());
                }

                System.out.println("RULES FOR TABLE "+t.getName());

                for(Rule r : t.getRules()){
                    System.out.print("Rule id: "+r.getId()+ ":\n\tIF ");
                    for(Formulae f : r.getConditions()){
                        System.out.print(f.getLHS()+" "+f.getOp()+" "+f.getRHS()+", ");
                    }

                    System.out.println("THEN ");

                    for(Decision d: r.getDecisions()){
                        System.out.print("\t"+d.getAttribute().getName()+"is set to ");

                        ExpressionInterface e = d.getDecision();
                        System.out.print(e);
                    }
                    System.out.println();

                }
                System.out.println();
                System.out.println("=============================");
            }

            State XTTstate = new State();
            StateElement patientMassE = new StateElement();
            StateElement patientHeightE = new StateElement();
            StateElement ageE = new StateElement();
            StateElement patientKcalDemandE = new StateElement();
            StateElement patientFatsE = new StateElement();
            StateElement patientProteinsE = new StateElement();
            StateElement patientSugarsE = new StateElement();
            StateElement patientCurrentMealsCountE = new StateElement();
            StateElement activityFactorE = new StateElement();
            StateElement stateChangeTriggerE = new StateElement();
            StateElement sexE = new StateElement();
            StateElement dietGoalE = new StateElement();
            StateElement dietGoalDecisionE = new StateElement();

            patientMassE.setAttributeName("mass");
            String massString = String.valueOf(results.get("mass"));

            patientHeightE.setAttributeName("height");
            String heightString = String.valueOf(results.get("height"));

            ageE.setAttributeName("age");
            String ageString = String.valueOf(results.get("age"));

            patientKcalDemandE.setAttributeName("kcal_demand");
            String kcalDemandString = String.valueOf(results.get("kcal_demand"));

            patientFatsE.setAttributeName("fats");
            String fatsDemandString = String.valueOf(results.get("fats"));

            patientProteinsE.setAttributeName("proteins");
            String proteinsString = String.valueOf(results.get("proteins"));

            patientSugarsE.setAttributeName("sugars");
            String sugarsString = String.valueOf(results.get("sugars"));

            patientCurrentMealsCountE.setAttributeName("current_meals_count");
            String currentMealsCountString = String.valueOf(results.get("current_meals_count"));

            activityFactorE.setAttributeName("activity_factor");
            String activityFactorString = String.valueOf(results.get("activity_factor"));

            stateChangeTriggerE.setAttributeName("state_change_trigger");
            String stateChangeTriggerString = String.valueOf(results.get("state_change_trigger"));

            sexE.setAttributeName("sex");
            String sexString = String.valueOf(results.get("sex"));

            dietGoalE.setAttributeName("diet_goal");
            String dietGoalString = String.valueOf(results.get("current_diet_goal"));

            dietGoalDecisionE.setAttributeName("diet_goal_decision");

            patientKcalDemandE.setValue(new SimpleNumeric(Double.valueOf(kcalDemandString)));
            patientMassE.setValue(new SimpleNumeric(Double.valueOf(massString)));
            patientHeightE.setValue(new SimpleNumeric(Double.valueOf(heightString)));
            patientProteinsE.setValue(new SimpleNumeric(Double.valueOf(proteinsString)));
            ageE.setValue(new SimpleNumeric(Double.valueOf(ageString)));
            patientCurrentMealsCountE.setValue(new SimpleNumeric(Double.valueOf(currentMealsCountString)));
            activityFactorE.setValue(new SimpleNumeric(Double.valueOf(activityFactorString)));
            stateChangeTriggerE.setValue(new SimpleSymbolic(stateChangeTriggerString,1));
            sexE.setValue(new SimpleSymbolic(sexString,1));
            dietGoalE.setValue(new SimpleSymbolic(dietGoalString,1));
            patientSugarsE.setValue(new SimpleNumeric(Double.valueOf(sugarsString)));
            patientFatsE.setValue(new SimpleNumeric(Double.valueOf(fatsDemandString)));


            XTTstate.addStateElement(patientKcalDemandE);
            XTTstate.addStateElement(patientMassE);
            XTTstate.addStateElement(patientHeightE);
            XTTstate.addStateElement(patientProteinsE);
            XTTstate.addStateElement(ageE);
            XTTstate.addStateElement(patientCurrentMealsCountE);
            XTTstate.addStateElement(activityFactorE);
            XTTstate.addStateElement(stateChangeTriggerE);
            XTTstate.addStateElement(sexE);
            XTTstate.addStateElement(dietGoalE);
            XTTstate.addStateElement(patientSugarsE);
            XTTstate.addStateElement(patientFatsE);

            System.out.println("DD1");
            System.out.println("Printing current state before inference");
            State current = HeaRT.getWm().getCurrentState(model);

            for (StateElement se : current) {
                System.out.println("Attribute " + se.getAttributeName() + " = " + se.getValue());
            }

            System.out.println("DD2");
            Debug.debugLevel = Debug.Level.WARNING;
            HeaRT.fixedOrderInference(model, new String[]{"DietGoalDecision", "CurrentMealsCountInfo"},
                    new Configuration.Builder().setCsr(new ConflictSetFireAll())
                            .setInitialState(XTTstate)
                            .build());

            System.out.println("Printing current state (after inference FOI)");
            current = HeaRT.getWm().getCurrentState(model);
            for (StateElement se : current) {
                System.err.println("Attribute " + se.getAttributeName() + " = " + se.getValue());
            }
            System.out.println("DD3");


        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
        catch (BuilderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (NotInTheDomainException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (ModelBuildingException e) {
            e.printStackTrace();
        }
        catch (ParsingSyntaxException e) {
            e.printStackTrace();
        }
        catch (AttributeNotRegisteredException e1) {
           e1.printStackTrace();
        }

        return results;
    }
}
