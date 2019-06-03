
import heart.Callback;
import heart.Debug;
import heart.WorkingMemory;
import heart.alsvfd.SimpleNumeric;
import heart.exceptions.AttributeNotRegisteredException;
import heart.exceptions.NotInTheDomainException;
import heart.xtt.Attribute;

public class DietDaySugarRate implements Callback{
    public void execute(Attribute subject, WorkingMemory wmm) {
        System.out.println("Executing CalculatePatientBmi for "+subject.getName());

        String dietGoal = wmm.getAttributeValue("diet_goal").toString();
        double sugars = Double.valueOf(wmm.getAttributeValue("sugars").toString());
        double mass = Double.valueOf(wmm.getAttributeValue("mass").toString());

        double sugarsDemand;
        if(dietGoal.equals("reduce/1")){
            sugarsDemand = mass * 3.5;
        }
        else {
            sugarsDemand = mass * 5.0;
        }

        double rateArg = (sugars * Math.PI) / (2 * sugarsDemand);
        double rateExpValue = 10;
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
