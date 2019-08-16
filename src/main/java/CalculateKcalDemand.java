
import heart.Callback;
import heart.Debug;
import heart.WorkingMemory;
import heart.alsvfd.SimpleNumeric;
import heart.exceptions.AttributeNotRegisteredException;
import heart.exceptions.NotInTheDomainException;
import heart.xtt.Attribute;

public class CalculateKcalDemand implements Callback{
    public void execute(Attribute subject, WorkingMemory wmm) {
        System.out.println("Executing CalculateKcalDemand callback");

        String sex = wmm.getAttributeValue("sex").toString();
        String dietGoal = wmm.getAttributeValue("diet_goal").toString();
        Double age = Double.valueOf(wmm.getAttributeValue("age").toString());
        Double mass = Double.valueOf(wmm.getAttributeValue("mass").toString());
        Double height = Double.valueOf(wmm.getAttributeValue("height").toString());
        Double activityFactor = Double.valueOf(wmm.getAttributeValue("activity_factor").toString());

        double ppm;
        double extraKcal;
        if(sex == "male") {
            ppm = 10 * mass + 6.25 * height - 5 * age + 5;
            extraKcal = 500;
        }
        else {
            ppm = 10 * mass + 6.25 * height - 5 * age - 161;
            extraKcal = 300;
        }

        double kcalDemand;
        double baseKcal = ppm * activityFactor;
        if(dietGoal.equals("reduce/1")){
            kcalDemand = baseKcal - 500;
        }
        else if(dietGoal.equals("put_on/1")) {
            kcalDemand = baseKcal + extraKcal;
        }
        else {
            kcalDemand = baseKcal;
        }

        try {
            wmm.setAttributeValue(subject,new SimpleNumeric(kcalDemand),false);
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
