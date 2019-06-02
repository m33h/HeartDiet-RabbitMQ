
import heart.Callback;
import heart.Debug;
import heart.WorkingMemory;
import heart.alsvfd.SimpleNumeric;
import heart.exceptions.AttributeNotRegisteredException;
import heart.exceptions.NotInTheDomainException;
import heart.xtt.Attribute;

public class SimplifiedDietCreated implements Callback{
    public void execute(Attribute subject, WorkingMemory wmm) {
        System.out.println("Executing SimplifiedDietCreated for "+subject.getName());

        double kcal_demand = Double.valueOf(wmm.getAttributeValue("kcal_demand").toString());
        double simplified_diet_normal_calories = Double.valueOf(wmm.getAttributeValue("simplified_diet_normal_calories").toString());
        double simplified_diet_weekend_calories = Double.valueOf(wmm.getAttributeValue("simplified_diet_weekend_calories").toString());

        // caloric rate should visualize how average caloric demand is compatible with real calories demand
        double avgKcal = (simplified_diet_normal_calories * 5 + simplified_diet_weekend_calories * 2) / 7;

        // rate value is convergent to transformed sinus function
        double rateArg = (avgKcal * Math.PI) / (2 * kcal_demand);
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
