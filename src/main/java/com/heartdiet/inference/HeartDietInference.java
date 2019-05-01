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
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class HeartDietInference {
    private String modelFilepath;

    public HeartDietInference(String modelFilepath) {
        this.modelFilepath = modelFilepath;
    }

    public JSONObject call(JSONObject jsonMessage) {
        JSONObject results = jsonMessage;

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

            // Creating StateElements objects, one for each attribute
//			StateElement suggestedDietGoalE = new StateElement();
//			suggestedDietGoalE.setAttributeName("suggested_diet_goal");

//			StateElement dietGoalDecisionRateE = new StateElement();
//			dietGoalDecisionRateE.setAttributeName("diet_goal_decision_rate");

//			StateElement kcalDemandE = new StateElement();
//			kcalDemandE.setAttributeName("kcal_demand");

//			StateElement currentDietKcalE = new StateElement();
//			currentDietKcalE.setAttributeName("diet_goal_kcal");

            StateElement patientBmiE = new StateElement();
            patientBmiE.setAttributeName("patient_bmi");
            patientBmiE.setValue(new SimpleNumeric(16d));

            //Creating a XTTState object that agregates all the StateElements
            State XTTstate = new State();
//			XTTstate.addStateElement(suggestedDietGoalE);
            XTTstate.addStateElement(patientBmiE);
//			XTTstate.addStateElement(dietGoalDecisionRateE);
//			XTTstate.addStateElement(kcalDemandE);
//			XTTstate.addStateElement(currentDietKcalE);

            System.out.println("Printing current state before inference");
            State current = HeaRT.getWm().getCurrentState(model);

            for (StateElement se : current) {
                System.out.println("Attribute " + se.getAttributeName() + " = " + se.getValue());
            }

            try {
                Debug.debugLevel = Debug.Level.SILENT;
                HeaRT.fixedOrderInference(model, new String[]{"SuggestedDietGoal"},
                        new Configuration.Builder().setCsr(new ConflictSetFireAll())
                                .setInitialState(XTTstate)
                                .build());

            } catch(UnsupportedOperationException e){
                e.printStackTrace();
            } catch (AttributeNotRegisteredException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            System.out.println("Printing current state (after inference FOI)");
            current = HeaRT.getWm().getCurrentState(model);
            for (StateElement se : current) {
                System.err.println("Attribute " + se.getAttributeName() + " = " + se.getValue());
            }


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

        return results;
    }
}
