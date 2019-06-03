
import heart.Callback;
import heart.Debug;
import heart.WorkingMemory;
import heart.alsvfd.SimpleNumeric;
import heart.exceptions.AttributeNotRegisteredException;
import heart.exceptions.NotInTheDomainException;
import heart.xtt.Attribute;

public class DietDayFatsRate implements Callback{
    public void execute(Attribute subject, WorkingMemory wmm) {
        System.out.println("Executing DietDayFatsRate callback");

        String dietGoal = wmm.getAttributeValue("diet_goal").toString();
        double mass = Double.valueOf(wmm.getAttributeValue("mass").toString());
        double fats = Double.valueOf(wmm.getAttributeValue("fats").toString());

        double fatsDemand;
        if(dietGoal.equals("reduce/1")){
            fatsDemand = mass * 1;
        }
        else if(dietGoal.equals("maintain/1")){
            fatsDemand = mass * 2.5;
        }
        else {
            fatsDemand = mass * 3;
        }

        double rateArg = (fats * Math.PI) / (2 * fatsDemand);
        double rateExpValue = 5;
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
