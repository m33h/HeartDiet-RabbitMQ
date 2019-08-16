
import heart.Callback;
import heart.Debug;
import heart.WorkingMemory;
import heart.alsvfd.SimpleNumeric;
import heart.exceptions.AttributeNotRegisteredException;
import heart.exceptions.NotInTheDomainException;
import heart.xtt.Attribute;

public class DietDayProteinsRate implements Callback{
    public void execute(Attribute subject, WorkingMemory wmm) {
        System.out.println("Executing DietDayProteinsRate callback");

        String dietGoal = wmm.getAttributeValue("diet_goal").toString();
        double mass = Double.valueOf(wmm.getAttributeValue("mass").toString());
        double proteins = Double.valueOf(wmm.getAttributeValue("proteins").toString());

        double proteinsDemand;
        if(dietGoal.equals("reduce/1")){
            proteinsDemand = mass * 1.2;
        }
        else if(dietGoal.equals("maintain/1")){
            proteinsDemand = mass * 1.0;
        }
        else {
            proteinsDemand = mass * 1.4;
        }

        double rateArg = (proteins * Math.PI) / (2 * proteinsDemand);
        double rateExpValue = 3;
        double rateValue = 100 * Math.pow(Math.sin(rateArg), rateExpValue);

        if(rateValue < 0) {
            rateValue = 0;
        }

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
