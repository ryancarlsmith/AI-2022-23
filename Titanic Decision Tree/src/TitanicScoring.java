import java.util.ArrayList;
import java.util.List;

public class TitanicScoring {

    public static int pluralityAgeGivenSex(Titanic.Passenger p){
        List<Integer> passengerArrayList = new ArrayList<>();
        for (Titanic.Passenger passenger : TitanicTestingData.passengers) {
            if (passenger.sex().equals(p.sex())){
                int age = (int) passenger.age();
                passengerArrayList.add(age);
            }
        }

        Integer[] passengerArray = passengerArrayList.toArray(new Integer[0]);

        return mode(passengerArray);

    }

    public static Titanic.Class fareClassRegression(double fare){

        if(fare < 0) return Titanic.Class.THIRD;

        int pclass = (int) Math.round(-0.0090808629297*fare + 3);

        if(pclass==1) return Titanic.Class.FIRST;
        else if(pclass==2) return Titanic.Class.SECOND;
        else return Titanic.Class.THIRD;
    }

    public static int mode(Integer[] ints){
        int maxValue =0;
        int maxCount = 0;
        for (int i = 0; i < ints.length; ++i) {
            int count = 0;
            for (int j = 0; j < ints.length; ++j) {
                if (ints[j] == ints[i] && ints[j] != -1) ++count; //-1 check because the plurality is the missing data
            }
            if (count > maxCount) {
                maxCount = count;
                maxValue = ints[i];
            }
        }

        return maxValue;
    }

    public static boolean survived(Titanic.Passenger passenger) { //TO DO
        if (passenger.sex().equals(Titanic.Sex.MALE)) {

            //males
            int tempAge = -1;
            if (passenger.age() == -1){
                tempAge = pluralityAgeGivenSex(passenger);
            }
            if (passenger.age() >= 13.0 || (tempAge >= 13.0 && tempAge != -1)) {
                //old male

                if (passenger.pclass().equals(Titanic.Class.SECOND) || passenger.pclass().equals(Titanic.Class.THIRD)) {
                    //old poor male
                    return true; //died
                } else {
                    if (passenger.age() >= 36 || (tempAge >= 36 && tempAge != -1)) {
                        return true; //died
                    } else if (passenger.age() < 30 || (tempAge < 30 && tempAge != -1)){
                        return true; //died
                    }
                    else{
                        return false; //survived
                    }
                }
            } else {
                //male child
                if (passenger.siblings() >= 2) {
                    //male child with few siblings/spouses
                    return true; // died
                } else {
                    //male child with a lot of siblings
                    return false; // survived
                }
            }
        }
        else if (passenger.sex().equals(Titanic.Sex.FEMALE)){


            // Missing Data Variables
            Titanic.Class passengerClass = passenger.pclass();
            double passengerAge = passenger.age();
            int passengerParents = passenger.parents();
            Titanic.Port passengerPort = passenger.port();
            int passengerSiblings = passenger.siblings();


            if(passengerClass.equals(Titanic.Class.UNKNOWN)) passengerClass = fareClassRegression(passenger.fare()); // Runs linear regression in the case of missing class
            switch(passengerClass) {

                case FIRST -> {
                    // 79 out of 82 passengers survive here
                    return (int)(Math.random() * 82) < 79;
                }

                case SECOND -> {

                    if (passengerAge < 0 ) passengerAge = 28.36; // Average age of second class females

                    if(passengerAge <= 23) return true;

                    if(passengerParents < 0) passengerParents = 1; // Average parCh for second class females over 23

                    if(passengerParents >= 2) return true;

                    return (int)(Math.random()*41) < 36;

                }
                case THIRD -> {


                    if(passengerPort.equals(Titanic.Port.UNKNOWN)) passengerPort = Titanic.Port.SOUTHAMPTON;// Most popular port for third class females

                    switch(passengerPort) {

                        case QUEENSTOWN ->  {

                            if(passengerAge < 0) passengerAge = 21.05; // Average age of third class females from Queenstown

                            if(passengerAge >= 23) return false;

                            return (int)(Math.random()*7) < 5;

                        }

                        case CHERBOURG -> {

                            if(passengerAge < 0) passengerAge = 13.5; // Average age of third class females from Cherbourg;

                            if(passengerAge <= 14) return true;

                            return (int)(Math.random()*5) < 1;


                        }

                        case SOUTHAMPTON -> {

                            if(passengerSiblings < 0) passengerSiblings = 1; // Average sibSp for third class females from Southampton

                            switch (passengerSiblings) {

                                case 0 -> {

                                    if(passengerAge < 0) passengerAge = 25; // Average age for third class females from Southampton with no sibSp

                                    if(passenger.age() < 28) return (int)(Math.random()*22) < 22;

                                    return (int)(Math.random()*11) < 3;

                                }

                                case 1 -> {

                                    return (int)(Math.random()*23) < 8;

                                }

                                case 2,3,4,5,6,7,8,9,10 -> {

                                    return (int)(Math.random()*19) < 3;
                                }


                            }

                        }

                    }
                }
            }

        }
        else {
            //the unknowns
            System.out.println("HOPEFULLY NEVER HERE -----------------------------------------");
        }
        return false;
    }

    public static void main(String[] args) {
        int correct = 0;
        double total = 0;
        for (Titanic.Passenger passenger : TitanicTrainingData.passengers) {
            try {
                boolean survived = survived(passenger);

                System.out.println(passenger.id() + "," + survived);
                if(survived != passenger.survived()) correct++;
                total++;


            } catch (Exception e) {
                System.out.println(passenger.id() + ",null");
            }
        }

        System.out.println(" Percent Correct " + correct/total*100 + "%");
    }
}
