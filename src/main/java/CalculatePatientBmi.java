
import heart.Callback;
import heart.Debug;
import heart.WorkingMemory;
import heart.alsvfd.SimpleNumeric;
import heart.exceptions.AttributeNotRegisteredException;
import heart.exceptions.NotInTheDomainException;
import heart.xtt.Attribute;

public class CalculatePatientBmi implements Callback{
    public void execute(Attribute subject, WorkingMemory wmm) {
        System.out.println("Executing CalculatePatientBmi for "+subject.getName());

        try {
            double height = Double.valueOf(wmm.getAttributeValue("height").toString());
            double mass = Double.valueOf(wmm.getAttributeValue("mass").toString());
            double heightInMeters = height / 100.0;
            double bmi = mass / (heightInMeters * heightInMeters);

            wmm.setAttributeValue(subject,new SimpleNumeric(bmi),false);
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
