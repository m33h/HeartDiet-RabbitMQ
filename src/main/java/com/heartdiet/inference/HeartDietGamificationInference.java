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

public class HeartDietGamificationInference {
    private String modelFilepath;

    public HeartDietGamificationInference(String modelFilepath) {
        this.modelFilepath = modelFilepath;
    }

    public JSONObject call(JSONObject jsonMessage) {
        JSONObject results = jsonMessage;

//        results.keySet().forEach(keyStr ->
//        {
//            Object keyvalue = results.get(keyStr);
//            System.out.println("key: "+ keyStr + " value: " + keyvalue);
//        });

        try {
            //Loading a file with a model
            XTTModel model = null;
            SourceFile heartDietModel = new SourceFile(this.modelFilepath);
            HMRParser parser = new HMRParser();

            //Parsing the file with the model
            parser.parse(heartDietModel);
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

            StateElement UserActivityE = new StateElement();
            UserActivityE.setAttributeName("user_activity");
            UserActivityE.setValue(new SimpleSymbolic("sign_in"));
            XTTstate.addStateElement(UserActivityE);

            StateElement CurrentActivityProfileE = new StateElement();
            CurrentActivityProfileE.setAttributeName("current_activity_profile");
            CurrentActivityProfileE.setValue(new SimpleSymbolic("partially_active"));
            XTTstate.addStateElement(CurrentActivityProfileE);

            StateElement DietPointsSumE = new StateElement();
            DietPointsSumE.setAttributeName("diet_points_sum");
            DietPointsSumE.setValue(new SimpleNumeric(0.0));
            XTTstate.addStateElement(DietPointsSumE);

            StateElement RankPointsSumE = new StateElement();
            RankPointsSumE.setAttributeName("rank_points_sum");
            RankPointsSumE.setValue(new SimpleNumeric(0.0));
            XTTstate.addStateElement(RankPointsSumE);

            StateElement LastActivityPointsE = new StateElement();
            LastActivityPointsE.setAttributeName("last_activity_points");
            LastActivityPointsE.setValue(new SimpleNumeric(0.0));
            XTTstate.addStateElement(LastActivityPointsE);

            StateElement PositiveActionE = new StateElement();
            PositiveActionE.setAttributeName("positive_action");
            PositiveActionE.setValue(new SimpleSymbolic("false"));
            XTTstate.addStateElement(PositiveActionE);

            StateElement ExtraDietPointsInfoE = new StateElement();
            ExtraDietPointsInfoE.setAttributeName("extra_diet_points_info");
            ExtraDietPointsInfoE.setValue(new SimpleSymbolic("null"));
            XTTstate.addStateElement(ExtraDietPointsInfoE);

            StateElement ExtraDietPointsE = new StateElement();
            ExtraDietPointsE.setAttributeName("extra_diet_points");
            ExtraDietPointsE.setValue(new SimpleNumeric(0.0));
            XTTstate.addStateElement(ExtraDietPointsE);

            StateElement GameLevelE = new StateElement();
            GameLevelE.setAttributeName("beginner");
            GameLevelE.setValue(new SimpleNumeric());
            XTTstate.addStateElement(GameLevelE);

            StateElement BadgeStatusE = new StateElement();
            BadgeStatusE.setAttributeName("not_achieved");
            BadgeStatusE.setValue(new SimpleNumeric());
            XTTstate.addStateElement(BadgeStatusE);

            StateElement PlayerBadgeE = new StateElement();
            PlayerBadgeE.setAttributeName("player_badge");
            PlayerBadgeE.setValue(new SimpleSymbolic("first_steps_in_diet"));
            XTTstate.addStateElement(PlayerBadgeE);

            System.out.println("Printing current state before inference");
            State current = HeaRT.getWm().getCurrentState(model);

            for (StateElement se : current) {
                System.out.println("Attribute " + se.getAttributeName() + " = " + se.getValue());
            }

            Debug.debugLevel = Debug.Level.WARNING;
            HeaRT.fixedOrderInference(model, new String[]{"CurrentActivity", "DietPlanEvaluation", "DietDayEvaluation", "SignInPoints", "PlayerLevel", "PlayerBadges"},
                    new Configuration.Builder().setCsr(new ConflictSetFireAll())
                            .setInitialState(XTTstate)
                            .build());

            System.out.println("Printing current state (after inference FOI)");
            current = HeaRT.getWm().getCurrentState(model);
            for (StateElement se : current) {
                System.err.println("Attribute " + se.getAttributeName() + " = " + se.getValue());
                results.put(se.getAttributeName(), se.getValue().toString());
            }

            System.out.println("=====\n=====\n=====\n\n\n");

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
