
import heart.Callback;
import heart.Debug;
import heart.WorkingMemory;
import heart.alsvfd.SimpleNumeric;
import heart.exceptions.AttributeNotRegisteredException;
import heart.exceptions.NotInTheDomainException;
import heart.xtt.Attribute;

public class DietDaySugarsRate implements Callback{
    public void execute(Attribute subject, WorkingMemory wmm) {
        System.out.println("Executing DietDaySugarsRate callback");

        String sex = wmm.getAttributeValue("sex").toString();
        String dietGoal = wmm.getAttributeValue("diet_goal").toString();
        Double age = Double.valueOf(wmm.getAttributeValue("age").toString());
        Double mass = Double.valueOf(wmm.getAttributeValue("mass").toString());
        Double height = Double.valueOf(wmm.getAttributeValue("height").toString());
        Double calories = Double.valueOf(wmm.getAttributeValue("calories").toString());
        Double activityFactor = Double.valueOf(wmm.getAttributeValue("activity_factor").toString());

        double ppm;
        if(sex == "male") {
            ppm = 10 * mass + 6.25 * height - 5 * age 
        }
        else {

        }

        double sugarsDemand;
        if(dietGoal.equals("reduce/1")){
            sugarsDemand = mass * 4.0;
        }
        else if(dietGoal.equals("maintain")) {
            sugarsDemand = mass * 5.0;
        }
        else {
            sugarsDemand = mass * 6.0;
        }

        double rateArg = (sugars * Math.PI) / (2 * sugarsDemand);
        double rateExpValue = 20;
        double rateValue = 100 * Math.pow(Math.sin(rateArg), rateExpValue);

        try {
            wmm.setAttributeValue(subject,new SimpleNumeric(rateValue),false);
        } catch (AttributeNotRegisteredException e) {
            Debug.debug("CALLBACK",
                    Debug.Level.WARNING,
                    "Callback failed to set value of"+subject.getName()+", as the attribute is not registered in the Working Memory.");
        } catch (NotInTheDomainException e) {
            Debug.debug("CALLBACK",
                    Debug.Level.WARNING,
                    "Callback failed to set value of"+subject.getName()+", as the obtained value was not in the domain of attribute.");
        }
    }
}
