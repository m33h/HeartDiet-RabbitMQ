/**
 *
 *     Copyright 2013-15 by Szymon Bobek, Grzegorz J. Nalepa, Mateusz Ślażyński
 *
 *
 *     This file is part of HeaRTDroid.
 *     HeaRTDroid is a rule engine that is based on HeaRT inference engine,
 *     XTT2 representation and other concepts developed within the HeKatE project .
 *
 *     HeaRTDroid is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     HeaRTDroid is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with HeaRTDroid.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/



import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;

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
import heart.xtt.Attribute;
import heart.xtt.Decision;
import heart.xtt.Rule;
import heart.xtt.Table;
import heart.xtt.Type;
import heart.xtt.XTTModel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.io.FileWriter;
import java.io.IOException;

public class FoiInference {

    public static void main(String [] args) throws Exception{
        try {
            //Loading a file with a model
            XTTModel model = null;
            SourceFile modelFile = new SourceFile(args[0]);
            HMRParser parser = new HMRParser();

            //Parsing the file with the model
            parser.parse(modelFile);
            model = parser.getModel();

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
            StateElement suggestedDietGoalE = new StateElement();
            suggestedDietGoalE.setAttributeName("suggested_diet_goal");

            StateElement dietGoalDecisionRateE = new StateElement();
            dietGoalDecisionRateE.setAttributeName("diet_goal_decision_rate");

            StateElement kcalDemandE = new StateElement();
            kcalDemandE.setAttributeName("kcal_demand");

            StateElement currentDietKcalE = new StateElement();
            currentDietKcalE.setAttributeName("diet_goal_kcal");

            Object obj = new JSONParser().parse(new FileReader(args[1]));
            JSONObject jo = (JSONObject) obj;

            double bmi = (double) jo.get("bmi");
            StateElement patientBmiE = new StateElement();
            patientBmiE.setAttributeName("patient_bmi");
            patientBmiE.setValue(new SimpleNumeric(bmi));


            Object currentDayKcal = jo.get("current_day_kcal");
            double currentDayKcalDouble = new Double(currentDayKcal.toString());
            StateElement currentDayKcalE = new StateElement();
            currentDayKcalE.setAttributeName("current_day_kcal");
            currentDayKcalE.setValue(new SimpleNumeric(currentDayKcalDouble));


            //Creating a XTTState object that agregates all the StateElements
            State XTTstate = new State();
//			XTTstate.addStateElement(suggestedDietGoalE);
            XTTstate.addStateElement(patientBmiE);
//			XTTstate.addStateElement(currentDayKcalE);
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

                if(se.getValue() != null) {
                    if(se.getValue() instanceof SimpleSymbolic) {
                        jo.put(se.getAttributeName(), "" + se.getValue() + "");
                    }
                    else {
                        jo.put(se.getAttributeName(), se.getValue());
                    }
                }
            }

            try (FileWriter file = new FileWriter(args[1])) {

                file.write(jo.toJSONString());
                file.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        catch (BuilderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (NotInTheDomainException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (UnsupportedOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (ModelBuildingException e) {
            e.printStackTrace();
        }
        catch (ParsingSyntaxException e) {
            e.printStackTrace();
        }
    }
}
