//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.math.F1D;
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.hipo4.data.*;
import org.jlab.groot.data.*;
import org.jlab.clas.physics.LorentzVector;
import org.jlab.clas.physics.PhysicsEvent;

public class Main {
    public static void main(String[] args) {
        // initiates other classes used in main class
        System.out.println("please work");
        Calculate calculate = new Calculate();
        System.out.println("is it calculate");
        Graphing graphing = new Graphing();

        //initiates all ArrayList that are not calling a function
        ArrayList<Double> massParam = new ArrayList<>();
        ArrayList<Double> trueGaussParam = new ArrayList<>();
        ArrayList<Double> mesonInvMass = new ArrayList<>();
        ArrayList<H1F> mesonHisto = new ArrayList<>();
        ArrayList<F1D> mesonFunctions = new ArrayList<>();
        ArrayList<ArrayList<?>> twoGraphs = new ArrayList<>();

        // Gives Parameters to massParam ArrayList
        massParam.add(350.0);//value for[amp]
        massParam.add(0.15); // value for [mean]
        massParam.add(0.1); //value for [sigma]
        massParam.add(100.0); // value for [A]
        massParam.add(.0); //value for [B]


        //initiates Hiporeader, reader, Bank ane events( one can change what file is being anylized by commenting or uncommenting read.open... code)
        HipoReader reader = new HipoReader(); // Create a reader obejct
        reader.open("/home/urbank/HipoFiles/rec_clas_020508.evio.00040.hipo"); // open a file
        //reader.open("/home/urbank/LabJavaCode/photon_tagged.hipo"); // open a file
        //

        SchemaFactory readerFactory = reader.getSchemaFactory();
        readerFactory.show();

        Bank particles = new Bank(readerFactory.getSchema("REC::Particle"));
        Event event = new Event();

        //sets reader index to zero for first event and initiats pion0 vetor
        reader.getEvent(event, 0);
        LorentzVector vL_pi0 = new LorentzVector();

        //--------
        // calls outside class methods: get PhysicsEvent, getTwoPhotonMass
        System.out.println("test 4, start main");
        ArrayList<PhysicsEvent> returnedPhysicsEvents = calculate.getPhysicsEvents(reader, event, particles);
        ArrayList<ArrayList<?>> comboList = calculate.getTwoPhotonMass(graphing, calculate, returnedPhysicsEvents, vL_pi0);

        //Converts ArrayList inside comboList to correct object defined ArrayList as its own object inside main function
        ArrayList<Double> invMass = new ArrayList<>();
        for (Object obj : comboList.get(0)) {
            if (obj instanceof Double) {
                invMass.add((Double) obj);
            }
        }
        ArrayList<H1F> histGraph = new ArrayList<>();
        for (Object obj : comboList.get(2)) {
            if (obj instanceof H1F) {
                histGraph.add((H1F) obj);
            }
        }
        ArrayList<PhysicsEvent> qualGammaEvents = new ArrayList<>();
        for (Object obj : comboList.get(1)) {
            if (obj instanceof PhysicsEvent) {
                qualGammaEvents.add((PhysicsEvent) obj);
            }
        }

        /*Calls mesonMass method and creates returned arrayList, converts ArrayList inside returned
        mesonComboList to their correct object defined ArrayList
        */
        ArrayList<ArrayList<?>> mesonComboList = calculate.mesonMass(graphing, qualGammaEvents, invMass);

        for (Object obj : mesonComboList.get(1)) {
            if (obj instanceof Double) {
                mesonInvMass.add((Double) obj);
            }
        }

        List<?> list = mesonComboList.get(2);
        Object obj = list.get(0);
        mesonHisto.add((H1F) obj);

        //Creates function variables through calling Graphing class methods
        F1D massType1 = Graphing.makePolyGauss("massType1", 0.5, .28, massParam);
        F1D massType2 = Graphing.makePolyGauss("massType2", 0.5, 0.28, massParam);

        // Creates Histogram object variables in main class from histGraph ArrayList
        H1F firstMass = histGraph.get(0);
        H1F secondMass = histGraph.get(1);


        DataFitter.fit(massType1, firstMass, "Q");
        DataFitter.fit(massType2, secondMass, "Q");
        firstMass.fit(massType1, "same");
        secondMass.fit(massType2, "same");
        trueGaussParam.add(massType1.getParameter(0));
        trueGaussParam.add(massType2.getParameter(1));
        trueGaussParam.add(massType2.getParameter(2));

        F1D trueGauss = Graphing.makeGauss("trueGauss",0.5, .28, trueGaussParam);
        mesonFunctions.add(trueGauss);

        System.out.println("First meson Mass " + mesonInvMass.get(0) + " ," + mesonInvMass.get(1) + " ," + mesonInvMass.get(2));
        histGraph.add(mesonHisto.get(0));

        graphing.makeCanvas("Meson graphs", 1600, 400, 4, 1, histGraph);
        graphing.makeCanvas("Meson function", 800, 400, 1, 1, mesonFunctions);
        //graphing.makeCanvas("Meason functions", 1600, 400, 1, 1, mesonFunctions);

    }
}