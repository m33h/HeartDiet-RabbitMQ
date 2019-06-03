
import heart.Callback;
import heart.Debug;
import heart.WorkingMemory;
import heart.alsvfd.SimpleNumeric;
import heart.exceptions.AttributeNotRegisteredException;
import heart.exceptions.NotInTheDomainException;
import heart.xtt.Attribute;

public class DietDayCaloricRate implements Callback{
    public void execute(Attribute subject, WorkingMemory wmm) {
        System.out.println("Executing DietDayCaloricRate callback");

        double kcalDemand = Double.valueOf(wmm.getAttributeValue("kcal_demand").toString());
        double calories = Double.valueOf(wmm.getAttributeValue("calories").toString());

        double rateArg = (calories * Math.PI) / (2 * kcalDemand);
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
